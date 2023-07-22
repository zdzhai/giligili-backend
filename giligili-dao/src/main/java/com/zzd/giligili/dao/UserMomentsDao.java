package com.zzd.giligili.dao;

import com.zzd.giligili.domain.UserMoments;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author dongdong
 * @Date 2023/7/21 14:02
 */
@Mapper
public interface UserMomentsDao {

    /**
     * 新增用户动态
     * @param userMoments
     * @return
     */
    Long addUserMoments(UserMoments userMoments);
}
