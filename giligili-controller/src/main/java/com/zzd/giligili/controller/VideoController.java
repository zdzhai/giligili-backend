package com.zzd.giligili.controller;

import com.zzd.giligili.controller.support.UserSupport;
import com.zzd.giligili.domain.JsonResponse;
import com.zzd.giligili.domain.PageResult;
import com.zzd.giligili.domain.Video;
import com.zzd.giligili.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    /**
     * 添加用户视频信息
     * @param video
     * @return
     */
    @PostMapping("/videos")
    public JsonResponse<String> addVideo(@RequestBody Video video) {
        Long userId = userSupport.getUserId();
        video.setUserId(userId);
        videoService.addVideos(video);
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

}
