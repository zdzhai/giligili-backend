package com.zzd.giligili.domain.vo;

import com.zzd.giligili.domain.UserInfo;

import java.io.Serializable;
import java.util.Date;

/**
* 用户表
* @author dongdong
 * @TableName t_user
*/
public class UserVO implements Serializable {

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
     * 用户信息
     */
    private UserInfoVO userInfo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserInfoVO getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoVO userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String toString() {
        return "UserVO{" +
                "id=" + id +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", userInfoVO=" + userInfo +
                '}';
    }
}
