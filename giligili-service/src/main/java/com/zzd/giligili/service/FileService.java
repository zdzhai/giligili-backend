package com.zzd.giligili.service;
import com.google.common.collect.Lists;

import com.zzd.giligili.dao.FileDao;
import com.zzd.giligili.dao.VideoDao;
import com.zzd.giligili.domain.File;
import com.zzd.giligili.domain.JsonResponse;
import com.zzd.giligili.domain.Video;
import com.zzd.giligili.domain.exception.ConditionException;
import com.zzd.giligili.service.utils.FastDFSUtil;
import com.zzd.giligili.service.utils.MD5Util;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.locks.Condition;

/**
 * @author dongdong
 * @Date 2023/7/25 15:14
 */
@Service
public class FileService {

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Resource
    private FileDao fileDao;

    @Resource
    private VideoDao videoDao;

    @Autowired
    private ElasticSearchService elasticSearchService;

    /**
     * 通过分片上传文件
     * 通过MD5判断来实现文件秒传
     * @param slice
     * @param fileMd5
     * @param sliceNum
     * @param sliceTot
     * @return
     * @throws IOException
     */
    public String uploadFileBySlices(MultipartFile slice,
                                     String fileMd5,
                                     Integer sliceNum,
                                     Integer sliceTot) throws IOException {
        File dbFileByMD5 = fileDao.getFileByMD5(fileMd5);
        if (dbFileByMD5 != null) {
            return dbFileByMD5.getUrl();
        }
        String url = fastDFSUtil.uploadFileBySlices(slice, fileMd5, sliceNum, sliceTot);
        Date now = new Date();
        String fileType = fastDFSUtil.getFileType(slice);
        return savaFileToDB(fileType, url, fileMd5, now);
    }

    /**
     * 直接上传文件
     * 通过MD5判断来实现文件秒传
     * @param file
     * @return
     * @throws IOException
     */
    @Transactional
    public String uploadCommonFile(MultipartFile file,
                                   Video video) throws IOException {
        if (StringUtil.isNullOrEmpty(video.getTitle())) {
            throw new ConditionException("视频上传失败");
        }
        String fileMd5 = getFileMD5(file);
        File dbFileByMD5 = fileDao.getFileByMD5(fileMd5);
        if (dbFileByMD5 != null) {
            return dbFileByMD5.getUrl();
        }
        String url = fastDFSUtil.uploadCommonFile(file);
        //保存文件信息到DB
        Date now = new Date();
        String fileType = fastDFSUtil.getFileType(file);
        savaFileToDB(fileType, url, fileMd5, now);
        //保存视频信息到DB
        video.setCreateTime(now);
        video.setUrl(url);
        video.setThumbnail("https://plus.unsplash.com/premium_photo-1673239605865-bfcab01dca06?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=300&ixid=MnwxfDB8MXxyYW5kb218MHx8fHx8fHx8MTY5MjUzODU5MQ&ixlib=rb-4.0.3&q=80&w=400");
        videoDao.addVideo(video);
        //往es中添加视频数据
        elasticSearchService.addVideo(video);
        return url;
    }

    /**
     * 获取文件的MD5加密值
     * @param file
     * @return
     * @throws IOException
     */
    public String getFileMD5(MultipartFile file) throws IOException {
        return MD5Util.getFileMD5(file);
    }

    /**
     * 把文件信息保存到db
     * @param url
     * @return
     */
    public String savaFileToDB(String fileType,String url, String fileMd5, Date now) {
        if (!StringUtil.isNullOrEmpty(url)) {
            File file = new File();
            file.setUrl(url);
            file.setType(fileType);
            file.setMd5(fileMd5);
            file.setCreateTime(now);
            fileDao.addFile(file);
        }
        return url;
    }

    /**
     * 根据视频url删除文件和视频
     * @param url
     */
    @Transactional(rollbackFor = {ConditionException.class})
    public void deleteFile(
            String url) {
        fastDFSUtil.deleteFile(url);
        videoDao.deleteVideoByUrl(url);
        fileDao.deleteFileByUrl(url);
        elasticSearchService.deleteVideo(url);
    }
}
