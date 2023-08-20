package com.zzd.giligili.domain.vo;


/**
 * 传递给前端的danmu实体类
 */
public class DanmuVO {

    /**
     * danmuTime
     */
    private String danmuTime;

    /**
     * content
     */
    private String content;

    /**
     * userId
     */
    private Long userId;

    public String getDanmuTime() {
        return danmuTime;
    }

    public void setDanmuTime(String danmuTime) {
        this.danmuTime = danmuTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
