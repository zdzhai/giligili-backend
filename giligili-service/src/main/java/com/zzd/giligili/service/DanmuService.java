package com.zzd.giligili.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zzd.giligili.dao.DanmuDao;
import com.zzd.giligili.domain.Danmu;
import com.zzd.giligili.domain.exception.ConditionException;
import com.zzd.giligili.domain.vo.DanmuVO;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@EnableAsync
public class DanmuService {

    private static final String DANMU_KEY = "gili:dm:video:";

    @Resource
    private DanmuDao danmuDao;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void addDanmu(Danmu danmu){
        danmuDao.addDanmu(danmu);
    }

    @Async
    public void asyncAddDanmu(Danmu danmu){
        if (danmu.getVideoId() == null) {
            throw new ConditionException("视频不存在！");
        }
        danmuDao.addDanmu(danmu);
    }

    /**
     * 查询策略是优先查redis中的弹幕数据，
     * 如果没有的话查询数据库，然后把查询的数据写入redis当中
     */
    public List<DanmuVO> getDanmus(Long videoId,
                                 String startTime, String endTime) throws Exception {

        String key = DANMU_KEY + videoId;
        String value = redisTemplate.opsForValue().get(key);
        List<DanmuVO> list;
        if(!StringUtil.isNullOrEmpty(value)){
            list = JSONArray.parseArray(value, DanmuVO.class);
/*            if(!StringUtil.isNullOrEmpty(startTime)
                    && !StringUtil.isNullOrEmpty(endTime)){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date startDate = sdf.parse(startTime);
                Date endDate = sdf.parse(endTime);
                List<DanmuVO> childList = new ArrayList<>();
                for(DanmuVO danmu : list){
                    Date createTime = danmu.getCreateTime();
                    if(createTime.after(startDate) && createTime.before(endDate)){
                        childList.add(danmu);
                    }
                }
                list = childList;
            }*/
        }else{
            Map<String, Object> params = new HashMap<>();
            params.put("videoId", videoId);
            params.put("startTime", startTime);
            params.put("endTime", endTime);
            list = danmuDao.getDanmuVOs(params);
            //保存弹幕到redis
            redisTemplate.opsForValue().set(key, JSONObject.toJSONString(list));
        }
        return list;
    }

    public List<Object[]> getDanmusForBackend(Long videoId,
                                             String startTime, String endTime) throws Exception {

        String key = DANMU_KEY + videoId;
        String value = redisTemplate.opsForValue().get(key);
        List<Object[]> danmusList;
        if(!StringUtil.isNullOrEmpty(value)){
            danmusList = JSONArray.parseArray(value, Object[].class);
        } else {
            Map<String, Object> params = new HashMap<>();
            params.put("videoId", videoId);
            params.put("startTime", startTime);
            params.put("endTime", endTime);
            List<Danmu> dbDanmus = danmuDao.getDanmus(params);
            danmusList = new ArrayList<>();
            dbDanmus.forEach(danmu -> {
                Object[] danmuObj = new Object[5];
                danmuObj[0] = Float.valueOf(danmu.getDanmuTime());
                danmuObj[1] = danmu.getDanmuType();
                danmuObj[2] = danmu.getColor();
                danmuObj[3] = String.valueOf( danmu.getUserId());
                danmuObj[4] = danmu.getContent();
                danmusList.add(danmuObj);
            });
            redisTemplate.opsForValue().set(key, JSONObject.toJSONString(danmusList));
        }
        return  danmusList;
    }

    /**
     * 增加弹幕信息
     * @param danmuVO
     */
    public void addDanmusToRedis(Long videoId ,DanmuVO danmuVO) {
        if (videoId == null) {
            throw new ConditionException("无效视频ID!");
        }
        //1.查redis
        String key = DANMU_KEY + videoId;
        String value = redisTemplate.opsForValue().get(key);
        List<DanmuVO> list = new ArrayList<>();
        //2.在视频页加载时已经往redis写，所以一定有值，解析并添加新的弹幕
        if(!StringUtil.isNullOrEmpty(value)){
            list = JSONArray.parseArray(value, DanmuVO.class);
        }
        list.add(danmuVO);
        redisTemplate.opsForValue().set(key, JSONObject.toJSONString(list));
    }

}
