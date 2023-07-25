package com.zzd.giligili.domain.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 用户角色接口控制注解
 * @author dongdong
 * @Date 2023/7/22 14:50
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Component
public @interface ApiLimitedRole {

    String[] limitedRoleCodeList() default {};
}
