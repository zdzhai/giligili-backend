package com.zzd.giligili.service.webscocket;

import com.alibaba.fastjson.JSONObject;
import com.zzd.giligili.domain.Danmu;
import com.zzd.giligili.service.DanmuService;
import com.zzd.giligili.service.utils.TokenUtil;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author dongdong
 * @Date 2023/7/26 16:41
 */
@Service
@ServerEndpoint("/imserver/{token}")
public class WebSocketService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final AtomicInteger ONLINE_COUNT = new AtomicInteger(0);

    private static final ConcurrentHashMap<String, WebSocketService> WEBSOCKET_MAP = new ConcurrentHashMap<>();

    private Session session;

    private String sessionId;

    private Long userId;

    private static ApplicationContext APPLICATION_CONTEXT;

    /**
     * 多例模式下bean注入的解决
     * 这里相当于就是直接把全局的ApplicationContext注入到WebSocketService中
     * 因为ApplicationContext中的实例是单例的，使用AutoWired注入的话，只能注入一次
     * 所以采用这种方法直接把ApplicationContext注入到WebSocketService的静态变量中
     * 就可以每一个WebSocketService对象都能获取到需要的Bean
     * @param applicationContext
     */
    public static void setApplicationContext (ApplicationContext applicationContext) {
        WebSocketService.APPLICATION_CONTEXT = applicationContext;
    }

    @OnOpen
    public void openConnect(Session session, @PathParam("token") String token) {
        try {
            this.userId = TokenUtil.verifyToken(token);
        } catch ( Exception e){ }
        this.sessionId = session.getId();
        this.session = session;
        if(WEBSOCKET_MAP.contains(sessionId)) {
            WEBSOCKET_MAP.remove(sessionId);
            WEBSOCKET_MAP.put(sessionId, this);
        } else {
            WEBSOCKET_MAP.put(sessionId, this);
            ONLINE_COUNT.getAndIncrement();
        }
        logger.info("用户连接成功:" + sessionId + "," + "当前在线人数未" + ONLINE_COUNT.get());
        try {
            this.sendMessage("0");
        } catch (Exception e) {
        logger.error("连接异常!");
        }
    }

    @OnClose
    public void closeConnect() {
        if (WEBSOCKET_MAP.contains(sessionId)) {
            WEBSOCKET_MAP.remove(sessionId);
            ONLINE_COUNT.getAndDecrement();
        }
        logger.info("用户连接成功:" + sessionId + "," + "当前在线人数未" + ONLINE_COUNT.get());
    }

    @OnMessage
    public void onMessage(String message)  {
        logger.info("用户信息:" + sessionId + "," + "报文" + message);
        if (!StringUtil.isNullOrEmpty(message)) {
            try {
                //群发消息
                //todo 批量处理，通过MQ进行并发分批发送
                for (Map.Entry<String, WebSocketService> entry : WEBSOCKET_MAP.entrySet()) {
                    WebSocketService webSocketService = entry.getValue();
                    if (webSocketService.session.isOpen()) {
                        webSocketService.sendMessage(message);
                    }
                }
                //数据持久化
                //todo 异步批量添加到mysql
                if (this.userId != null) {
                    Danmu danmu = JSONObject.parseObject(message, Danmu.class);
                    danmu.setUserId(userId);
                    danmu.setCreateTime(new Date());
                    DanmuService danmuService = (DanmuService) APPLICATION_CONTEXT.getBean("danmuService");
                    danmuService.addDanmu(danmu);
                    //保存到redis
                    //todo 写入优化
                    danmuService.addDanmusToRedis(danmu);
                }
            } catch (Exception e) {
                logger.error("弹幕接收异常");
                e.printStackTrace();
            }

        }
    }

    @OnError
    public void onError(Throwable error) {

    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }
}
