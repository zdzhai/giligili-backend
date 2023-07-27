package com.zzd.giligili.service;


import com.zzd.giligili.dao.UserCoinDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author dongdong
 */
@Service
public class UserCoinService {

    @Resource
    private UserCoinDao userCoinDao;

    public Integer getUserCoinsAmount(Long userId) {
        return userCoinDao.getUserCoinsAmount(userId);
    }

    public void updateUserCoinsAmount(Long userId, Integer amount) {
        Date updateTime = new Date();
        userCoinDao.updateUserCoinAmount(userId, amount, updateTime);
    }
}
