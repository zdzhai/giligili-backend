package com.zzd.giligili.service.utils;

import com.github.tobato.fastdfs.domain.fdfs.FileInfo;
import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadCallback;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.zzd.giligili.domain.constant.FileConstant;
import com.zzd.giligili.domain.exception.ConditionException;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * @author dongdong
 * @Date 2023/7/25 11:29
 */
@Component
public class FastDFSUtil {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private AppendFileStorageClient appendFileStorageClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 获取文件类型
     * @param file
     * @return
     */
    public String getFileType(MultipartFile file){
        if (file == null) {
            throw new ConditionException("非法文件！");
        }
        String originalFilename = file.getOriginalFilename();
        int index = originalFilename.lastIndexOf('.');
        return originalFilename.substring(index + 1);
    }


    /**
     * 上传一般文件（常见文件，不超过500M的文件）
     * @param file
     * @return
     */
    public String uploadCommonFile(MultipartFile file) throws IOException {
        String fileType = this.getFileType(file);
        Set<MetaData> metaDataSet = new HashSet<>();
        StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(),
                file.getSize(), fileType, metaDataSet);
        return storePath.getPath();
    }

    /**
     * 上传可以断点续传的文件
     * @param file
     * @return 文件路径
     * @throws IOException
     */
    public String uploadAppenderFile(MultipartFile file) throws IOException {
        String fileType = this.getFileType(file);
        String fileName = file.getOriginalFilename();
        StorePath storePath = appendFileStorageClient.uploadAppenderFile(FileConstant.GROUP1,
                file.getInputStream(), file.getSize(), fileType);
        return storePath.getPath();
    }

    /**
     * 继续上传文件的分片
     * @param file
     * @param filePath
     * @param offset
     * @throws IOException
     */
    public void modifyAppenderFile(MultipartFile file, String filePath, long offset) throws IOException {
        appendFileStorageClient.modifyFile(FileConstant.GROUP1, filePath, file.getInputStream(),
                file.getSize(), offset);
    }

    /**
     * 完整文件分片上传方法
     * @param file
     * @param fileMd5
     * @param sliceNum
     * @param sliceTot
     * @return
     * @throws IOException
     */
    public String uploadFileBySlices(MultipartFile file, String fileMd5,
                                     Integer sliceNum, Integer sliceTot) throws IOException {
        if (file == null || sliceNum == null || sliceTot == null) {
            throw new ConditionException("参数异常！");
        }
        //文件路径 文件大小 分片num
        String pathKey = FileConstant.PATH_KEY_PREFIX + fileMd5;
        String fileSizeKey = FileConstant.FILE_SIZE_KEY_PREFIX + fileMd5;
        String fileSliceNumKey = FileConstant.FILE_SLICE_NUM_KEY_PREFIX + fileMd5;
        String uploadSizeStr = redisTemplate.opsForValue().get(fileSizeKey);
        Long uploadSize = 0L;
        if (!StringUtil.isNullOrEmpty(uploadSizeStr)) {
            uploadSize = Long.valueOf(uploadSizeStr);
        }
        String fileType = this.getFileType(file);
        //第一个分片
        if (sliceNum == 1) {
            String filePath = this.uploadAppenderFile(file);
            if (StringUtil.isNullOrEmpty(filePath)) {
                throw new ConditionException("上传失败！");
            }
            redisTemplate.opsForValue().set(pathKey,filePath);
            redisTemplate.opsForValue().set(fileSliceNumKey, "1");
        } else {
            String filePath = redisTemplate.opsForValue().get(pathKey);
            if (StringUtil.isNullOrEmpty(filePath)) {
                throw new ConditionException("上传失败！");
            }
            //修改分片文件
            this.modifyAppenderFile(file, filePath, uploadSize);
            redisTemplate.opsForValue().increment(fileSliceNumKey);
        }
        uploadSize += file.getSize();
        redisTemplate.opsForValue().set(fileSizeKey, String.valueOf(uploadSize));
        //所有分片上传完毕，清空相关redis
        String uploadedNumStr = redisTemplate.opsForValue().get(fileSliceNumKey);
        Integer uploadedNum = Integer.valueOf(uploadedNumStr);
        String resultPath = "";
        if (sliceTot.equals(uploadedNum)) {
            resultPath = redisTemplate.opsForValue().get(pathKey);
            List<String> keyList = Arrays.asList(pathKey, fileSizeKey, fileSliceNumKey);
            redisTemplate.delete(keyList);
        }
        return  resultPath;
    }

    /**
     * 对文件进行分片
     * @param multipartFile
     * @throws IOException
     */
    public void convertFileToSlices(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String fileType = this.getFileType(multipartFile);
        File file = this.multipartFileTofFile(multipartFile);
        long fileLength = file.length();
        int count = 1;
        //先切片，然后把切片数据先保存在本地
        for (int i = 0; i < fileLength; i += FileConstant.SLICE_SIZE) {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            randomAccessFile.seek(i);
            byte[] bytes = new byte[FileConstant.SLICE_SIZE];
            int readLen = randomAccessFile.read(bytes);
            String path = "D:\\SoftWare\\Code\\giligili\\tmpfile\\" + count + "." +fileType;
            File slice = new File(path);
            FileOutputStream fos = new FileOutputStream(slice);
            fos.write(bytes, 0, readLen);
            fos.close();
            randomAccessFile.close();
            count++;
        }
        //把临时文件删除
        file.delete();
    }

    public File multipartFileTofFile(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String[] fileName = originalFilename.split("\\.");
        File file = File.createTempFile(fileName[0], "." + fileName[1]);
        multipartFile.transferTo(file);
        return file;
    }


    /**
     * 根据文件路径删除文件
     * @param path
     */
    public void deleteFile(String path){
        fastFileStorageClient.deleteFile(path);
    }


    @Value("${fdfs.http.storage-addr}")
    private String httpFdfsStorageAddr;

    /**
     * 通过分片在线观看视频（分片获取视频资源）
     * @param request
     * @param response
     * @param path
     */
    public void viewVideoOnlineBySlices(HttpServletRequest request,
                                        HttpServletResponse response,
                                        String path) throws Exception {
        FileInfo fileInfo = fastFileStorageClient.queryFileInfo(FileConstant.GROUP1, path);
        long fileSize = fileInfo.getFileSize();
        String url = httpFdfsStorageAddr + path;
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, Object> headers = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            headers.put(header, request.getHeader(header));
        }
        //获取请求头信息
        String rangeStr = request.getHeader("range");
        String[] range;
        if (StringUtil.isNullOrEmpty(rangeStr)) {
            rangeStr = "bytes=0-" + (fileSize - 1);
        }
        range = rangeStr.split("bytes=|-");
        long begin = 0;
        if (range.length >= 2) {
            begin = Long.parseLong(range[1]);
        }
        long end = fileSize - 1;
        if (range.length >= 3) {
            end = Long.parseLong(range[2]);
        }
        long len = end - begin + 1;
        //设置响应头信息
        String contentRange = "bytes " + begin + "-" + end + "/" + fileSize;
        response.setHeader("Content-Range", contentRange);
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Type", "video/mp4");
        response.setContentLength((int) len);
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        HttpUtil.get(url, headers, response);
    }

    /**
     * 直接在线观看视频
     * @param request
     * @param response
     * @param path
     */
    public void viewVideoOnline(HttpServletRequest request,
                                        HttpServletResponse response,
                                        String path) throws Exception {
        FileInfo fileInfo = fastFileStorageClient.queryFileInfo(FileConstant.GROUP1, path);
        long fileSize = fileInfo.getFileSize();
        String url = httpFdfsStorageAddr + path;
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, Object> headers = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            headers.put(header, request.getHeader(header));
        }
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Type", "video/mp4");
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        HttpUtil.get(url, headers, response);
    }

    /**
     * 下载视频到本地
     * @param url
     * @param localPath
     */
    public void downloadFile(String url, String localPath) {
        fastFileStorageClient.downloadFile(FileConstant.GROUP1, url, new DownloadCallback<String>() {
            @Override
            public String recv(InputStream ins) throws IOException {
                File file = new File(localPath);
                FileOutputStream outputStream = new FileOutputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = ins.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, len);
                }
                outputStream.close();
                ins.close();
                return "success";
            }
        });
    }
}
