package com.zzd.giligili.controller;

import com.zzd.giligili.controller.support.UserSupport;
import com.zzd.giligili.domain.*;
import com.zzd.giligili.domain.exception.ConditionException;
import com.zzd.giligili.service.ElasticSearchService;
import com.zzd.giligili.service.VideoService;
import org.apache.avro.data.Json;
import org.apache.mahout.cf.taste.common.TasteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author dongdong
 * @Date 2023/7/25 16:25
 */
@RestController
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private ElasticSearchService elasticSearchService;

    /**
     * 获取视频信息
     * @param videoId
     * @return
     */
    @GetMapping("/video")
    public JsonResponse<Video> getVideoById(@RequestParam Long videoId) {
        Video video = videoService.getVideoById(videoId);
        return new JsonResponse<>(video);
    }

    /**
     * 根据userId获取用户所属视频信息
     * @return
     */
    @GetMapping("/my-videos")
    public JsonResponse<List<Video>> getVideosByUserId() {
        Long userId = userSupport.getUserId();
        List<Video> videoList = videoService.getVideosByUserId(userId);
        return new JsonResponse<>(videoList);
    }

    /**
     * 根据userId获取用户收藏视频信息
     * @return
     */
    @GetMapping("/star-videos")
    public JsonResponse<List<Video>> getStarVideosByUserId() {
        Long userId = userSupport.getUserId();
        List<Video> videoList = videoService.getStarVideosByUserId(userId);
        return new JsonResponse<>(videoList);
    }

    /**
     * 添加视频信息
     * @param video
     * @return
     */
    @PostMapping("/videos")
    public JsonResponse<String> addVideo(@RequestBody Video video) {
        Long userId = userSupport.getUserId();
        video.setUserId(userId);
        videoService.addVideos(video);
        //往es中添加视频数据
        elasticSearchService.addVideo(video);
        return JsonResponse.success();
    }

    /**
     * 按照分区对视频进行分页查询
     * @param pageNum
     * @param pageSize
     * @param area
     * @return
     */
    @GetMapping("/videos")
    public JsonResponse<PageResult<Video>> pageListVideos(Integer pageNum,
                                                          Integer pageSize,
                                                          String area){
        PageResult<Video> result = videoService.pageListVideos(pageNum, pageSize, area);
        return new JsonResponse<>(result);
    }

    /**
     * 通过分片在线观看视频（分片获取视频资源）
     * @param request
     * @param response
     * @param path
     */
    @GetMapping("/videos-online")
    public void viewVideoOnlineBySlices(HttpServletRequest request,
                                        HttpServletResponse response,
                                        String path) throws Exception {
        videoService.viewVideoOnlineBySlices(request, response, path);
    }

    //点赞 取消点赞 查询当前视频点赞数

    /**
     * 点赞
     * @param videoId
     * @return
     */
    @PostMapping("/video-likes")
    public JsonResponse<String> addVideoLikes(@RequestParam Long videoId) {
        Long userId = userSupport.getUserId();
        videoService.addVideoLikes(userId, videoId);
        return JsonResponse.success();
    }

    /**
     * 取消点赞
     * @param videoId
     * @return
     */
    @DeleteMapping("/video-likes")
    public JsonResponse<String> deleteVideoLikes(@RequestParam Long videoId) {
        Long userId = userSupport.getUserId();
        videoService.deleteVideoLikes(userId, videoId);
        return JsonResponse.success();
    }

    /**
     * 查询视频的总点赞数
     * @param videoId
     * @return
     */
    @GetMapping("/video-likes")
    public JsonResponse<Map<String, Object>> getVideoLikes(@RequestParam Long videoId) {
        Long userId = null;
        try {
            userId = userSupport.getUserId();
        } catch (Exception e) {}
        Map<String, Object> result = videoService.getVideoLikes(userId,videoId);
        return new JsonResponse<>(result);
    }

    //收藏 取消收藏 查询当前视频收藏数
    /**
     * 收藏
     * @param videoCollection
     * @return
     */
    @PostMapping("/video-collections")
    public JsonResponse<String> addVideoCollections(
            @RequestBody VideoCollection videoCollection) {
        Long userId = userSupport.getUserId();
        videoCollection.setUserId(userId);
        videoCollection.setGroupId(0L);
        videoService.addVideoCollections(videoCollection);
        return JsonResponse.success();
    }

    /**
     * 取消收藏
     * @param videoId
     * @return
     */
    @DeleteMapping("/video-collections")
    public JsonResponse<String> deleteVideoCollections(
            @RequestParam Long videoId
    ) {
        Long userId = userSupport.getUserId();
        videoService.deleteVideoCollections(videoId, userId);
        return JsonResponse.success();
    }

    /**
     * 查询视频的总收藏数
     * @param videoId
     * @return
     */
    @GetMapping("/video-collections")
    public JsonResponse<Map<String, Object>> getVideoCollections(@RequestParam Long videoId) {
        Long userId = null;
        try {
            userId = userSupport.getUserId();
        } catch (Exception e) {}
        Map<String, Object> result = videoService.getVideoCollections(userId,videoId);
        return new JsonResponse<>(result);
    }
    //投币 查询当前视频投币数
    /**
     * 投币
     * @param videoCoin
     * @return
     */
    @PostMapping("/video-coins")
    public JsonResponse<String> addVideoCoins(@RequestBody VideoCoin videoCoin) {
        Long userId = userSupport.getUserId();
        videoCoin.setUserId(userId);
        videoService.addVideoCoins(videoCoin);
        return JsonResponse.success();
    }

    /**
     * 查询视频的总投币数
     * @param videoId
     * @return
     */
    @GetMapping("/video-coins")
    public JsonResponse<Map<String, Object>> getVideoCoins(@RequestParam Long videoId) {
        Long userId = null;
        try {
            userId = userSupport.getUserId();
        } catch (Exception e) {}
        Map<String, Object> result = videoService.getVideoCoins(userId,videoId);
        return new JsonResponse<>(result);
    }

    /**
     * 获取视频评论功能（待完善）
     */

    /**
     * 获取视频详细信息
     * @param videoId
     * @return
     */
    @GetMapping("/video-details")
    public JsonResponse<Map<String, Object>> getVideoDetails(@RequestParam Long videoId) {
        Map<String, Object> videoDetails = videoService.getVideoDetails(videoId);
        return new JsonResponse<>(videoDetails);
    }

    /**
     * 区分游客和用户添加观看记录
     * @param videoView
     * @param request
     * @return
     */
    @PostMapping("/video-view")
    public JsonResponse<String> addVideoView(@RequestBody VideoView videoView,
                                             HttpServletRequest request) {
        Long userId;
        try {
            userId = userSupport.getUserId();
            videoView.setUserId(userId);
        } catch (Exception e ){ }
        finally {
            videoService.addVideoView(videoView, request);
        }
        return JsonResponse.success();
    }

    /**
     * 根据视频id获取视频观看总量
     * @param videoId
     * @return
     */
    @GetMapping("/video-view-count")
    public JsonResponse<Long> getVideoViewCount(Long videoId) {
        if (videoId == null || videoId <= 0) {
            throw new ConditionException("参数异常!");
        }
        Long count = videoService.getVideoViewCount(videoId);
        return new JsonResponse<>(count);
    }

    /**
     * 获取首页视频推荐信息
     * @return
     * @throws TasteException
     */
    @GetMapping("/video-recommend")
    public JsonResponse<List<Video>> getVideoRecommend() throws TasteException {
        Long userId = 11L;
        try {
            userId = userSupport.getUserId();
        } catch (Exception e) {}
        List<Video> list = videoService.recommend(userId);
        return new JsonResponse<>(list);
    }


}
