package com.zzd;

import com.zzd.giligili.service.webscocket.WebSocketService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * @author dongdong
 * @Date 2023/7/17 22:24
 */
@SpringBootApplication
public class GiligiliApp {
    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(GiligiliApp.class, args);
        WebSocketService.setApplicationContext(applicationContext);
    }
}
