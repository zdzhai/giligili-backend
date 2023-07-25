package com.zzd.giligili.domain;

import javax.xml.crypto.Data;

/**
 * @author dongdong
 * @Date 2023/7/22 16:54
 */
public class RefreshTokenDetails {

    private Long id;

    private Long userId;

    private String refreshToken;

    private Data createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Data getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Data createTime) {
        this.createTime = createTime;
    }
}
