package com.zzd.giligili.controller;

import com.zzd.giligili.domain.JsonResponse;
import com.zzd.giligili.domain.Video;
import com.zzd.giligili.service.DemoService;
import com.zzd.giligili.service.ElasticSearchService;
import com.zzd.giligili.service.FileService;
import com.zzd.giligili.service.utils.FastDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author dongdong
 * @Date 2023/7/17 22:50
 */
@RestController
public class DemoController {

    @Autowired
    private DemoService demoService;
    
    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Autowired
    private FileService fileService;

    @GetMapping("/query")
    public Long query(Long id){
        return demoService.query(id);
    }

    @GetMapping("/es-videos")
    public JsonResponse<Video> getEsVideo(@RequestParam String keyword) {
        Video video = elasticSearchService.getVideo(keyword);
        return new JsonResponse<>(video);
    }
}
