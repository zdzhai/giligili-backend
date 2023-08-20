package com.zzd.giligili.dao;

import com.zzd.giligili.domain.File;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author dongdong
 * @Date 2023/7/25 15:34
 */
@Mapper
public interface FileDao {

    /**
     * 添加用户上传文件信息
     * @param file
     */
    void addFile(File file);

    /**
     * 根据MD5查询文件信息
     * @param md5
     * @return
     */
    File getFileByMD5(String md5);

    void deleteFileByUrl(String url);
}
