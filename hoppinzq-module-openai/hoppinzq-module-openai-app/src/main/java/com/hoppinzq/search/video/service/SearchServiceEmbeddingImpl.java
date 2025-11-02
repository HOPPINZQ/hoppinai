package com.hoppinzq.search.video.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.hoppinzq.function.zq.constants.FunctionCallCommon;
import com.hoppinzq.model.openai.embedding.EmbeddingRequest;
import com.hoppinzq.model.openai.embedding.EmbeddingResult;
import com.hoppinzq.openai.service.OpenAiService;
import com.hoppinzq.query.LambdaQueryWrapperX;
import com.hoppinzq.search.SearchService;
import com.hoppinzq.search.embedding.EmbeddingUtils;
import com.hoppinzq.search.embedding.ThreadPoolManager;
import com.hoppinzq.search.embedding.dao.EmbeddingMapper;
import com.hoppinzq.search.embedding.po.EmbeddingPO;
import com.hoppinzq.search.video.dao.MyVideoMapper;
import com.hoppinzq.search.video.po.VideoPO;
import com.hoppinzq.service.annotation.ApiMapping;
import com.hoppinzq.service.annotation.ApiServiceMapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ApiServiceMapping(title = "向量", description = "向量服务", roleType = ApiServiceMapping.RoleType.NO_RIGHT)
public class SearchServiceEmbeddingImpl implements SearchService {

    @Autowired
    private MyVideoMapper myVideoMapper;
    @Autowired
    private EmbeddingMapper embeddingMapper;

    @Override
    @ApiMapping(value = "initVideo2", title = "初始化", description = "初始化视频")
    public void init() {
        List<VideoPO> VideoPOS = myVideoMapper.selectList();
        try {
            List<String> docList = new ArrayList<>();
            List<EmbeddingPO> embeddingPOS = new ArrayList<>();
            for (VideoPO VideoPO : VideoPOS) {
                docList.add(JSON.toJSONString(VideoPO));
            }
            OpenAiService service = new OpenAiService(FunctionCallCommon.apiKey, Duration.ofSeconds(60), FunctionCallCommon.openaiProxy);
            for (int i = 0; i < VideoPOS.size(); i++) {
                EmbeddingResult embeddings = service.createEmbeddings(EmbeddingRequest.builder().model(FunctionCallCommon.embedding_model)
                        .input(Collections.singletonList(docList.get(i))).build());
                EmbeddingPO blog = EmbeddingPO.builder()
                        .embedding(String.valueOf(embeddings.getData().get(0).getEmbedding()))
                        .tableId(String.valueOf(VideoPOS.get(i).getVideoId()))
                        .tableName("video")
                        .build();
                embeddingPOS.add(blog);
                embeddingMapper.insert(blog);
            }
//            embeddingMapper.insertBatch(embeddingPOS);
        } catch (Exception ex) {
            throw new RuntimeException("初始化视频失败:" + ex);
        }
    }

    /**
     * 搜索视频
     *
     * @param searchContent
     * @return
     */
    @Override
    @ApiMapping(value = "queryVideo2", title = "搜索视频", description = "搜索视频")
    public List<VideoPO> query(String searchContent) {
        List<EmbeddingPO> embeddingPOS = embeddingMapper.selectList(new LambdaQueryWrapperX<EmbeddingPO>().eq(EmbeddingPO::getTableName, "video"));
        OpenAiService service = new OpenAiService(FunctionCallCommon.apiKey, Duration.ofSeconds(60), FunctionCallCommon.openaiProxy);
        EmbeddingResult embeddings = service.createEmbeddings(EmbeddingRequest.builder().model(FunctionCallCommon.embedding_model)
                .input(Collections.singletonList(searchContent)).build());
        List<Double> inputEmbedding = embeddings.getData().get(0).getEmbedding();
        List<EmbeddingPO> dataSimilarity = new ArrayList<>();
        ThreadPoolManager.createThreadPool();
        List<List<EmbeddingPO>> divideList = EmbeddingUtils.divideList(embeddingPOS, 10);
        for (int i = 0; i < divideList.size(); i++) {
            Callable callableR = new SimlarityCall(divideList.get(i), EmbeddingUtils.getDoubleArray(inputEmbedding));
            Future<Object> future = ThreadPoolManager.submitTask(callableR);
            try {
                List<EmbeddingPO> embeddingResult = (List<EmbeddingPO>) future.get(10, TimeUnit.SECONDS);
                dataSimilarity.addAll(embeddingResult);
            } catch (TimeoutException e) {
                future.cancel(true);
                dataSimilarity.addAll(Collections.emptyList());
            } catch (Exception ex) {
                dataSimilarity.addAll(Collections.emptyList());
            }
        }
        ThreadPoolManager.destroyThreadPool();
        Comparator<EmbeddingPO> comparator = new Comparator<EmbeddingPO>() {
            @Override
            public int compare(EmbeddingPO o1, EmbeddingPO o2) {
                try {
                    double embedding1 = o1.getSimilarity();
                    double embedding2 = o2.getSimilarity();
                    if (embedding1 > embedding2) {
                        return -1;
                    } else if (embedding1 < embedding2) {
                        return 1;
                    } else {
                        return 0;
                    }
                } catch (JSONException e) {
                    return 0;
                }
            }
        };
        Collections.sort(dataSimilarity, comparator);
        if (dataSimilarity.size() > 10) {
            dataSimilarity = dataSimilarity.subList(0, 10);
        }
        List<Long> ids = new ArrayList<>();
        for (EmbeddingPO embeddingPO : dataSimilarity) {
            ids.add(Long.parseLong((embeddingPO.getTableId())));
        }
        List<VideoPO> VideoPOS = myVideoMapper.selectBatchIds(ids);
        return VideoPOS;
    }

}

class SimlarityCall implements Callable<List<EmbeddingPO>> {
    List<EmbeddingPO> embeddingPOS = null;
    double[] embedding = null;

    public SimlarityCall(List<EmbeddingPO> embeddingPOS, double[] embedding) {
        this.embeddingPOS = embeddingPOS;
        this.embedding = embedding;
    }

    @Override
    public List<EmbeddingPO> call() throws Exception {
        for (int i = 0; i < embeddingPOS.size(); i++) {
            EmbeddingPO embeddingPO = embeddingPOS.get(i);
            JSONArray embeddingArrays = JSON.parseArray(embeddingPO.getEmbedding());
            embeddingPO.setSimilarity(EmbeddingUtils.cosineSimilarity(embedding, EmbeddingUtils.getDoubleArray(embeddingArrays)));
        }
        return embeddingPOS;
    }
}
