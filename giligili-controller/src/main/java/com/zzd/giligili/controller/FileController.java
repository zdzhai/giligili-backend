package com.zzd.giligili.controller;

import com.google.common.collect.Lists;
import com.zzd.giligili.controller.support.UserSupport;
import com.zzd.giligili.domain.JsonResponse;
import com.zzd.giligili.domain.Video;
import com.zzd.giligili.domain.constant.FileConstant;
import com.zzd.giligili.service.FileService;
import com.zzd.giligili.service.utils.FastDFSUtil;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

/**
 * @author dongdong
 * @Date 2023/7/25 14:47
 */
@RestController
public class FileController {

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Autowired
    private FileService fileService;

    @Autowired
    private UserSupport userSupport;
    /**
     * 获取文件的MD5加密字符串
     */
    @PostMapping("/md5files")
    public JsonResponse<String> getFileMD5(@RequestPart(value = "file")MultipartFile file) throws IOException {
        String fileMD5 = fileService.getFileMD5(file);
        return new JsonResponse<>(fileMD5);
    }

    /**
     * 对文件进行分片并临时保存在本地
     * @param file
     * @throws IOException
     */
    @GetMapping("/slices")
    public void slices(@RequestParam(value = "file") MultipartFile file) throws IOException {
        fastDFSUtil.convertFileToSlices(file);
    }

    /**
     * 通过切片上传文件
     */
    @PutMapping("/file-slices")
    public JsonResponse<String> uploadFileBySlices(MultipartFile slice,
                                                   String fileMd5,
                                                   Integer sliceNum,
                                                   Integer sliceTot) throws IOException {
        String filePath = fileService.uploadFileBySlices(slice, fileMd5, sliceNum, sliceTot);
        return new JsonResponse<>(filePath);
    }

    /**
     * 直接上传文件
     * @param file
     * @param title
     * @param area
     * @param description
     * @return
     * @throws IOException
     */
    @PutMapping("/file-common")
    public JsonResponse<String> uploadCommonFile(@RequestParam(value = "file")MultipartFile file,
                                                 String title,
                                                 String area,
                                                 String description) throws IOException {
        Long userId = userSupport.getUserId();
        Video video = new Video();
        if (!StringUtil.isNullOrEmpty(title) &&
                !StringUtil.isNullOrEmpty(area) &&
                !StringUtil.isNullOrEmpty(description)) {
            video.setUserId(userId);
            video.setTitle(title);
            video.setThumbnail("");
            video.setType("0");
            video.setDuration("1314");
            video.setArea(area);
            video.setDescription(description);
        }
        String filePath = fileService.uploadCommonFile(file, video);
        //保存视频信息
        return new JsonResponse<>(filePath);
    }

    /**
     * 根据视频url删除视频
     * @param url
     * @return
     */
    @DeleteMapping("/file-delete")
    public JsonResponse<String> deleteFile(
            @RequestParam String url) {
        fileService.deleteFile(FileConstant.GROUP1 + "/" + url);
        return JsonResponse.success();
    }
}
