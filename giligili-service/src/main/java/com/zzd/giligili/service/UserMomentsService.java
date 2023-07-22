package com.zzd.giligili.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zzd.giligili.dao.UserMomentsDao;
import com.zzd.giligili.domain.UserMoments;
import com.zzd.giligili.domain.constant.RedisConstant;
import com.zzd.giligili.domain.constant.UserMomentConstant;
import com.zzd.giligili.service.utils.RocketMQUtil;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * @author dongdong
 * @Date 2023/7/21 14:01
 */
@Service
public class UserMomentsService {

    @Resource
    private UserMomentsDao userMomentsDao;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 新增用户动态并添加到MQ
     * @param userMoments
     * @return
     */
    public void addUserMoments(UserMoments userMoments) throws Exception {
        //1.新增用户动态
        userMoments.setCreateTime(new Date());
        Long momentsId = userMomentsDao.addUserMoments(userMoments);
        //2.把用户动态添加到MQ
        DefaultMQProducer producer = (DefaultMQProducer) applicationContext.getBean("momentsProducer");
        byte[] bytes = JSONObject.toJSONString(userMoments).getBytes(StandardCharsets.UTF_8);
        Message msg = new Message(UserMomentConstant.TOPIC_MOMENTS,bytes);
        RocketMQUtil.syncSendMsg(producer, msg);
    }

    /**
     * 获取用户关注的用户动态
     * @param userId
     * @return
     */
    public List<UserMoments> getUserSubscribedMoments(Long userId) {
        String key = RedisConstant.SUBSCRIBED_PREFIX + userId;
        String listStr = redisTemplate.opsForValue().get(key);
        return JSONArray.parseArray(listStr, UserMoments.class);
    }
}
