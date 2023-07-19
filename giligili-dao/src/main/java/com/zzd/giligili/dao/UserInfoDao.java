package com.zzd.giligili.dao;

import com.zzd.giligili.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;

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
}
