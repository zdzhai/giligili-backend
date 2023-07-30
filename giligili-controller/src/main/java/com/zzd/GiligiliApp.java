package com.zzd;

import com.zzd.giligili.service.webscocket.WebSocketService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author dongdong
 * @Date 2023/7/17 22:24
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
//@EnableFeignClients(basePackages = "com.zzd.giligili.service.feign")
public class GiligiliApp {
    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(GiligiliApp.class, args);
        WebSocketService.setApplicationContext(applicationContext);
    }
}
