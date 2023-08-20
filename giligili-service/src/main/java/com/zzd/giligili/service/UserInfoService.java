package com.zzd.giligili.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zzd.giligili.dao.UserInfoDao;
import com.zzd.giligili.domain.PageResult;
import com.zzd.giligili.domain.UserInfo;
import com.zzd.giligili.domain.vo.DanmuVO;
import com.zzd.giligili.domain.vo.UserInfoVO;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
* @author dongdong
* @description 针对表【t_user_info(用户基本信息表)】的数据库操作Service
* @createDate 2023-07-18 15:48:22
*/
@Service
public class UserInfoService  {

    private static final String USERINFO_KEY = "gili:userinfo:";

    @Resource
    private UserInfoDao userInfoDao;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 添加用户信息
     * @param userInfo
     */
     public void addUserInfo(UserInfo userInfo) {
         userInfoDao.addUserInfo(userInfo);
    }

    /**
     * 通过id获取用户信息
     * 增加首次获取信息后缓存到redis
     * @param userId
     * @return
     */
    public UserInfoVO getUserInfoById(Long userId){
        //1.查redis
        String key = USERINFO_KEY + userId;
        String value = redisTemplate.opsForValue().get(key);
        //2. 命中
        if(!StringUtil.isNullOrEmpty(value)){
            return JSONObject.parseObject(value, UserInfoVO.class);
        } else {
            //未命中
            UserInfo dbUserInfo = userInfoDao.getUserInfoById(userId);
            UserInfoVO userInfoVO = new UserInfoVO();
            BeanUtils.copyProperties(dbUserInfo, userInfoVO);
            redisTemplate.opsForValue().set(key, JSONObject.toJSONString(userInfoVO),10, TimeUnit.HOURS);
            return userInfoVO;
        }
    }
    /**
     * 更新用户信息
     * @param userInfo
     */
    Long updateUserInfo(UserInfo userInfo){
        return userInfoDao.updateUserInfo(userInfo);
    }

    /**
     * 根据用户id集合获取用户信息
     * @param userIdSet
     * @return
     */
    public List<UserInfo> getUserInfoByUserIds(Set<Long> userIdSet){
        return userInfoDao.getUserInfoByUserIds(userIdSet);
    }

    /**
     * 分页获取用户信息
     * @param params
     * @return
     */
    public PageResult<UserInfo> pageListUserInfos(JSONObject params) {
        Integer pageNum = params.getInteger("pageNum");
        Integer pageSize = params.getInteger("pageSize");
        params.put("start", (pageNum - 1) * pageSize);
        params.put("limit", pageSize);
        Long total = userInfoDao.pageCountUserInfos(params);
        List<UserInfo> list = new ArrayList<>();
        if (total > 0){
            list = userInfoDao.pageListUserInfos(params);
        }
        return new PageResult<>(list, total);
    }

    public List<UserInfo> listAll(){
        return userInfoDao.listAll();
    }

    public UserInfo getUserInfoByVideoId(Long videoId) {
        return userInfoDao.getUserInfoByVideoId(videoId);
    }
}
