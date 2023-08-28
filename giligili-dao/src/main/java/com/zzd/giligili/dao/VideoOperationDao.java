package com.zzd.giligili.dao;

import com.zzd.giligili.domain.Video;
import com.zzd.giligili.domain.VideoOperation;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 62618
* @description 针对表【t_video_operation(视频操作表)】的数据库操作Mapper
* @createDate 2023-08-20 21:59:00
*/
@Mapper
public interface VideoOperationDao {

    void addLikeOperation(VideoOperation videoOperation);

    void deleteLikeOperation(VideoOperation videoOperation);

    void addStarOperation(VideoOperation videoOperation);

    void deleteStarOperation(VideoOperation videoOperation);
}




