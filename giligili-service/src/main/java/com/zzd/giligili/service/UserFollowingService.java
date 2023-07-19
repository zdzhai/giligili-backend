package com.zzd.giligili.service;

import com.zzd.giligili.dao.UserFollowingDao;
import com.zzd.giligili.domain.FollowingGroup;
import com.zzd.giligili.domain.User;
import com.zzd.giligili.domain.UserFollowing;
import com.zzd.giligili.domain.constant.UserConstant;
import com.zzd.giligili.domain.exception.ConditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

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
        Long userId = userFollowing.getuserId();
        userFollowingDao.deleteUserFollowing(userId, followingId);
        userFollowing.setCreateTime(new Date());
        Long addId = userFollowingDao.addUserFollowing(userFollowing);
        return addId;
    }
}




