package com.zzd.giligili.dao;

import com.zzd.giligili.domain.VideoView;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * @author dongdong
 * @Date 2023/7/25 16:26
 */
@Mapper
public interface VideoViewDao {

    /**
     * 查询观看记录
     * @param params
     * @return
     */
    VideoView getVideoView(Map<String, Object> params);

    /**
     * 添加观看记录
     * @param videoView
     */
    void addVideoView(VideoView videoView);

    /**
     * 根据视频id获取视频观看总量
     * @param videoId
     * @return
     */
    Long getVideoViewCount(Long videoId);
}
