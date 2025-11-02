package com.hoppinzq.service.config;

import com.hoppinzq.service.bean.ServiceWrapper;
import com.hoppinzq.service.bean.ServiceWrapperRPC;
import com.hoppinzq.service.bean.ZqServerConfig;
import com.hoppinzq.service.cache.RPCServiceStore;
import com.hoppinzq.service.cache.ServiceStore;
import com.hoppinzq.service.handler.MessageHandler;
import com.hoppinzq.service.listen.EventServiceListener;
import com.hoppinzq.service.listen.ServiceChangeListener;
import com.hoppinzq.service.message.MessageBean;
import com.hoppinzq.service.message.MessageBuffer;
import com.hoppinzq.service.proxy.ServiceProxyFactory;
import com.hoppinzq.service.service.HeartbeatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author:ZhangQi 注册中心，心跳服务检验
 */
@ConditionalOnWebApplication
public class RegisterCoreConfig {
    private static final String heartHeader = "X-HEART";
    public static long startTime;
    public static boolean isCore = false;
    private static Logger logger = LoggerFactory.getLogger(RegisterCoreConfig.class);
    @Autowired
    private ZqServerConfig zqServerConfig;

    private static void startHeartbeat(ScheduledExecutorService executorService) {
        Map heartbeatHeader = ServiceStore.heartbeatHeader;
        heartbeatHeader.put(heartHeader, 1);
        // 每10秒心跳一次
        executorService.scheduleAtFixedRate(() -> {
            if (ServiceStore.heartbeatService.size() > 0) {
                for (ServiceWrapper heart : ServiceStore.heartbeatService) {
                    if (heart.isAvailable()) {
                        String serviceID = heart.getId();
                        try {
                            String serviceIP = heart.getServiceMessage().getServiceIP();
//                            InetAddress inetAddress = InetAddress.getByName(serviceIP);
//                            boolean isReachable = inetAddress.isReachable(2000);
//                            if(!isReachable) {
//                                throw new RemotingException(new RuntimeException("服务连接失败"));
//                            }.
                            String serviceHeartAddress = "http://" + serviceIP + ":" + heart.getServiceMessage().getServicePort() + heart.getServiceMessage().getServicePrefix();
                            logger.debug("开始心跳：服务路径" + serviceHeartAddress);
                            HeartbeatService service = ServiceProxyFactory.createProxy(HeartbeatService.class, serviceHeartAddress, heartbeatHeader);
                            service.areYouOk();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            //heart.setAvailable(Boolean.FALSE);
                            List<ServiceWrapperRPC> serviceWrapperRPCS = RPCServiceStore.serviceWrapperRPCList;
                            synchronized (serviceWrapperRPCS) {
                                Iterator<ServiceWrapperRPC> iterator = serviceWrapperRPCS.iterator();
                                while (iterator.hasNext()) {
                                    ServiceWrapperRPC outerService = iterator.next();
                                    if (serviceID.equals(outerService.getId())) {
                                        outerService.setAvailable(false);
                                    }
                                }
                            }
                            logger.error("检测到id为：" + serviceID + "的服务已不可用，尝试重新连接");
                        }
                    }
                }
            }
        }, 0, 10, TimeUnit.SECONDS);

        //每60s检查并移除不可用服务
        executorService.scheduleAtFixedRate(() -> {
            if (ServiceStore.heartbeatService.size() > 0) {
                List<ServiceWrapperRPC> serviceWrapperRPCS = RPCServiceStore.serviceWrapperRPCList;
                synchronized (serviceWrapperRPCS) {
                    String serviceID;
                    Iterator<ServiceWrapperRPC> iterator = serviceWrapperRPCS.iterator();
                    while (iterator.hasNext()) {
                        ServiceWrapperRPC outerService = iterator.next();
                        if (!outerService.getAvailable()) {
                            serviceID = outerService.getId();
                            iterator.remove();
                            List<ServiceWrapper> heartbeatService = ServiceStore.heartbeatService;
                            for (int i = 0; i < heartbeatService.size(); i++) {
                                ServiceWrapper heart = heartbeatService.get(i);
                                if (serviceID.equals(heart.getId())) {
                                    logger.error("检测到id为：" + serviceID + "的服务已不可用，将移除该服务！");
                                    logger.debug("服务移除成功！服务ID：" + serviceID);
                                    heartbeatService.remove(i);
                                    i--;
                                }
                            }
                        }
                    }
                }
            }
        }, 0, 60, TimeUnit.SECONDS);
    }

    private static void startMessage(ScheduledExecutorService executorService) {
        //每10s消费一次消息队列里的消息
        executorService.scheduleAtFixedRate(() -> {
            if (MessageBuffer.count == 0) {

            }
            for (int i = 0; i < MessageBuffer.count; i++) {
                try {
                    MessageBean messageBean = MessageBuffer.take();
                    MessageHandler handler = messageBean.getMessageHandler();
                    handler.handle(messageBean);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    private static void startServiceListen() {
        EventServiceListener eventListener = new EventServiceListener() {
            @Override
            public void onServiceChange(List<ServiceWrapperRPC> list) {
                System.err.println("服务列表改变: " + list);
            }
        };
        // 创建守护线程来监听服务的变化
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 5000, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
        // 允许核心线程超时
        executor.allowCoreThreadTimeOut(true);
        Thread thread = new Thread(new ServiceChangeListener(eventListener), "监听服务列表守护线程-" + System.currentTimeMillis());
        thread.setDaemon(true);
        executor.execute(thread);
    }

    @PostConstruct
    public void init() {
        isCore = true;
        startTime = System.currentTimeMillis();
        String serviceAddress = "http://" + zqServerConfig.getIp() + ":" + zqServerConfig.getPort() + zqServerConfig.getPrefix();
        logger.debug("注册中心启动成功，注册中心路径：" + serviceAddress);
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        startHeartbeat(executorService);
        startMessage(executorService);
        startServiceListen();
    }
}