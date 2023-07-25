package com.zzd.giligili.dao;

import com.zzd.giligili.domain.RefreshTokenDetails;
import com.zzd.giligili.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * @author dongdong
 * @Date 2023/7/18 15:53
 */
@Mapper
public interface UserDao {

    /**
     * 根据手机号查用户
     * @param phone
     */
    User getUserByPhone(String phone);

    /**
     * 创建用户
     * @param user
     */
    void addUser(User user);

    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    User getUserById(Long userId);

    /**
     * 更新用户
     * @param user
     */
    Long updateUser(User user);

    /**
     * 删除refreshToken
     * @param userId
     * @param refreshToken
     */
    void deleteRefreshToken(@Param("userId") Long userId,
                            @Param("refreshToken") String refreshToken);

    /**
     * 添加refreshToken
     * @param userId
     * @param refreshToken
     * @param createTime
     */
    void addRefreshToken(@Param("userId") Long userId,
                         @Param("refreshToken") String refreshToken,
                         @Param("createTime") Date createTime);

    /**
     * 获取RefreshTokenDetails
     * @param refreshToken
     * @return
     */
    RefreshTokenDetails getRefreshAccessToken(String refreshToken);
}
