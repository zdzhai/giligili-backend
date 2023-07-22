package com.zzd.giligili.service;

import com.mysql.cj.util.StringUtils;
import com.zzd.giligili.dao.UserDao;
import com.zzd.giligili.dao.UserInfoDao;
import com.zzd.giligili.domain.User;
import com.zzd.giligili.domain.UserInfo;
import com.zzd.giligili.domain.constant.UserConstant;
import com.zzd.giligili.domain.exception.ConditionException;
import com.zzd.giligili.service.utils.MD5Util;
import com.zzd.giligili.service.utils.RSAUtil;
import com.zzd.giligili.service.utils.TokenUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
* @author dongdong
* @description 针对表【t_user(用户表)】的数据库操作Service
* @createDate 2023-07-18 15:48:22
*/
@Service
public class UserService {

    @Resource
    private UserDao userDao;

    @Resource
    private UserInfoDao userInfoDao;

    public void addUser(User user) {
        //1.校验参数
        String phone = user.getPhone();
        if (StringUtils.isNullOrEmpty(phone)){
            throw new ConditionException("手机号不能为空");
        }
        //2.根据手机号查数据库
        User dbUser = this.getUserByPhone(phone);
        if (dbUser != null) {
            throw new ConditionException("手机号已被注册!");
        }
        //3.密码解密
        String password = user.getPassword();
        String decryptPwd;
        try {
            decryptPwd = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解析失败！");
        }
        Date now = new Date();
        String salt = String.valueOf(now.getTime());
        String signPwd = MD5Util.sign(decryptPwd, salt, "UTF-8");
        //4.创建用户
        user.setPassword(signPwd);
        user.setSalt(salt);
        user.setCreateTime(now);
        userDao.addUser(user);
        //5.注册用户信息表
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setNick(UserConstant.DEFAULT_NICK_NAME);
        userInfo.setBirth(UserConstant.DEFAULT_BIRTH);
        userInfo.setGender(UserConstant.GENDER_UNKNOW);
        userInfo.setCreateTime(now);
        userInfoDao.addUserInfo(userInfo);
    }

    private User getUserByPhone(String phone) {
        User user = userDao.getUserByPhone(phone);
        return user;
    }

    public String login(User user) {
        //1.参数校验
        if (user == null){
            throw new ConditionException("请求参数不正确！");
        }
        String phone = user.getPhone();
        if (StringUtils.isNullOrEmpty(phone)){
            throw new ConditionException("手机号不能为空");
        }
        User dbUser = this.getUserByPhone(phone);
        if (dbUser == null) {
            throw new ConditionException("手机号尚未注册!");
        }
        String password = user.getPassword();
        //2.密码解密及校验
        String decryptPwd;
        try {
            decryptPwd = RSAUtil.decrypt(password);
        } catch (Exception e){
            throw new ConditionException("密码解密失败！");
        }
        String salt = dbUser.getSalt();
        String signPwd = MD5Util.sign(decryptPwd, salt, "UTF-8");
        if (!signPwd.equals(dbUser.getPassword())){
            throw new ConditionException("密码错误！");
        }
        //3.生成用户登录token
        String token;
        try {
            token = TokenUtil.generateToken(dbUser.getId());
        } catch (Exception e) {
            throw new ConditionException("获取token失败");
        }
        return token;
    }

    public User getUserById(Long userId) {
        User user = userDao.getUserById(userId);
        UserInfo userInfo = userInfoDao.getUserInfoById(userId);
        user.setUserInfo(userInfo);
        return user;
    }

    public Long updateUser(User user) {
        user.setUpdateTime(new Date());
        return userDao.updateUser(user);
    }

    public Long updateUserInfo(UserInfo userInfo) {
        userInfo.setUpdateTime(new Date());
        return userInfoDao.updateUserInfo(userInfo);
    }
}