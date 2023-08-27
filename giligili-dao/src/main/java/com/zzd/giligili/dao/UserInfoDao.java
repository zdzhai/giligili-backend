package com.zzd.giligili.dao;

import com.alibaba.fastjson.JSONObject;
import com.zzd.giligili.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author dongdong
 * @Date 2023/7/18 15:54
 */
@Mapper
public interface UserInfoDao {

    /**
     * 添加用户信息
     * @param userInfo
     */
    void addUserInfo(UserInfo userInfo);

    /**
     * 通过id获取用户信息
     * @param userId
     * @return
     */
    UserInfo getUserInfoById(Long userId);

    /**
     * 更新用户信息
     * @param userInfo
     */
    Long updateUserInfo(UserInfo userInfo);

    /**
     * 获取关注用户信息
     * @param userIdSet
     * @return
     */
    List<UserInfo> getUserInfoByUserIds(@Param("userIdSet") Set<Long> userIdSet);

    /**
     * 获取用户总数
     * @param params
     * @return
     */
    Long pageCountUserInfos(Map<String, Object> params);

    /**
     * 获取分页用户
     * @param params
     * @return
     */
    List<UserInfo> pageListUserInfos(JSONObject params);

    /**
     * 获取所有用户信息
     * @return
     */
    List<UserInfo> listAll();

    /**
     * 根据视频videoId获取用户详细信息
     * @param videoId
     * @return
     */
    UserInfo getUserInfoByVideoId(Long videoId);

    /**
     * 查询用户信息列表（包括已被删除的数据）
     * @param fiveMinutesAgoDate
     */
    List<UserInfo> listUserInfoWithDelete(Date fiveMinutesAgoDate);
}
