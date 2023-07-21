package com.zzd.giligili.dao;

import com.zzd.giligili.domain.FollowingGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author 62618
* @description 针对表【t_following_group(用户关注分组表)】的数据库操作Mapper
* @createDate 2023-07-19 20:31:06
* @Entity com.zzd.giligili.domain.FollowingGroup
*/
@Mapper
public interface FollowingGroupDao {

    FollowingGroup getFollowingGroupByType(Integer groupType);

    FollowingGroup getFollowingGroupByGroupId(Long groupId);


    List<FollowingGroup> getFollowingGroupByUserId(Long userId);

    Long addUserFollowingGroup(FollowingGroup followingGroup);

    List<FollowingGroup> getUserFollowingGroup();
}




