package com.zzd.giligili.dao;

import org.apache.ibatis.annotations.Mapper;

/**
 * @author dongdong
 * @Date 2023/7/17 22:42
 */
@Mapper
public interface DemoDao {

    /**
     * 根据id查询
     * @param id
     * @return
     */
    public Long query(Long id);
}
