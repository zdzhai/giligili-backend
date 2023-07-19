package com.zzd.giligili.dao;

/**
 * @author dongdong
 * @Date 2023/7/19 20:43
 */

import com.zzd.giligili.domain.UserFollowing;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author 62618
 * @description 针对表【t_user_following(用户关注表)】的数据库操作Mapper
 * @createDate 2023-07-19 20:38:21
 * @Entity generator.domain.TUserFollowing
 */
@Mapper
public interface UserFollowingDao {

    /**
     * 根据用户id和关注的用户id删除数据
     * @param userId
     * @param followingId
     */
    void deleteUserFollowing(@Param("userId") Long userId, @Param("followingId") Long followingId);

    /**
     * 添加用户分组信息
     * @param userFollowing
     */
    Long addUserFollowing(UserFollowing userFollowing);
}