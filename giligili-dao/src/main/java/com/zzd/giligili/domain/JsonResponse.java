package com.zzd.giligili.domain;

/**
 * @author dongdong
 * @Date 2023/7/18 14:59
 */
public class JsonResponse<T> {

    private String code;

    private String msg;

    private T data;

    //构造器
    public JsonResponse(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public JsonResponse(T data){
        this.code = "0";
        this.msg = "成功";
        this.data = data;
    }

    public static JsonResponse<String> success(){
        return new JsonResponse<String>(null);
    }

    public static JsonResponse<String> success(String data){
        return new JsonResponse<String>(data);
    }

    public static JsonResponse<String> fail(){
        return new JsonResponse<String>("1","失败");
    }

    public static JsonResponse<String> fail(String code, String msg){
        return new JsonResponse<String>(code,msg);
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
