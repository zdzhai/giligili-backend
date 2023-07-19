package com.zzd.giligili.controller;

import com.zzd.giligili.domain.JsonResponse;
import com.zzd.giligili.domain.UserFollowing;
import com.zzd.giligili.domain.exception.ConditionException;
import com.zzd.giligili.service.UserFollowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dongdong
 * @Date 2023/7/19 21:50
 */
@RestController
public class UserFollowingController {

    @Autowired
    private UserFollowingService userFollowingService;

    /**
     * 新增用户关注信息
     * @param userFollowing
     * @return
     */
    @PostMapping("/user-following")
    public JsonResponse<String> addUserFollowing(@RequestBody UserFollowing userFollowing){
        if (userFollowing == null){
            throw new ConditionException("请求参数错误！");
        }
        Long addId = userFollowingService.addUserFollowing(userFollowing);
        return JsonResponse.success(String.valueOf(addId));
    }
}
