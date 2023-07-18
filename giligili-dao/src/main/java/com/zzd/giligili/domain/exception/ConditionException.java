package com.zzd.giligili.domain.exception;

/**
 * 定义异常类
 * @author dongdong
 * @Date 2023/7/18 15:23
 */
public class ConditionException extends RuntimeException{

    private static final long serialVersionID = 1L;

    private String code;

    public ConditionException(String code,String name){
        super(name);
        this.code = code;
    }

    public ConditionException(String name){
        super(name);
        this.code = "500";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
