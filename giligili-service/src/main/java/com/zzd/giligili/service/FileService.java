package com.zzd.giligili.service;

import com.zzd.giligili.dao.FileDao;
import com.zzd.giligili.domain.File;
import com.zzd.giligili.service.utils.FastDFSUtil;
import com.zzd.giligili.service.utils.MD5Util;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;

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

    /**
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
        if (!StringUtil.isNullOrEmpty(url)) {
            File file = new File();
            file.setUrl(url);
            file.setType(fastDFSUtil.getFileType(slice));
            file.setMd5(fileMd5);
            file.setCreateTime(new Date());
            fileDao.addFile(file);
        }
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

}
