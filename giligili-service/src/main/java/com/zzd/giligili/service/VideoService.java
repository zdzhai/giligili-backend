package com.zzd.giligili.service;

import com.zzd.giligili.dao.VideoDao;
import com.zzd.giligili.domain.*;
import com.zzd.giligili.domain.exception.ConditionException;
import com.zzd.giligili.service.utils.FastDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private UserCoinService userCoinService;

    @Autowired
    private UserService userService;

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

    /**
     * 点赞
     * @param userId
     * @param videoId
     */
    public void addVideoLikes(Long userId, Long videoId) {
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频!");
        }
        VideoLike videoLike = videoDao.getVideoLikeByVideoIdAndUserId(videoId, userId);
        if (videoLike != null) {
            throw new ConditionException("已点赞!");
        }
        videoLike = new VideoLike();
        videoLike.setUserId(userId);
        videoLike.setVideoId(videoId);
        videoLike.setCreateTime(new Date());
        videoDao.addVideoLike(videoLike);
    }

    /**
     * 取消点赞
     * @param userId
     * @param videoId
     */
    public void deleteVideoLikes(Long userId, Long videoId) {
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频!");
        }
        VideoLike videoLike = videoDao.getVideoLikeByVideoIdAndUserId(videoId, userId);
        if (videoLike == null) {
            throw new ConditionException("未点赞!");
        }
        videoDao.deleteVideoLike(videoId, userId);
    }

    /**
     * 查询视频的总点赞数
     * @param userId
     * @param videoId
     * @return
     */
    public Map<String, Object> getVideoLikes(Long userId, Long videoId) {
        Long countLikes = videoDao.getVideoLikes(videoId);
        VideoLike videoLike = videoDao.getVideoLikeByVideoIdAndUserId(videoId, userId);
        boolean like = videoLike != null;
        HashMap<String, Object> result = new HashMap<>(2);
        result.put("count", countLikes);
        result.put("like", like);
        return  result;
    }

    /**
     * 收藏
     * @param videoCollection
     */
    @Transactional
    public void addVideoCollections(VideoCollection videoCollection) {
        //参数校验
        Long videoId = videoCollection.getVideoId();
        Long groupId = videoCollection.getGroupId();
        Long userId = videoCollection.getUserId();
        if (videoId == null || groupId == null) {
            throw new ConditionException("参数异常!");
        }
        //查询视频收藏视频是否存在
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("收藏视频不存在!");
        }
        //删除原有视频收藏记录
        videoDao.deleteVideoCollection(videoId, userId);
        //添加新收藏记录
        videoCollection.setCreateTime(new Date());
        videoDao.addVideoCollection(videoCollection);
    }

    /**
     * 取消收藏
     * @param videoId
     * @param userId
     */
    public void deleteVideoCollections(Long videoId, Long userId) {
        //参数校验
        if (videoId == null) {
            throw new ConditionException("参数异常!");
        }
        videoDao.deleteVideoCollection(videoId, userId);
    }

    /**
     *
     * @param userId
     * @param videoId
     * @return
     */
    public Map<String, Object> getVideoCollections(Long userId, Long videoId) {
        Long countCollections = videoDao.getVideoCollections(videoId);
        VideoCollection videoCollection = videoDao.getVideoCollectionByVideoIdAndUserId(videoId, userId);
        boolean like = videoCollection != null;
        HashMap<String, Object> result = new HashMap<>(2);
        result.put("count", countCollections);
        result.put("like", like);
        return  result;
    }

    /**
     * 投币
     * @param videoCoin
     */
    @Transactional
    public void addVideoCoins(VideoCoin videoCoin) {
        //参数校验
        Long videoId = videoCoin.getVideoId();
        Integer amount = videoCoin.getAmount();
        Long userId = videoCoin.getUserId();
        if (videoId == null) {
            throw new ConditionException("参数异常!");
        }
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频!");
        }
        //用户拥有的币数量 > 用户要投的币数量比较
        Integer userCoinsAmount = userCoinService.getUserCoinsAmount(userId);
        userCoinsAmount = userCoinsAmount == null ? 0 : userCoinsAmount;
        if (amount > userCoinsAmount) {
            throw new ConditionException("用户硬币不足!");
        }
        VideoCoin dbVideoCoin = videoDao.getVideoCoinByVideoIdAndUserId(videoId, userId);
        //根据userId,videoId,更新用户投币数
        if (dbVideoCoin == null) {
            videoCoin.setCreateTime(new Date());
            videoDao.addVideoCoin(videoCoin);
        } else {
            amount += dbVideoCoin.getAmount();
            videoCoin.setAmount(amount);
            videoCoin.setUpdateTime(new Date());
            videoDao.updateVideoCoin(videoCoin);
        }
        //更新用户当前硬币数
        userCoinService.updateUserCoinsAmount(userId, userCoinsAmount - amount);
    }

    /**
     * 获取视频总投币数
     * @param userId
     * @param videoId
     * @return
     */
    public Map<String, Object> getVideoCoins(Long userId, Long videoId) {
        //获取视频总硬币数
        Long countCoins = videoDao.getVideoCoinsAmount(videoId);
        //查看当前用户是否已投币
        VideoCoin videoCoin = videoDao.getVideoCoinByVideoIdAndUserId(videoId, userId);
        boolean like = videoCoin != null;
        HashMap<String, Object> result = new HashMap<>(2);
        result.put("count", countCoins);
        result.put("like", like);
        return  result;
    }

    /**
     * 获取视频评论功能（待完善）
     */

    /**
     * 获取视频详细信息
     * @param videoId
     * @return
     */
    public Map<String, Object> getVideoDetails(Long videoId) {
        if (videoId == null) {
            throw new ConditionException("参数异常!");
        }
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频!");
        }
        Long userId = video.getUserId();
        User user = userService.getUserById(userId);
        UserInfo userInfo = user.getUserInfo();
        HashMap<String, Object> result = new HashMap<>(2);
        result.put("video", video);
        result.put("userInfo", userInfo);
        return  result;
    }
}
