package com.zzd.giligili.controller.support;

import com.zzd.giligili.domain.exception.ConditionException;
import com.zzd.giligili.service.utils.TokenUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author dongdong
 * @Date 2023/7/19 18:24
 */
@Component
public class UserSupport {

    public Long getUserId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        Long userId = TokenUtil.verifyToken(token);
        if (userId < 0){
            throw new ConditionException("非法用户！");
        }
        return userId;
    }
}
