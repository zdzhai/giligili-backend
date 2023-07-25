package com.zzd.giligili.service;

import com.zzd.giligili.dao.UserRoleDao;
import com.zzd.giligili.domain.auth.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author dongdong
 * @Date 2023/7/21 16:43
 */
@Service
public class UserRoleService {

    @Resource
    private UserRoleDao userRoleDao;

    /**
     * 根据userId获取用户角色信息
     * @param userId
     * @return
     */
    public List<UserRole> getUserRoleByUserid(Long userId) {
        return userRoleDao.getUserRoleByUserid(userId);

    }

    /**
     * 添加用户角色信息
     * @param userRole
     */
    public void addUserRole(UserRole userRole) {
        userRole.setCreateTime(new Date());
        userRoleDao.addUserRole(userRole);
    }
}
