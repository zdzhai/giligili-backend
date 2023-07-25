package com.zzd.giligili.controller.aspect;

import com.zzd.giligili.controller.support.UserSupport;
import com.zzd.giligili.domain.UserMoments;
import com.zzd.giligili.domain.auth.UserRole;
import com.zzd.giligili.domain.constant.AuthRoleConstant;
import com.zzd.giligili.domain.exception.ConditionException;
import com.zzd.giligili.service.UserRoleService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 数据控制切面
 * @author dongdong
 * @Date 2023/7/22 14:52
 */
@Aspect
@Order(1)
@Component
public class DataLimitedAspect {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;

    //定义切点
    @Pointcut("@annotation(com.zzd.giligili.domain.annotation.DataLimited)")
    public void check(){

    }

    /**
     * 编写切入前方法
     * 直接控制切入方法的参数
     * @param joinPoint
     */
    @Before("check()")
    public void doBefore(JoinPoint joinPoint){
        Long userId = userSupport.getUserId();
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserid(userId);
        Set<String> userRoleSet = userRoleList.stream()
                .map(UserRole::getRoleCode)
                .collect(Collectors.toSet());
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof UserMoments) {
                UserMoments userMoments = (UserMoments) arg;
                String type = userMoments.getType();
                if (userRoleSet.contains(AuthRoleConstant.ROLE_CODE_LV1) && !"0".equals(type)) {
                    throw new ConditionException("参数异常！");
                }
            }
        }
    }
}
