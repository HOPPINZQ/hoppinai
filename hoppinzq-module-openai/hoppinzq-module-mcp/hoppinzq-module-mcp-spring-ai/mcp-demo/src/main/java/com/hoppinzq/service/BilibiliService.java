package com.hoppinzq.service;

import com.alibaba.fastjson.JSONObject;
import com.hoppinzq.service.cmd.CmdUtils;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 需要you-get和ffmpeg的环境
 * 有疑问、安装问题或者想要了解爬虫是如何工作的，请联系我微信：zhangqiff19
 */
@Service
public class BilibiliService {

    private static final String YOU_GET_CMD = "you-get ";

    private static JSONObject parseProgressInfo(String str) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("processRate", str.substring(0, str.indexOf("%") + 1).trim());
        jsonObject.put("processNum", str.substring(str.indexOf("(") + 1, str.indexOf(")")).trim());
        jsonObject.put("noIndex", str.substring(str.indexOf("[") + 1, str.indexOf("]")).trim());
        jsonObject.put("processV", str.substring(str.indexOf("]") + 1).trim());
        return jsonObject;
    }

    @Tool(name = "get_video_info", description = "获取链接里的视频信息和下载地址")
    public String getVideoInfo(@ToolParam(description = "视频链接") String url) {
        return CmdUtils.startProcess2(YOU_GET_CMD, "--info", url);
    }

    @Tool(name = "get_video_json", description = "获取视频信息的json")
    public JSONObject getVideoJson(@ToolParam(description = "视频链接") String url) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(YOU_GET_CMD + " --json " + url);
        String line = null;
        StringBuilder sb = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        String msg = sb.toString();
        if (msg.length() > msg.lastIndexOf("}") + 1) {
            msg = msg.substring(0, msg.lastIndexOf("}") + 1);
        }
        return JSONObject.parseObject(msg);
    }

    @Tool(name = "get_video_download_url", description = "获取视频的下载地址")
    public String getVideoDownloadUrl(@ToolParam(description = "视频链接") String url) {
        return CmdUtils.startProcess2(YOU_GET_CMD, "--url", url);
    }

    @Tool(name = "download_video", description = "下载视频")
    public String downloadVideo(@ToolParam(description = "视频链接") String url,
                                @ToolParam(description = "视频格式") String type,
                                @ToolParam(description = "视频名称") String videoName) {
        JSONObject jsonObject = new JSONObject();
        String youget = YOU_GET_CMD;
        if (type != null) {
            youget += " --format=" + type;
        }
        String filePath;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            /**
             * Windows系统的下载路径，我直接写死了
             */
            filePath = "D:\\video";
        } else {
            filePath = "/usr/myproject/video/";
        }
        youget += " -o " + filePath;
        youget += " -O " + videoName;
        youget += " -f " + url;

        jsonObject.put("url", url);
        jsonObject.put("type", type);
        jsonObject.put("state", "0");
        jsonObject.put("youget", youget);
        jsonObject.put("videoName", videoName);
        try {
            pa(youget);
            // 爬取成功

        } catch (Exception e) {
            jsonObject.put("error", "下载视频失败:" + e.getMessage());
            e.printStackTrace();
        }
        return jsonObject.toJSONString();
    }

    private void pa(String youget) throws Exception {
        String line = null;
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(youget);
        BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
        BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));
        while ((line = bufferedReader1.readLine()) != null) {
            if (line.indexOf("%") == 4) {
                System.out.println(parseProgressInfo(line).toJSONString());
            } else {
                if (line.indexOf("Merging video parts") != -1) {
                    System.out.println("合并文件中。。。");
                } else if (line.indexOf(".cmt.xml") != -1) {
                    System.out.println("爬取弹幕文件中。。。");
                } else {
                    System.out.println(line);
                }
            }
            System.out.println(line);
        }
        while ((line = bufferedReader2.readLine()) != null) {
            System.err.println(line);
            if (line.indexOf("already exists") != -1) {
                process.destroy();
                throw new RuntimeException("文件已存在，将直接上传！");
            } else if (line.indexOf("This is a multipart video") != -1) {
                process.destroy();
                throw new RuntimeException("该链接中有多个视频！暂时不允许爬取多个！");
            } else if (line.indexOf("You will need login cookies") != -1) {
                throw new RuntimeException("你或许需要登录后的cookie，来获取最佳体验！");
            } else {
                process.destroy();
                throw new RuntimeException("爬取失败！");
            }
        }
        process.destroy();
    }

}

