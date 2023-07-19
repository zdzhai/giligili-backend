package com.zzd.giligili.domain;

import java.io.Serializable;
import java.util.Date;

/**
* 用户表
* @TableName t_user
*/
public class User implements Serializable {

    /**
    * 主键
    */
    private Long id;
    /**
    * 手机号
    */
    private String phone;
    /**
    * 邮箱
    */
    private String email;
    /**
    * 密码
    */
    private String password;
    /**
    * 盐值
    */
    private String salt;
    /**
    * 创建时间
    */
    private Date createTime;
    /**
    * 更新时间
    */
    private Date updateTime;

    /**
     * 用户信息
     */
    private UserInfo userInfo;

    /**
    * 主键
    */
    public void setId(Long id){
    this.id = id;
    }

    /**
    * 手机号
    */
    public void setPhone(String phone){
    this.phone = phone;
    }

    /**
    * 邮箱
    */
    public void setEmail(String email){
    this.email = email;
    }

    /**
    * 密码
    */
    public void setPassword(String password){
    this.password = password;
    }

    /**
    * 盐值
    */
    public void setSalt(String salt){
    this.salt = salt;
    }

    /**
    * 创建时间
    */
    public void setCreateTime(Date createTime){
    this.createTime = createTime;
    }

    /**
    * 更新时间
    */
    public void setUpdateTime(Date updatetime){
    this.updateTime = updatetime;
    }


    /**
    * 主键
    */
    public Long getId(){
    return this.id;
    }

    /**
    * 手机号
    */
    public String getPhone(){
    return this.phone;
    }

    /**
    * 邮箱
    */
    public String getEmail(){
    return this.email;
    }

    /**
    * 密码
    */
    public String getPassword(){
    return this.password;
    }

    /**
    * 盐值
    */
    public String getSalt(){
    return this.salt;
    }

    /**
    * 创建时间
    */
    public Date getCreateTime(){
    return this.createTime;
    }

    /**
    * 更新时间
    */
    public Date getUpdateTime(){
    return this.updateTime;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
