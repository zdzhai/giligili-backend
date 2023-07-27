package com.zzd.giligili.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface UserCoinDao {

    /**
     * 获取用户总硬币数
     * @param userId
     * @return
     */
    Integer getUserCoinsAmount(Long userId);

    Integer updateUserCoinAmount(@Param("userId") Long userId,
                              @Param("amount") Integer amount,
                              @Param("updateTime") Date updateTime);
}
