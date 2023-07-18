package com.zzd.giligili.service;

import com.zzd.giligili.dao.DemoDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author dongdong
 * @Date 2023/7/17 22:47
 */
@Service
public class DemoService {

    @Resource
    private DemoDao demoDao;

    public Long query(Long id){
        return demoDao.query(id);
    }
}
