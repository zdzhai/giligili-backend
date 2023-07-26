package com.zzd.giligili.service;

import com.zzd.giligili.dao.VideoDao;
import com.zzd.giligili.domain.PageResult;
import com.zzd.giligili.domain.Video;
import com.zzd.giligili.domain.VideoTag;
import com.zzd.giligili.domain.exception.ConditionException;
import com.zzd.giligili.service.utils.FastDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author dongdong
 * @Date 2023/7/25 16:26
 */
@Service
public class VideoService {

    @Resource
    private VideoDao videoDao;

    @Autowired
    private FastDFSUtil fastDFSUtil;

    /**
     * 添加用户视频信息
     * @param video
     */
    @Transactional
    public void addVideos(Video video) {
        Date now = new Date();
        video.setCreateTime(now);
        videoDao.addVideo(video);
        Long videoId = video.getId();
        List<VideoTag> videoTagList = video.getVideoTagList();
        videoTagList.stream().forEach(item -> {
            item.setCreateTime(now);
            item.setVideoId(videoId);
        });
        videoDao.batchAddVideoTags(videoTagList);
    }

    /**
     * 根据分区分页查询视频
     * @param pageNum
     * @param pageSize
     * @param area
     * @return
     */
    public PageResult<Video> pageListVideos(Integer pageNum, Integer pageSize, String area) {
        if (pageNum == null || pageSize == null) {
            throw new ConditionException("参数异常!");
        }
        Map<String, Object> map = new HashMap<>();
        Integer start = (pageNum - 1) * pageSize;
        Integer limit = pageSize;
        map.put("start",start);
        map.put("limit",limit);
        map.put("area",area);
        List<Video> videoList = new ArrayList<>();
        Integer total = videoDao.countVideos(map);
        if (total > 0) {
            videoList = videoDao.pageListVideos(map);
        }
        return new PageResult<>(videoList, Long.valueOf(total));
    }

    /**
     * 通过分片在线观看视频（分片获取视频资源）
     * @param request
     * @param response
     * @param path
     */
    public void viewVideoOnlineBySlices(HttpServletRequest request,
                                        HttpServletResponse response,
                                        String path) throws Exception {
        fastDFSUtil.viewVideoOnlineBySlices(request, response , path);
    }
}
