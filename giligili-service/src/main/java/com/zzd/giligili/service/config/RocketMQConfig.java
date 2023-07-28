package com.zzd.giligili.service.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zzd.giligili.domain.UserFollowing;
import com.zzd.giligili.domain.UserMoments;
import com.zzd.giligili.domain.constant.RedisConstant;
import com.zzd.giligili.domain.constant.UserMomentConstant;
import com.zzd.giligili.service.UserFollowingService;
import com.zzd.giligili.service.webscocket.WebSocketService;
import io.netty.util.internal.StringUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * RocketMQ配置类
 * @author dongdong
 * @Date 2023/7/20 20:37
 */
//@Configuration
public class RocketMQConfig {

    @Value("${rocketmq.name.server.addr}")
    public static String nameServerAddr;

    @Autowired
    private UserFollowingService userFollowingService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 动态生产者
     * @return
     * @throws MQClientException
     */
    @Bean("momentsProducer")
    public DefaultMQProducer momentsProducer() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(UserMomentConstant.GROUP_MOMENTS);
        producer.setNamesrvAddr(nameServerAddr);
        producer.start();
        return producer;
    }

    /**
     * 动态消费者
     * 采用推送模式的消费者及监听器
     * @return
     * @throws MQClientException
     */
    @Bean("momentsConsumer")
    public DefaultMQPushConsumer momentsConsumer() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(UserMomentConstant.GROUP_MOMENTS);
        consumer.setNamesrvAddr(nameServerAddr);
        consumer.subscribe(UserMomentConstant.TOPIC_MOMENTS,"*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                MessageExt msg = msgs.get(0);
                if (msg == null) {
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                String bodyStr = new String(msg.getBody());
                UserMoments userMoments = JSONObject.toJavaObject(JSONObject.parseObject(bodyStr),UserMoments.class);
                //根据id查询粉丝并推送至各自的redis缓存中
                Long userId = userMoments.getUserId();
                List<UserFollowing> userFans = userFollowingService.getUserFans(userId);
                for (UserFollowing userFan : userFans) {
                    //Redis Key
                    String key = RedisConstant.SUBSCRIBED_PREFIX + userFan.getUserId();
                    String subscribedListStr = redisTemplate.opsForValue().get(key);
                    List<UserMoments> subscribedList;
                    if (StringUtil.isNullOrEmpty(subscribedListStr)){
                        subscribedList = new ArrayList<>();
                    } else {
                        subscribedList = JSONArray.parseArray(subscribedListStr, UserMoments.class);
                    }
                    subscribedList.add(userMoments);
                    redisTemplate.opsForValue().set(key, JSONObject.toJSONString(subscribedList));
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        return consumer;
    }

    /**
     * 弹幕生产者
     * @return
     * @throws MQClientException
     */
    @Bean("danmusProducer")
    public DefaultMQProducer danmusProducer() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(UserMomentConstant.GROUP_DANMUS);
        producer.setNamesrvAddr(nameServerAddr);
        producer.start();
        return producer;
    }

    /**
     * 弹幕消费者
     * 采用推送模式的消费者及监听器
     * @return
     * @throws MQClientException
     */
    @Bean("danmusConsumer")
    public DefaultMQPushConsumer danmusConsumer() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(UserMomentConstant.GROUP_DANMUS);
        consumer.setNamesrvAddr(nameServerAddr);
        consumer.subscribe(UserMomentConstant.TOPIC_DANMUS,"*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                MessageExt msg = msgs.get(0);
                if (msg == null) {
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                String bodyStr = new String(msg.getBody());
                JSONObject jsonObject = JSONObject.parseObject(bodyStr);
                //处理前端传递的参数
                String sessionId = jsonObject.getString("sessionId");
                //message是一个json(Danmu)，包含videoId和content
                String message = jsonObject.getString("message");
                //根据sessionId获取对应的WebSocketService
                WebSocketService webSocketService = WebSocketService.WEBSOCKET_MAP.get(sessionId);
                if (webSocketService.getSession().isOpen()) {
                    try {
                        webSocketService.sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        return consumer;
    }
}
