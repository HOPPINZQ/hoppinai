package com.hoppinzq.util;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hoppinzq.context.WebMessageContext;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zq
 */
@Component
public class CSDNProcessor implements PageProcessor {

    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    @Override
    public void process(Page page) {
        Selectable links = page.getHtml().links();
        String blogTitle = page.getHtml().xpath("//*[@id=\"articleContentId\"]/html()").toString();
        String blogText = page.getHtml().xpath("//*[@id=\"content_views\"]/tidyText()").toString();
        String blogHtml = page.getHtml().xpath("//*[@id=\"content_views\"]/html()").toString();
        String blogAuthor = page.getHtml().xpath("//*[@id=\"mainBox\"]/main/div[1]/div[1]/div/div[2]/div[1]/div/a[1]/html()").toString();
        String imageSrc = page.getHtml().xpath("//*[@id=\"mainBox\"]/main/div[1]/div[1]/div/div[2]/div[1]/img/@src").toString();
        List<String> blogClass = new ArrayList<String>();
        int classIndex = 2;
        while (true) {
            String xpathClassA = "//*[@id=\"mainBox\"]/main/div[1]/div[1]/div/div[2]/div[2]/div/a[" + classIndex + "]/text()";
            String blogClassText = page.getHtml().xpath(xpathClassA).toString();
            if (blogClassText != null) {
                blogClass.add(blogClassText);
                classIndex++;
            } else {
                break;
            }
        }
        String blogDate = page.getHtml().xpath("//*[@id=\"mainBox\"]/main/div[1]/div[1]/div/div[2]/div[1]/div/span[2]/html()").toString();
        String like = page.getHtml().xpath("//*[@id=\"spanCount\"]/text()").toString();
        String collect = page.getHtml().xpath("//*[@id=\"get-collection\"]/text()").toString();
//        CSDNBlog csdnBlog=(CSDNBlog)WebMessageContext.getPrincipal();
//        csdnBlog.setCSDNBlog(blogTitle,blogAuthor,blogDate,blogHtml,blogText,blogClass,is_create_self,page.getUrl().toString(),like,collect);
//        page.putField("res", JSON.toJSONString(csdnBlog));
        page.putField("links", links.all());
        page.putField("blogTitle", blogTitle);
        page.putField("blogText", blogText);
        page.putField("blogHtml", blogHtml);
        page.putField("blogAuthor", blogAuthor);
        page.putField("imageSrc", imageSrc);
        page.putField("blogDate", blogDate);
        ObjectNode response = (ObjectNode) WebMessageContext.getPrincipal();
        if (response != null) {
            response.put("blogTitle", blogTitle);
            response.put("blogText", blogText);
            response.put("blogAuthor", blogAuthor);
            response.put("imageSrc", imageSrc);
            response.put("blogDate", blogDate);
        }
    }

    @Override
    public Site getSite() {
        return site;
    }
}
