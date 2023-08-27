package com.zzd.giligili.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zzd.giligili.dao.UserInfoDao;
import com.zzd.giligili.domain.*;
import com.zzd.giligili.domain.exception.ConditionException;
import com.zzd.giligili.domain.vo.DanmuVO;
import com.zzd.giligili.domain.vo.UserInfoVO;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author dongdong
 * @description 针对表【t_user_info(用户基本信息表)】的数据库操作Service
 * @createDate 2023-07-18 15:48:22
 */
@Service
public class UserInfoService {

    private static final String USERINFO_KEY = "gili:userinfo:";

    @Resource
    private UserInfoDao userInfoDao;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private VideoService videoService;

    @Resource
    private UserFollowingService userFollowingService;

    /**
     * 添加用户信息
     *
     * @param userInfo
     */
    public void addUserInfo(UserInfo userInfo) {
        userInfoDao.addUserInfo(userInfo);
    }

    /**
     * 通过id获取用户信息
     * 增加首次获取信息后缓存到redis
     *
     * @param userId
     * @return
     */
    public UserInfoVO getUserInfoById(Long userId) {
        //1.查redis
        String key = USERINFO_KEY + userId;
        String value = redisTemplate.opsForValue().get(key);
        //2. 命中
        if (!StringUtil.isNullOrEmpty(value)) {
            return JSONObject.parseObject(value, UserInfoVO.class);
        } else {
            //未命中
            UserInfo dbUserInfo = userInfoDao.getUserInfoById(userId);
            UserInfoVO userInfoVO = new UserInfoVO();
            BeanUtils.copyProperties(dbUserInfo, userInfoVO);
            List<UserFollowing> userFans = userFollowingService.getUserFans(userId);
            userInfoVO.setFansNum(Long.valueOf(String.valueOf(userFans.size())));
            List<FollowingGroup> userFollowings = userFollowingService.getUserFollowings(userId);
            Long followingNum = 0L;
            if (userFollowings != null && userFollowings.size() > 0) {
                followingNum = Long.parseLong(String.valueOf(userFollowings.get(0).getFollowingUserInfoList().size()));
            }
            userInfoVO.setFollowingNum(followingNum);
            redisTemplate.opsForValue().set(key, JSONObject.toJSONString(userInfoVO), 10, TimeUnit.HOURS);
            return userInfoVO;
        }
    }

    /**
     * 更新用户信息
     *
     * @param userInfo
     */
    Long updateUserInfo(UserInfo userInfo) {
        return userInfoDao.updateUserInfo(userInfo);
    }

    /**
     * 根据用户id集合获取用户信息
     *
     * @param userIdSet
     * @return
     */
    public List<UserInfo> getUserInfoByUserIds(Set<Long> userIdSet) {
        return userInfoDao.getUserInfoByUserIds(userIdSet);
    }

    /**
     * 分页获取用户信息
     *
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
        if (total > 0) {
            list = userInfoDao.pageListUserInfos(params);
        }
        return new PageResult<>(list, total);
    }

    public List<UserInfo> listAll() {
        return userInfoDao.listAll();
    }

    /**
     * 根据视频videoId获取用户详细信息
     *
     * @param videoId
     * @return
     */
    public UserInfo getUserInfoByVideoId(Long videoId) {
        return userInfoDao.getUserInfoByVideoId(videoId);
    }

    /**
     * 根据用户id和视频id获取用户详细信息及三连信息
     *
     * @param userId
     * @param videoId
     * @return
     */
    public Map<String, Object> getUserInfoAndSan(Long userId, Long videoId) {
        if (videoId == null || videoId < 0) {
            throw new ConditionException("请求参数异常！");
        }
        Map<String, Object> resMap = new HashMap<>();
        UserInfoVO userInfoVO = null;
        if (userId != null) {
            if (userId < 0) {
                throw new ConditionException("请求参数异常！");
            }
            userInfoVO = this.getUserInfoById(userId);
            //1.根据videoId查询视频所属userId
            Video dbVideo = videoService.getVideoById(videoId);
            if (dbVideo == null) {
                throw new ConditionException("视频不存在！");
            }
            Long videoUserId = dbVideo.getUserId();
            //2. 根据userId和videoUsrId查t_following表
            UserFollowing following = userFollowingService.isFollowing(userId, videoUserId);
            if (following != null) {
                userInfoVO.setFollowed(true);
            }
            //3. 根据videoId和userId，查询点赞和收藏的ture/false
            Map<String, Object> videoLikes = videoService.getVideoLikes(userId, videoId);
            Map<String, Object> videoCollections = videoService.getVideoCollections(userId, videoId);
            Map<String, Object> videoCoins = videoService.getVideoCoins(userId, videoId);
            resMap.putAll(videoLikes);
            resMap.putAll(videoCollections);
            resMap.putAll(videoCoins);
        }
        //4. 根据videoId获取视频所属用户相关信息
        UserInfo videoUserInfo = this.getUserInfoByVideoId(videoId);
        resMap.put("userInfo", userInfoVO);
        resMap.put("videoUserInfo", videoUserInfo);
        return resMap;
    }

    /**
     * 查询用户信息列表（包括已被删除的数据）
     */
    public List<UserInfo> listUserInfoWithDelete(Date fiveMinutesAgoDate) {
        return userInfoDao.listUserInfoWithDelete(fiveMinutesAgoDate);
    }
}
