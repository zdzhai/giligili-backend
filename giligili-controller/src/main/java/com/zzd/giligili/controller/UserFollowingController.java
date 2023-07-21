package com.zzd.giligili.controller;

import com.zzd.giligili.controller.support.UserSupport;
import com.zzd.giligili.domain.FollowingGroup;
import com.zzd.giligili.domain.JsonResponse;
import com.zzd.giligili.domain.UserFollowing;
import com.zzd.giligili.domain.constant.UserConstant;
import com.zzd.giligili.domain.exception.ConditionException;
import com.zzd.giligili.service.FollowingGroupService;
import com.zzd.giligili.service.UserFollowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author dongdong
 * @Date 2023/7/19 21:50
 */
@RestController
public class UserFollowingController {

    @Autowired
    private UserFollowingService userFollowingService;

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private FollowingGroupService followingGroupService;

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
        Long userId = userSupport.getUserId();
        userFollowing.setUserId(userId);
        Long addId = userFollowingService.addUserFollowing(userFollowing);
        return JsonResponse.success(String.valueOf(addId));
    }

    /**
     * 获取用户关注信息
     */
    @GetMapping("/user-followings")
    public JsonResponse<List<FollowingGroup>> getUserFollowing(){
        Long userId = userSupport.getUserId();
        List<FollowingGroup> userFollowings = userFollowingService.getUserFollowings(userId);
        return new JsonResponse<>(userFollowings);
    }

    /**
     * 获取用户粉丝信息
     */
    @GetMapping("/user-fans")
    public JsonResponse<List<UserFollowing>> getUserFans(){
        Long userId = userSupport.getUserId();
        List<UserFollowing> userFans = userFollowingService.getUserFans(userId);
        return new JsonResponse<>(userFans);
    }

    /**
     * 新增用户关注列表
     * @param followingGroup
     * @return
     */
    @PostMapping("/user-following-groups")
    public JsonResponse<Long> addUserFollowingGroup(@RequestBody FollowingGroup followingGroup){
        Long userId = userSupport.getUserId();
        followingGroup.setUserId(userId);
        followingGroup.setType(UserConstant.DEFAULT_USER_GROUP_NEW);
        Long groupId = followingGroupService.addUserFollowingGroup(followingGroup);
        return new JsonResponse<>(groupId);
    }

    /**
     * 获取用户关注分组信息
     * @return
     */
    @GetMapping("/user-following-groups")
    public JsonResponse<List<FollowingGroup>> getUserFollowingGroups(){
        Long userId = userSupport.getUserId();
        List<FollowingGroup> groupList = followingGroupService.getUserFollowingGroup();
        return new JsonResponse<>(groupList);
    }

}
