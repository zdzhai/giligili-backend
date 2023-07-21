package com.zzd.giligili.service;

import com.zzd.giligili.dao.UserFollowingDao;
import com.zzd.giligili.domain.FollowingGroup;
import com.zzd.giligili.domain.User;
import com.zzd.giligili.domain.UserFollowing;
import com.zzd.giligili.domain.UserInfo;
import com.zzd.giligili.domain.constant.UserConstant;
import com.zzd.giligili.domain.exception.ConditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author 62618
* @description 针对表【t_user_following(用户关注表)】的数据库操作Service实现
* @createDate 2023-07-19 20:38:21
*/
@Service
public class UserFollowingService {

    @Resource
    private UserFollowingDao userFollowingDao;

    @Autowired
    private FollowingGroupService followingGroupService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 添加用户关注信息
     */
    @Transactional
    public Long addUserFollowing(UserFollowing userFollowing){
        //1.先获取groupId
        Long groupId = userFollowing.getGroupId();
        //1.1如果为空则选择默认的分组
        if (groupId == null) {
            FollowingGroup followingGroup = followingGroupService.getFollowingGroupByType(UserConstant.DEFAULT_GROUP_TYPE);
            userFollowing.setGroupId(followingGroup.getId());
        } else {
            //todo 1.2如果不为空则先判断是不是 0/1/2
            //1.3不是的话根据groupId查询看分组是否存在
            //todo 如果是个人创建的分组 这里应该是根据groupId和userId一起查
            FollowingGroup followingGroup = followingGroupService.getFollowingGroupByGroupId(groupId);
            if (followingGroup == null) {
                throw new ConditionException("用户分组不存在！");
            }
        }
        //2.获取followingId用户看是否存在
        Long followingId = userFollowing.getFollowingId();
        User followUser = userService.getUserById(followingId);
        if (followUser == null) {
            throw new ConditionException("关注用户不存在！");
        }
        //3.存在的话就先查userFollowing库删除数据，再进行添加
        Long userId = userFollowing.getUserId();
        userFollowingDao.deleteUserFollowing(userId, followingId);
        userFollowing.setCreateTime(new Date());
        return userFollowingDao.addUserFollowing(userFollowing);
    }

    /**
     * 获取用户关注分类列表
     * @param userId
     * @return
     */
    public List<FollowingGroup> getUserFollowings(Long userId){
        //1.获取关注的用户列表
        List<UserFollowing> userFollowingList = userFollowingDao.getUserFollowings(userId);
        Set<Long> followingIdSet = userFollowingList.stream().map(UserFollowing::getFollowingId).collect(Collectors.toSet());
        //2.根据id查询出用户信息
        if (followingIdSet.size() == 0){
            return new ArrayList<>();
        }
        List<UserInfo> userInfoList = new ArrayList<>();
        userInfoList = userInfoService.getUserInfoByUserIds(followingIdSet);
        for (UserFollowing userFollowing : userFollowingList){
            for (UserInfo userInfo : userInfoList) {
                if (userFollowing.getFollowingId().equals(userInfo.getUserId())){
                    userFollowing.setFollowingUserInfo(userInfo);
                }
            }
        }
        //3.将关注用户按照关注分组进行分类
        //3.1查询当前用户的所有分类
        List<FollowingGroup> followingGroupList = followingGroupService.getFollowingGroupByUserId(userId);
        FollowingGroup allGroup = new FollowingGroup();
        allGroup.setName(UserConstant.DEFAULT_ALL_USER_GROUP);
        allGroup.setFollowingUserInfoList(userInfoList);
        List<FollowingGroup> resGroup = new ArrayList<>();
        resGroup.add(allGroup);
        //3.2根据分类id和关注用户信息的groupId匹配关注用户信息
        for (FollowingGroup followingGroup : followingGroupList) {
            List<UserInfo> infoList = new ArrayList<>();
            for (UserFollowing userFollowing : userFollowingList) {
                if (followingGroup.getId().equals(userFollowing.getGroupId())){
                    infoList.add(userFollowing.getFollowingUserInfo());
                }
            }
            followingGroup.setFollowingUserInfoList(infoList);
            resGroup.add(followingGroup);
        }

        return resGroup;
    }

    /**
     * 获取用户粉丝列表
     * @param userId
     * @return
     */
    public List<UserFollowing> getUserFans(Long userId) {
        //1.获取用户粉丝列表
        List<UserFollowing> fansList = userFollowingDao.getUserFans(userId);
        Set<Long> fansIdSet = fansList.stream().map(UserFollowing::getUserId).collect(Collectors.toSet());
        //2.根据ids查询粉丝信息
        if (fansIdSet.size() == 0){
            throw new ConditionException("暂无粉丝！");
        }
        List<UserInfo> userInfoList = new ArrayList<>();
        userInfoList = userInfoService.getUserInfoByUserIds(fansIdSet);
        //3.查询当前用户是否关注该粉丝
        List<UserFollowing> followingList = userFollowingDao.getUserFollowings(userId);

        for (UserFollowing fan : fansList){
            for (UserInfo userInfo : userInfoList) {
                if (fan.getUserId().equals(userInfo.getUserId())){
                    userInfo.setFollowed(false);
                    fan.setFollowingUserInfo(userInfo);
                }
            }
            for (UserFollowing userFollowing : followingList) {
                if (fan.getUserId().equals(userFollowing.getFollowingId())){
                    fan.getFollowingUserInfo().setFollowed(true);
                }
            }
        }
        return fansList;
    }

    public List<UserInfo> checkFollowingStatus(List<UserInfo> userInfoList, Long userId) {
        List<UserFollowing> userFollowingList = userFollowingDao.getUserFollowings(userId);
        for (UserInfo userInfo : userInfoList) {
            userInfo.setFollowed(false);
            for (UserFollowing userFollowing : userFollowingList) {
                if (userFollowing.getFollowingId().equals(userInfo.getUserId())){
                    userInfo.setFollowed(true);
                }
            }
        }
        return userInfoList;
    }
}




