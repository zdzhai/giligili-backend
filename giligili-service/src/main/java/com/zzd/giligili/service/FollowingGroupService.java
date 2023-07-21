package com.zzd.giligili.service;

import com.zzd.giligili.dao.FollowingGroupDao;
import com.zzd.giligili.domain.FollowingGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author 62618
* @description 针对表【t_following_group(用户关注分组表)】的数据库操作Service实现
* @createDate 2023-07-19 20:31:06
*/
@Service
public class FollowingGroupService {

    @Resource
    private FollowingGroupDao followingGroupDao;

    /**
     * 根据分组类型获取分组信息
     * @param groupType
     * @return
     */
    public FollowingGroup getFollowingGroupByType(Integer groupType) {
        FollowingGroup followingGroup = followingGroupDao.getFollowingGroupByType(groupType);
        return followingGroup;
    }
    /**
     * 根据分组Id获取分组信息
     * @param groupId
     * @return
     */
    public FollowingGroup getFollowingGroupByGroupId(Long groupId) {
        FollowingGroup followingGroup = followingGroupDao.getFollowingGroupByGroupId(groupId);
        return followingGroup;
    }

    /**
     * 根据用户id查询用户所有分类信息
     * @param userId
     * @return
     */
    public List<FollowingGroup> getFollowingGroupByUserId(Long userId) {
        return followingGroupDao.getFollowingGroupByUserId(userId);

    }

    /**
     * 新增用户分组信息
     * @param followingGroup
     * @return
     */
    public Long addUserFollowingGroup(FollowingGroup followingGroup) {
        return followingGroupDao.addUserFollowingGroup(followingGroup);
    }

    /**
     * 获取用户关注分组信息
     * @return
     */
    public List<FollowingGroup> getUserFollowingGroup() {
        return followingGroupDao.getUserFollowingGroup();
    }
}




