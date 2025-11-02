package com.hoppinzq.function.zq.music;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.common.collect.Lists;
import com.hoppinzq.function.zq.constants.AiFunctionCallResponse;
import com.hoppinzq.function.zq.constants.FunctionCallCommon;
import com.hoppinzq.model.openai.embedding.Embedding;
import com.hoppinzq.model.openai.embedding.EmbeddingRequest;
import com.hoppinzq.model.openai.embedding.EmbeddingResult;
import com.hoppinzq.openai.service.OpenAiService;
import com.hoppinzq.service.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static cn.hutool.core.util.ClassLoaderUtil.getClassLoader;

public class MusicFunctionCall extends AiFunctionCallResponse {
    private String musicName;
    private String musicPath;
    private String musicType;
    private String musicOpenType;

    public MusicFunctionCall(String musicName, OpenTypeEnum musicOpenType) {
        this.musicName = musicName;
        this.musicOpenType = musicOpenType.name();
        List<MusicFileInfo> musicFileList = new ArrayList<>();
        if (musicOpenType == OpenTypeEnum.WYY || musicOpenType == OpenTypeEnum.WINDOWS) {
            String musicListByCache = null;
            try {
                InputStream resource = getClassLoader().getResourceAsStream("music.txt");
                musicListByCache = IOUtils.toString(resource, StandardCharsets.UTF_8);
            } catch (Exception ignored) {
            }
            if (StringUtil.isEmpty(musicListByCache)) {
                String musicPath = FunctionCallCommon.music_path;
                List<File> fileList = getByCommonsIO(musicPath);
                List<String> musicNameList = new ArrayList<>();
                musicNameList.add(musicName);
                for (File file : fileList) {
                    String name = file.getName();
                    musicNameList.add(name);
                    musicFileList.add(MusicFileInfo.builder()
                            .musicName(name)
                            .musicPath(file.getAbsolutePath())
                            .musicType(name.substring(name.lastIndexOf(".") + 1))
                            .similarity(0.0)
                            .embeddings(new ArrayList<>())
                            .build());
                }
                List<Embedding> data = getEmbedding(musicNameList).getData();
                List<Double> inputEmbedding = data.get(0).getEmbedding();
                for (int i = 1; i < data.size(); i++) {
                    Embedding embedding = data.get(i);
                    List<Double> embeddingList = embedding.getEmbedding();
                    double similarity = cosineSimilarity(inputEmbedding.stream().mapToDouble(Double::doubleValue).toArray(),
                            embeddingList.stream().mapToDouble(Double::doubleValue).toArray());
                    musicFileList.get(i - 1).setSimilarity(similarity);
                    musicFileList.get(i - 1).setEmbeddings(embeddingList);
                }
                try (FileOutputStream fos = new FileOutputStream(getClassLoader().getResource("music.txt").getFile())) {
                    fos.write(JSON.toJSONString(musicFileList).getBytes(StandardCharsets.UTF_8));
                } catch (IOException ignored) {
                }
            } else {
                musicFileList = JSON.parseArray(musicListByCache, MusicFileInfo.class);
                List<String> musicNameList = Lists.newArrayList(musicName);
                EmbeddingResult embedding = getEmbedding(musicNameList);
                List<Double> inputEmbedding = embedding.getData().get(0).getEmbedding();
                // 读取music.txt
                for (MusicFileInfo musicFileInfo : musicFileList) {
                    List<Double> embeddingList = musicFileInfo.getEmbeddings();
                    double similarity = cosineSimilarity(inputEmbedding.stream().mapToDouble(Double::doubleValue).toArray(),
                            embeddingList.stream().mapToDouble(Double::doubleValue).toArray());
                    musicFileInfo.setSimilarity(similarity);
                }
            }
            // 遍历musicFileList，使之对similarity从大到小排序
            musicFileList.sort(new Comparator<MusicFileInfo>() {
                @Override
                public int compare(MusicFileInfo o1, MusicFileInfo o2) {
                    return o2.getSimilarity().compareTo(o1.getSimilarity());
                }
            });
            MusicFileInfo result = musicFileList.get(0);
            this.musicName = result.getMusicName();
            this.musicPath = result.getMusicPath();
            this.musicType = result.getMusicType();
            if (musicOpenType == OpenTypeEnum.WYY || "ncm".equals(this.musicType)) {
                try {
                    StringBuilder stringBuilder = new StringBuilder("cmd /c start ");
                    stringBuilder.append("\"\"").append(" ");
                    stringBuilder.append("\"").append("E:\\LenovoSoftstore\\Install\\wangyiyunyinyue\\cloudmusic.exe").append("\"").append(" ");
                    stringBuilder.append("\"").append(this.musicPath).append("\"");
                    Runtime.getRuntime().exec(stringBuilder.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Runtime.getRuntime().exec("cmd /c start " + this.musicPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            success("找到音乐");
        } else {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("windows")) {
                try {
                    Runtime.getRuntime().exec("cmd /c start " + "https://hoppinzq.com/wukong/index.html?s=" + musicName);
                    success("网页打开音乐成功");
                } catch (Exception e) {
                    e.printStackTrace();
                    fail("网页打开音乐失败");
                }
            } else {
                fail("不支持的操作系统");
            }
        }
    }

    private static EmbeddingResult getEmbedding(List<String> musicNameList) {
        OpenAiService openAiService = FunctionCallCommon.serviceThreadLocal.get();
        if (openAiService == null) {
            openAiService = new OpenAiService(FunctionCallCommon.apiKey, Duration.ofSeconds(60), FunctionCallCommon.openaiProxy);
        }
        EmbeddingResult embeddings = openAiService.createEmbeddings(EmbeddingRequest.builder()
                .model(FunctionCallCommon.embedding_model)
                .input(musicNameList)
                .build());
        return embeddings;
    }

    private static List<File> getByCommonsIO(String directoryPath) {
        List<File> fileList = new ArrayList<>();
        FileUtils.listFiles(
                new File(directoryPath),
                new String[]{"mp3", "ncm"}, // mp3正常文件，ncm网易云vip音乐
                true
        ).forEach(fileList::add);
        return fileList;
    }

    private static double cosineSimilarity(double[] vector1, double[] vector2) {
        RealVector realVector1 = new ArrayRealVector(vector1);
        RealVector realVector2 = new ArrayRealVector(vector2);
        double dotProduct = realVector1.dotProduct(realVector2);
        double norm = realVector1.getNorm() * realVector2.getNorm();
        return dotProduct / norm;
    }

    public enum OpenTypeEnum {
        WYY, WINDOWS, NETWORK
    }

    public static class MusicFunctionCallRequest {
        @JsonPropertyDescription("音乐名")
        @JsonProperty(required = true)
        public String musicName;

        @JsonPropertyDescription("打开方式，默认直接在windows系统打开，wyy为网易云打开,network为在网上搜索")
        public OpenTypeEnum openTypeEnum = OpenTypeEnum.WINDOWS;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class MusicFileInfo {
        public String musicName;
        public String musicPath;
        public String musicType;
        public Double similarity; // 相似度 0~1,越靠近1越相似
        public List<Double> embeddings;
    }

}
