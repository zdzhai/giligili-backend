package com.zzd.giligili.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author dongdong
 * @Date 2023/7/29 17:26
 */
//@FeignClient("giligili-demo-ms")
public interface MsDeclareService {

    @GetMapping("/demo")
    public Long mget(@RequestParam Long id);

    @PostMapping("/demo")
    public Map<String, Object> mpost(@RequestBody Map<String, String> params);
}
