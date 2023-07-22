package com.zzd.giligili.service;

import com.zzd.giligili.dao.AuthRoleElementOperationDao;
import com.zzd.giligili.domain.auth.AuthRoleElementOperation;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @author dongdong
 * @Date 2023/7/21 16:59
 */
@Service
public class AuthRoleElementOperationService {

    @Resource
    private AuthRoleElementOperationDao authRoleElementOperationDao;

    /**
     * 根据权限id roleIdSet 获取权限操作信息
     * @param roleIdSet
     * @return
     */
    public List<AuthRoleElementOperation> getAuthRoleElementOperationByRoleIds(Set<Long> roleIdSet) {
        return authRoleElementOperationDao.getAuthRoleElementOperationByRoleIds(roleIdSet);
    }
}
