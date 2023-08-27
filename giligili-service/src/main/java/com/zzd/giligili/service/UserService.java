package com.zzd.giligili.service;

import com.mysql.cj.util.StringUtils;
import com.zzd.giligili.dao.UserDao;
import com.zzd.giligili.dao.UserInfoDao;
import com.zzd.giligili.domain.RefreshTokenDetails;
import com.zzd.giligili.domain.User;
import com.zzd.giligili.domain.UserInfo;
import com.zzd.giligili.domain.constant.UserConstant;
import com.zzd.giligili.domain.exception.ConditionException;
import com.zzd.giligili.domain.vo.UserInfoVO;
import com.zzd.giligili.domain.vo.UserVO;
import com.zzd.giligili.service.utils.HttpUtil;
import com.zzd.giligili.service.utils.MD5Util;
import com.zzd.giligili.service.utils.RSAUtil;
import com.zzd.giligili.service.utils.TokenUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    private UserInfoService userInfoService;

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private ElasticSearchService elasticSearchService;



    @Transactional
    public Map<String, Object> addUser(User user) throws Exception {
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
        Long userId = user.getId();
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setNick(UserConstant.DEFAULT_NICK_NAME);
        userInfo.setBirth(UserConstant.DEFAULT_BIRTH);
        userInfo.setGender(UserConstant.GENDER_UNKNOW);
        userInfo.setAvatar("https://xsgames.co/randomusers/avatar.php?g=pixel&key=1");
        userInfo.setSign("唱，跳，rap,篮球，练习时长两年半的Java练习生！");
        userInfo.setCreateTime(now);
        userInfoService.addUserInfo(userInfo);
        //往es中添加用户信息数据
        elasticSearchService.addUserInfo(userInfo);
        //添加默认权限角色
        userAuthService.addUserDefaultRole(user.getId());
        //生成双token
        return generateDTS(userId);
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

    public UserVO getUserById(Long userId) {
        User user = userDao.getUserById(userId);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        UserInfoVO userInfoVO = userInfoService.getUserInfoById(userId);
        userVO.setUserInfo(userInfoVO);
        return userVO;
    }

    public Long updateUser(User user) {
        user.setUpdateTime(new Date());
        return userDao.updateUser(user);
    }

    public Long updateUserInfo(UserInfo userInfo) {
        userInfo.setUpdateTime(new Date());
        return userInfoService.updateUserInfo(userInfo);
    }

    /**
     * 双token登录
     * @param user
     * @return
     */
    public Map<String, Object> loginForDts(User user) throws Exception {
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
        Long userId = dbUser.getId();
        return generateDTS(userId);
    }

    public Map<String, Object> generateDTS(Long userId) throws Exception {
        //3.生成用户登录token
        String token;
        try {
            token = TokenUtil.generateToken(userId);
        } catch (Exception e) {
            throw new ConditionException("获取token失败");
        }
        String refreshToken = TokenUtil.generateRefreshToken(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("refreshToken", refreshToken);
        return map;
    }

    /**
     * 删除refreshToken
     * @param userId
     * @param refreshToken
     */
    public void logout(Long userId, String refreshToken) {
        userDao.deleteRefreshToken(userId, refreshToken);
    }

    public String refreshAccessToken(String refreshToken) throws Exception {
        /*RefreshTokenDetails refreshTokenDetails = userDao.getRefreshAccessToken(refreshToken);
        if (refreshTokenDetails == null) {
            throw new ConditionException("555", "token过期！");
        }*/
        Long userId = TokenUtil.verifyRefreshToken(refreshToken);
        String token;
        try {
            token = TokenUtil.generateToken(userId);
        } catch (Exception e) {
            throw new ConditionException("获取token失败");
        }
       return token;
    }

    /**
     * 根据视频videoId获取用户详细信息
     * @param videoId
     * @return
     */
    public UserInfo getUserInfoByVideoId(Long videoId) {
        return userInfoService.getUserInfoByVideoId(videoId);
    }
}
