package com.zzd.giligili.service;

import com.zzd.giligili.dao.UserAuthDao;
import com.zzd.giligili.domain.auth.*;
import com.zzd.giligili.domain.constant.AuthRoleConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author dongdong
 * @Date 2023/7/21 16:21
 */
@Service
public class UserAuthService {

    @Resource
    private UserAuthDao userAuthDao;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired AuthRoleService authRoleService;

    /**
     * 获取用户所有权限
     * @param userId
     * @return
     */
    public UserAuthorities getUserAuthorities(Long userId) {
        //1.根据userId查user_role表 联表auth_role 得到authRole信息放到userRole中
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserid(userId);
        Set<Long> roleIdSet = userRoleList.stream().map(UserRole::getRoleId).collect(Collectors.toSet());

        //2.根据roleId查操作权限auth_role_element_oper表联表auth_element_oper表查信息
        List<AuthRoleElementOperation> authRoleElementOperationList =
                authRoleService.getAuthRoleElementOperationByRoleIds(roleIdSet);

        //3.根据roleId查访问权限auth_role_menu表联表auth_menu表查信息
        List<AuthRoleMenu> authRoleMenuList =
                authRoleService.getAuthRoleMenuByRoleIds(roleIdSet);
        UserAuthorities userAuthorities = new UserAuthorities();
        userAuthorities.setRoleElementOperationList(authRoleElementOperationList);
        userAuthorities.setRoleMenuList(authRoleMenuList);
        return userAuthorities;
    }

    /**
     * 添加用户默认权限
     * @param userId
     */
    public void addUserDefaultRole(Long userId) {
        UserRole userRole = new UserRole();
        AuthRole authRole = authRoleService.getAuthRoleByCode(AuthRoleConstant.ROLE_CODE_LV0);
        userRole.setUserId(userId);
        userRole.setRoleId(authRole.getId());
        userRoleService.addUserRole(userRole);
    }
}
