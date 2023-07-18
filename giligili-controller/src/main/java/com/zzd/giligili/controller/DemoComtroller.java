package com.zzd.giligili.controller;

import com.zzd.giligili.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dongdong
 * @Date 2023/7/17 22:50
 */
@RestController
public class DemoComtroller {

    @Autowired
    private DemoService demoService;

    @GetMapping("/query")
    public Long query(Long id){
        return demoService.query(id);
    }
}
