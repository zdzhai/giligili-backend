package com.zzd.giligili.dao;

import com.zzd.giligili.domain.User;
import org.apache.ibatis.annotations.Mapper;

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
}
