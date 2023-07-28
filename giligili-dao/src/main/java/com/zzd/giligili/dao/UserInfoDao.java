package com.zzd.giligili.dao;

import com.alibaba.fastjson.JSONObject;
import com.zzd.giligili.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;

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
    List<UserInfo> getUserInfoByUserIds(Set<Long> userIdSet);

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
}
