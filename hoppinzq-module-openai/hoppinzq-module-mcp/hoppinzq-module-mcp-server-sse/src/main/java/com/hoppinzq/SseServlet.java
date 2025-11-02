package com.hoppinzq;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentLinkedQueue;

@WebServlet(urlPatterns = "/sse", asyncSupported = true)
public class SseServlet extends HttpServlet {

    private static final ConcurrentLinkedQueue<AsyncContext> clients =
            new ConcurrentLinkedQueue<>();

    // 模拟事件推送方法
    public static void pushEvent(String event, String data) {
        clients.forEach(ctx -> {
            SseServlet servlet = (SseServlet) ctx.getRequest()
                    .getServletContext()
                    .getAttribute("sseServlet");
            servlet.sendEvent(ctx, event, data);
        });
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 设置SSE相关头信息
        resp.setContentType("text/event-stream");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setHeader("Connection", "keep-alive");

        // 启用异步处理
        final AsyncContext asyncContext = req.startAsync();
        asyncContext.setTimeout(0);
        clients.add(asyncContext);

        // 发送初始数据
        sendEvent(asyncContext, "init", "Connection established");
    }

    private void sendEvent(AsyncContext context, String event, String data) {
        try {
            PrintWriter writer = context.getResponse().getWriter();
            writer.write("event: " + event + "\n");
            writer.write("data: " + data + "\n\n");
            writer.flush();
        } catch (IOException e) {
            clients.remove(context);
        }
    }
}
