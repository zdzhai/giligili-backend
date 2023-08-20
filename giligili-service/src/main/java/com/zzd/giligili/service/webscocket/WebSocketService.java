package com.zzd.giligili.service.webscocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zzd.giligili.domain.Danmu;
import com.zzd.giligili.domain.constant.UserMomentConstant;
import com.zzd.giligili.domain.vo.DanmuVO;
import com.zzd.giligili.service.DanmuService;
import com.zzd.giligili.service.utils.RocketMQUtil;
import com.zzd.giligili.service.utils.TokenUtil;
import io.netty.util.internal.StringUtil;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    public static final ConcurrentHashMap<String, WebSocketService> WEBSOCKET_MAP = new ConcurrentHashMap<>();

    private Session session;

    private String sessionId;

    private Long userId;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

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
        logger.info("用户连接成功:" + sessionId + "," + "当前在线人数为" + ONLINE_COUNT.get());
        try {
            this.sendMessage("0");
        } catch (Exception e) {
            logger.error("连接异常!");
        }
    }

    @OnClose
    public void closeConnect() {
        if (WEBSOCKET_MAP.containsKey(sessionId)) {
            WEBSOCKET_MAP.remove(sessionId);
            ONLINE_COUNT.getAndDecrement();
        }
        logger.info("用户连接关闭:" + sessionId + "," + "当前在线人数为" + ONLINE_COUNT.get());
    }

    /**
     * 响应前端发送的消息
     * @param message
     */
    @OnMessage
    public void onMessage(String message)  {
        logger.info("用户信息:" + sessionId + "," + "报文" + message);
        if (!StringUtil.isNullOrEmpty(message)) {
            Danmu danmu = JSONObject.parseObject(message, Danmu.class);
            DanmuVO danmuVO = new DanmuVO();
            BeanUtils.copyProperties(danmu, danmuVO);
            message = JSON.toJSONString(danmuVO);
            try {
                //群发消息
                //批量处理，通过MQ进行并发分批发送
                //todo 使用MQ进行削峰
                for (Map.Entry<String, WebSocketService> entry : WEBSOCKET_MAP.entrySet()) {
                    WebSocketService webSocketService = entry.getValue();
                    if (webSocketService.session.isOpen()) {
                        webSocketService.sendMessage(message);
                    }
                    /*DefaultMQProducer danmusProducer = (DefaultMQProducer) APPLICATION_CONTEXT.getBean("danmusProducer");
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("message", message);
                    jsonObject.put("sessionId", webSocketService.getSessionId());
                    Message msg = new Message();
                    msg.setTopic(UserMomentConstant.GROUP_DANMUS);
                    byte[] bytes = jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8);
                    msg.setBody(bytes);
                    RocketMQUtil.asyncSendMsg(danmusProducer, msg);*/
                }
                //数据持久化
                //异步批量添加到mysql
                if (this.userId != null) {
                    //todo 数据库和redis的双写一致性 先更新数据库，再更新redis
                    danmu.setUserId(userId);
                    danmu.setCreateTime(new Date());
                    DanmuService danmuService = (DanmuService) APPLICATION_CONTEXT.getBean("danmuService");
                    danmuService.asyncAddDanmu(danmu);
                    //保存到redis
                    //todo 写入优化
                    danmuService.addDanmusToRedis(danmu.getVideoId(), danmuVO);
                }
            } catch (Exception e) {
                logger.error("弹幕接收异常");
                e.printStackTrace();
            }

        }
    }

    /**
     * 每隔5秒给客户端发送当前在线人数
     * @throws IOException
     */
    @Scheduled(fixedRate = 5000)
    public void noticeOnlineCount() throws IOException {
        for (Map.Entry<String, WebSocketService> entry : WebSocketService.WEBSOCKET_MAP.entrySet()) {
            WebSocketService webSocketService = entry.getValue();
            if (webSocketService.session.isOpen()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("onlineCount", ONLINE_COUNT.get());
//                jsonObject.put("msg", "当前在线人数为" + ONLINE_COUNT.get());
                webSocketService.sendMessage(jsonObject.toJSONString());
            }
        }
    }

    @OnError
    public void onError(Throwable error) {

    }

    /**
     * 给客户端发送消息
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }
}
