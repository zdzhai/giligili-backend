package com.zzd.giligili.service.job.once;

import com.zzd.giligili.dao.es.UserInfoRepository;
import com.zzd.giligili.dao.es.VideoRepository;
import com.zzd.giligili.domain.UserInfo;
import com.zzd.giligili.domain.Video;
import com.zzd.giligili.service.UserInfoService;
import com.zzd.giligili.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 全量同步用户信息到 es
 *

 */
// todo 取消注释开启任务
//@Component
@Slf4j
public class FullSyncVideoToEs implements CommandLineRunner {

    @Resource
    private VideoService videoService;

    @Resource
    private VideoRepository videoRepository;

    @Override
    public void run(String... args) {
        List<Video> videoList = videoService.listAll();
        if (CollectionUtils.isEmpty(videoList)) {
            return;
        }
        final int pageSize = 500;
        int total = videoList.size();
        log.info("FullSyncUserInfoToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            videoRepository.saveAll(videoList.subList(i, end));
        }
        log.info("FullSyncUserInfoToEs end, total {}", total);
    }
}
