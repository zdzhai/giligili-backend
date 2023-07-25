package com.zzd.giligili.test;

import com.zzd.giligili.service.utils.RSAUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author dongdong
 * @Date 2023/7/22 17:12
 */
@SpringBootTest(classes = {RSATest.class})
public class RSATest {

    @Test
    void encrypt() throws Exception {
        String encrypt = RSAUtil.encrypt("111");
        System.out.println(encrypt);
    }

}
