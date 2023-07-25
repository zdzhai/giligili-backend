package com.zzd.giligili.dao;

import com.zzd.giligili.domain.auth.AuthRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author dongdong
 * @Date 2023/7/22 15:55
 */
@Mapper
public interface AuthRoleDao {

    AuthRole getAuthRoleByCode(String code);
}
