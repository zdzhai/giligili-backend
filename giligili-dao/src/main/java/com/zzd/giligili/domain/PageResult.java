package com.zzd.giligili.domain;

import java.util.List;

/**
 * @author dongdong
 * @Date 2023/7/20 19:23
 */
public class PageResult<T> {

    private Long total;

    private List<T> resList;

    public PageResult(List<T> resList, Long total){
        this.total = total;
        this.resList = resList;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getResList() {
        return resList;
    }

    public void setResList(List<T> resList) {
        this.resList = resList;
    }
}
