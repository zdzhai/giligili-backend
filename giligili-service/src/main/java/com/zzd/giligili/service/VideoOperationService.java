package com.zzd.giligili.service;

import com.zzd.giligili.dao.DemoDao;
import com.zzd.giligili.dao.VideoOperationDao;
import com.zzd.giligili.domain.Video;
import com.zzd.giligili.domain.VideoOperation;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author dongdong
 * @Date 2023/7/17 22:47
 */
@Service
public class VideoOperationService {

    @Resource
    private VideoOperationDao videoOperationDao;


    /**
     * 添加点赞操作
     * @param videoOperation
     */
    public void addLikeOperation(VideoOperation videoOperation) {
        videoOperationDao.addLikeOperation(videoOperation);
    }

    /**
     * 删除点赞操作
     * @param videoOperation
     */
    public void deleteLikeOperation(VideoOperation videoOperation) {
        videoOperationDao.deleteLikeOperation(videoOperation);
    }

    /**
     * 添加收藏操作
     * @param videoOperation
     */
    public void addStarOperation(VideoOperation videoOperation) {
        videoOperationDao.addStarOperation(videoOperation);
    }

    /**
     * 删除收藏操作
     * @param videoOperation
     */
    public void deleteStarOperation(VideoOperation videoOperation) {
        videoOperationDao.deleteStarOperation(videoOperation);
    }
}
