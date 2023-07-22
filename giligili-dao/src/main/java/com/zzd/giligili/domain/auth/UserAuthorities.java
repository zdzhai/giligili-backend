package com.zzd.giligili.domain.auth;

import java.util.List;

/**
 * @author 62618
 */
public class UserAuthorities {

    /**
     * 用户拥有的操作权限 具体的权限定义在auth_role_element_operation表中
     * 实体类中有一个 AuthMenu 字段 去接收联表查询的值
     */
    List<AuthRoleElementOperation> roleElementOperationList;

    /**
     * 用户拥有的菜单权限 具体的权限定义在auth_role_menu表中
     * 实体类中有一个 AuthElementOperation 字段 去接收联表查询的值
     */
    List<AuthRoleMenu> roleMenuList;

    public List<AuthRoleElementOperation> getRoleElementOperationList() {
        return roleElementOperationList;
    }

    public void setRoleElementOperationList(List<AuthRoleElementOperation> roleElementOperationList) {
        this.roleElementOperationList = roleElementOperationList;
    }

    public List<AuthRoleMenu> getRoleMenuList() {
        return roleMenuList;
    }

    public void setRoleMenuList(List<AuthRoleMenu> roleMenuList) {
        this.roleMenuList = roleMenuList;
    }
}
