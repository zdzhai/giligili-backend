package com.zzd.giligili.dao;

import com.zzd.giligili.domain.auth.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author dongdong
 * @Date 2023/7/21 16:48
 */
@Mapper
public interface UserRoleDao {

    /**
     * 根据userId获取用户角色信息
     * @param userId
     * @return
     */
    List<UserRole> getUserRoleByUserid(Long userId);

    /**
     * 添加用户权限
     * @param userRole
     */
    void addUserRole(UserRole userRole);
}
