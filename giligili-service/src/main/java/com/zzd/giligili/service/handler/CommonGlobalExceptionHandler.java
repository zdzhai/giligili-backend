package com.zzd.giligili.service.handler;

import com.zzd.giligili.domain.JsonResponse;
import com.zzd.giligili.domain.exception.ConditionException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 通用全局异常处理器
 * @author dongdong
 * @Date 2023/7/18 15:22
 */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CommonGlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public JsonResponse<String> conditionExceptionHandler(HttpRequest request, Exception e){
        String errorMsg = e.getMessage();
        if (e instanceof ConditionException){
            String errorCode = ((ConditionException) e).getCode();
            return new JsonResponse<>(errorCode,errorMsg);
        } else {
            return new JsonResponse<>("500",errorMsg);
        }
    }
}
