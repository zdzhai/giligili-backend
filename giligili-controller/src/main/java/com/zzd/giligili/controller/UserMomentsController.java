package com.zzd.giligili.controller;

import com.zzd.giligili.controller.support.UserSupport;
import com.zzd.giligili.domain.JsonResponse;
import com.zzd.giligili.domain.UserMoments;
import com.zzd.giligili.domain.annotation.ApiLimitedRole;
import com.zzd.giligili.domain.annotation.DataLimited;
import com.zzd.giligili.domain.constant.AuthRoleConstant;
import com.zzd.giligili.service.UserMomentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author dongdong
 * @Date 2023/7/21 13:59
 */
@RestController
public class UserMomentsController {

    @Autowired
    private UserMomentsService userMomentsService;

    @Autowired
    private UserSupport userSupport;

    /**
     * 新增用户动态并发送给MQ
     * @param userMoments
     * @return
     */
    @ApiLimitedRole(limitedRoleCodeList = {AuthRoleConstant.ROLE_CODE_LV0})
    @DataLimited
    @PostMapping("/user-moments")
    public JsonResponse<String> addUserMoments(@RequestBody UserMoments userMoments) throws Exception {
        Long userId = userSupport.getUserId();
        userMoments.setUserId(userId);
        userMomentsService.addUserMoments(userMoments);
        return JsonResponse.success();
    }

    /**
     * 获取用户关注的用户动态
     * @return
     */
    @GetMapping("/user-subscribed-moments")
    public JsonResponse<List<UserMoments>> getUserSubscribedMoments(){
        Long userId = userSupport.getUserId();
        List<UserMoments> userMomentsList = userMomentsService.getUserSubscribedMoments(userId);
        return new JsonResponse<>(userMomentsList);
    }

}
