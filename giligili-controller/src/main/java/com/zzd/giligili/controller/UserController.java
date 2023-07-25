package com.zzd.giligili.controller;

import com.alibaba.fastjson.JSONObject;
import com.zzd.giligili.controller.support.UserSupport;
import com.zzd.giligili.domain.*;
import com.zzd.giligili.service.UserFollowingService;
import com.zzd.giligili.service.UserInfoService;
import com.zzd.giligili.service.UserService;
import com.zzd.giligili.service.utils.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author dongdong
 * @Date 2023/7/18 15:51
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserFollowingService userFollowingService;

    /**
     * 获得rsa公钥
     * @return
     */
    @GetMapping("/rsa-pks")
    public JsonResponse<String> getRsaPublicKey(){
        String publicKeyStr = RSAUtil.getPublicKeyStr();
        return  new JsonResponse<>(publicKeyStr);
    }

    /**
     * 用户注册 添加用户及用户信息
     * @param user
     * @return
     */
    @PostMapping("/users")
    public JsonResponse<String> addUser(@RequestBody User user){
        userService.addUser(user);
        return JsonResponse.success();
    }

    /**
     * 用户登录
     */
    @PostMapping("/user-token")
    public JsonResponse<String> login(@RequestBody  User user){
        String token =  userService.login(user);
        return new JsonResponse<>(token);
    }

    /**
     * 根据用户id获取用户
     * @return
     */
    @GetMapping("/users")
    public JsonResponse<User> getUser(){
        Long userId = userSupport.getUserId();
        User user = userService.getUserById(userId);
        return new JsonResponse<>(user);
    }

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    @PutMapping("/users")
    public JsonResponse<Long> updateUser(@RequestBody User user){
        Long userId = userSupport.getUserId();
        user.setId(userId);
        Long updateId = userService.updateUser(user);
        return new JsonResponse<>(updateId);
    }

    /**
     * 更新用户基本信息
     * @param userInfo
     * @return
     */
    @PutMapping("/user-info")
    public JsonResponse<Long> updateUserInfo(@RequestBody UserInfo userInfo){
        Long userId = userSupport.getUserId();
        userInfo.setUserId(userId);
        Long updateId = userService.updateUserInfo(userInfo);
        return new JsonResponse<>(updateId);
    }

    /**
     * 分页获取用户信息
     * @return
     */
    @GetMapping("/list/user-info")
    public JsonResponse<PageResult<UserInfo>> pageListUserInfos(@RequestParam Integer pageNum,
                                                               @RequestParam Integer pageSize,
                                                               String nick){
        Long userId = userSupport.getUserId();
        JSONObject params = new JSONObject();
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        params.put("nick", nick);
        params.put("userId", userId);
        PageResult<UserInfo> result = userInfoService.pageListUserInfos(params);
        //判断查出的用户是否被当前用户关注
        if (result.getTotal() > 0){
            List<UserInfo> checkedUserInfoList = userFollowingService.checkFollowingStatus(result.getResList(), userId);
            result.setResList(checkedUserInfoList);
        }
        return new JsonResponse<>(result);
    }

    /**
     * 获取双token
     */
    @PostMapping("/user-dts")
    public JsonResponse<Map<String, Object>> loginForDts(@RequestBody User user) throws Exception {
        Map<String, Object> map = userService.loginForDts(user);
        return new JsonResponse<>(map);
    }

    /**
     * 用户退出时删除refreshToken
     * @param request
     * @return
     */
    @DeleteMapping("/delete-refresh-token")
    public JsonResponse<String> logout(HttpServletRequest request){
        String refreshToken = request.getHeader("refreshToken");
        Long userId = userSupport.getUserId();
        userService.logout(userId, refreshToken);
        return JsonResponse.success();
    }

    /**
     * 刷新accessToken
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("access-token")
    public JsonResponse<String> refreshAccessToken(HttpServletRequest request) throws Exception {
        String refreshToken = request.getHeader("refreshToken");
        String accessToken = userService.refreshAccessToken(refreshToken);
        return new JsonResponse<>(accessToken);
    }

}
