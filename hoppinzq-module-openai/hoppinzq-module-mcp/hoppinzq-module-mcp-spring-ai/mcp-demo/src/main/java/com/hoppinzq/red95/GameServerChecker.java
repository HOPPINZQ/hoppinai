package com.hoppinzq.red95;

import com.hoppinzq.red95.model.ScreenInfoResult;
import com.hoppinzq.red95.service.GameSocketAPI;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameServerChecker {

    private static final String API_VERSION = "1.0"; // 根据实际情况调整
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void executeWithDelay(Runnable task, Long delaySeconds) {
        scheduler.schedule(task, delaySeconds, TimeUnit.SECONDS);
    }

    // 使用默认参数的便捷方法
    public static boolean isServerRunning() {
        return isServerRunning("localhost", 7445, 2.0);
    }

    public static boolean isServerRunning(String host, int port, double timeout) {
        Socket socket = null;
        String command = "ping";
        Map params = new HashMap<>();
        try {
            // 创建请求数据
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("apiVersion", API_VERSION);
            requestData.put("requestId", UUID.randomUUID().toString());
            requestData.put("command", command);
            requestData.put("params", params);
            requestData.put("language", "zh");

            String jsonRequest = new JSONObject(requestData).toString();

            // 创建socket并设置超时
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), (int) (timeout * 1000));
            socket.setSoTimeout((int) (timeout * 1000));

            // 发送请求
            OutputStream out = socket.getOutputStream();
            out.write(jsonRequest.getBytes(StandardCharsets.UTF_8));
            out.flush();

            // 接收响应
            InputStream in = socket.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] chunk = new byte[4096];
            int bytesRead;

            while ((bytesRead = in.read(chunk)) != -1) {
                buffer.write(chunk, 0, bytesRead);
            }

            String response = buffer.toString(StandardCharsets.UTF_8);
            System.out.println(response);
            // 解析JSON响应
            JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse.has("status") &&
                    jsonResponse.getInt("status") > 0 &&
                    jsonResponse.has("data");

        } catch (SocketTimeoutException e) {
            return false;
        } catch (ConnectException e) {
            return false;
        } catch (IOException e) {
            return false;
        } catch (JSONException e) {
            return false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // 关闭socket时出错，忽略
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // 检查游戏服务器是否运行
        boolean running = isServerRunning();
        System.out.println("游戏服务 " + (running ? "运行中" : "不在运行"));
        if (running) {
            GameSocketAPI gameSocketAPI = new GameSocketAPI("localhost", 7445, "zh");
            // 查询服务信息
            ScreenInfoResult screenInfoResult = gameSocketAPI.screenInfoQuery();
            System.out.println(screenInfoResult.toString());
            // 展开基地车，延时1秒后，执行后续操作
            executeWithDelay(() -> gameSocketAPI.deployMcvAndWait(), 1L);
            // 查询电厂是否能建造，若能，建造
            System.out.println("电厂能建造吗？：" + gameSocketAPI.ensureCanBuildWait("电厂"));
            // 延时10秒
            executeWithDelay(() -> System.out.println("开始建造电厂"), 10L);
            // 查询地图信息
            System.out.println(gameSocketAPI.mapQuery());
            // 查询玩家信息
            System.out.println(gameSocketAPI.playerBaseInfoQuery());
            // 在建筑栏选择已就绪的建筑，放置建筑，位置自动选择
            gameSocketAPI.placeBuilding("Building");
            // 查询兵营是否能建造，若能，建造；不能，先建造电厂，再建造兵营
            System.out.println("兵营能建造吗？：" + gameSocketAPI.ensureCanBuildWait("兵营"));
            executeWithDelay(() -> System.out.println("开始建造兵营"), 10L);
            gameSocketAPI.placeBuilding("Building");
            // ... 后续操作

        }


    }
}
