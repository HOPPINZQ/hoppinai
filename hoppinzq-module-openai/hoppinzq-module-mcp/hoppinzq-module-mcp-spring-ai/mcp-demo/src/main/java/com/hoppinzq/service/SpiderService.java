package com.hoppinzq.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hoppinzq.context.WebMessageContext;
import com.hoppinzq.util.CSDNProcessor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;

import java.util.Date;

/**
 * 这个直接就可以跑，我用上下文来获取爬取的数据，注意一下
 */
@Service
public class SpiderService {

    @Tool(name = "get_web_data", description = "爬取csdn上的数据，到本地文件内")
    public String crawlerWeb(@ToolParam(required = true, description = "网页链接") String link,
                             @ToolParam(required = true, description = "本地文件路径") String path) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        response.put("now", new Date().toString());
        WebMessageContext.enter(response);
        try {
            Spider.create(new CSDNProcessor()).addUrl(link)
                    .addPipeline(new JsonFilePipeline(path))
                    .thread(1).run();
        } catch (Exception e) {
            response.put("error", e.getMessage());
        } finally {
            WebMessageContext.exit();
        }
        return response.toString();
    }
}

