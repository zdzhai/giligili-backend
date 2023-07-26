package com.zzd.giligili.controller;

import com.zzd.giligili.domain.JsonResponse;
import com.zzd.giligili.service.FileService;
import com.zzd.giligili.service.utils.FastDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

    /**
     * 获取文件的MD5加密字符串
     */
    @PostMapping("/md5files")
    public JsonResponse<String> getFileMD5(MultipartFile file) throws IOException {
        String fileMD5 = fileService.getFileMD5(file);
        return new JsonResponse<>(fileMD5);
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
}
