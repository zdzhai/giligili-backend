package com.zzd.giligili.service;

import com.zzd.giligili.dao.VideoViewDao;
import com.zzd.giligili.domain.VideoView;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author dongdong
 * @Date 2023/7/25 16:26
 */
@Service
public class VideoViewService {

    @Resource
    private VideoViewDao videoViewDao;


    /**
     * 查询观看记录
     * @param params
     * @return
     */
    public VideoView getVideoView(Map<String, Object> params) {
        return videoViewDao.getVideoView(params);
    }

    /**
     * 添加观看记录
     * @param videoView
     */
    public void addVideoView(VideoView videoView) {
        videoViewDao.addVideoView(videoView);
    }

    /**
     * 根据视频id获取视频观看总量
     * @param videoId
     * @return
     */
    public Long getVideoViewCount(Long videoId){
        return videoViewDao.getVideoViewCount(videoId);
    }
}
