package com.zzd.giligili.service;

import com.zzd.giligili.domain.auth.AuthRoleElementOperation;
import com.zzd.giligili.domain.auth.AuthRoleMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @author dongdong
 * @Date 2023/7/21 16:43
 */
@Service
public class AuthRoleService {

    @Autowired
    private AuthRoleElementOperationService authRoleElementOperationService;

    @Autowired
    private AuthRoleMenuService authRoleMenuService;

    /**
     * 根据权限id roleIdSet 获取权限操作信息
     * @param roleIdSet
     * @return
     */
    public List<AuthRoleElementOperation> getAuthRoleElementOperationByRoleIds(Set<Long> roleIdSet) {
        return authRoleElementOperationService.getAuthRoleElementOperationByRoleIds(roleIdSet);
    }

    /**
     * 根据权限id roleIdSet 获取权限操作信息
     * @param roleIdSet
     * @return
     */
    public List<AuthRoleMenu> getAuthRoleMenuByRoleIds(Set<Long> roleIdSet) {
        return authRoleMenuService.getAuthRoleMenuByRoleIds(roleIdSet);
    }
}
