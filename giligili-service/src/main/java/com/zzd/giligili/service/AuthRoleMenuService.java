package com.zzd.giligili.service;

import com.zzd.giligili.dao.AuthRoleMenuDao;
import com.zzd.giligili.domain.auth.AuthRoleMenu;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @author dongdong
 * @Date 2023/7/21 17:23
 */
@Service
public class AuthRoleMenuService {

    @Resource
    private AuthRoleMenuDao authRoleMenuDao;
    /**
     * 根据权限id roleIdSet 获取权限操作信息
     * @param roleIdSet
     * @return
     */
    public List<AuthRoleMenu> getAuthRoleMenuByRoleIds(Set<Long> roleIdSet) {

        return authRoleMenuDao.getAuthRoleMenuByRoleIds(roleIdSet);
    }
}
