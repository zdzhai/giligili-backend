package com.zzd.giligili.controller.aspect;

import com.zzd.giligili.controller.support.UserSupport;
import com.zzd.giligili.domain.annotation.ApiLimitedRole;
import com.zzd.giligili.domain.auth.UserRole;
import com.zzd.giligili.domain.exception.ConditionException;
import com.zzd.giligili.service.UserRoleService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户角色接口控制切面
 * @author dongdong
 * @Date 2023/7/22 14:52
 */
@Aspect
@Order(1)
@Component
public class ApiLimitedRoleAspect {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;

    //定义切点
    @Pointcut("@annotation(com.zzd.giligili.domain.annotation.ApiLimitedRole)")
    public void check(){

    }

    @Before("check() && @annotation(apiLimitRole)")
    public void doBefore(JoinPoint joinPoint, ApiLimitedRole apiLimitRole){
        Long userId = userSupport.getUserId();
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserid(userId);
        String[] limitedRoleCodeList = apiLimitRole.limitedRoleCodeList();
        //不能允许访问的集合
        Set<String> limitRoleSet = Arrays.stream(limitedRoleCodeList).collect(Collectors.toSet());
        //用户拥有的集合
        Set<String> userRoleSet = userRoleList.stream()
                .map(UserRole::getRoleCode)
                .collect(Collectors.toSet());
        //用户集合取不允许集合的交集
        userRoleSet.retainAll(limitRoleSet);
        if (userRoleSet.size() > 0){
            throw new ConditionException("权限不足！");
        }
    }
}
