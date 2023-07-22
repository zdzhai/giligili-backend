package com.zzd.giligili.dao;

import com.zzd.giligili.domain.auth.AuthRoleMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

/**
 * @author dongdong
 * @Date 2023/7/21 17:24
 */
@Mapper
public interface AuthRoleMenuDao {

    /**
     * 根据权限id roleIdSet 获取权限操作信息
     * @param roleIdSet
     * @return
     */
    List<AuthRoleMenu> getAuthRoleMenuByRoleIds(Set<Long> roleIdSet);
}
