package com.zzd.giligili.service.job.cycle;

import com.zzd.giligili.dao.es.VideoRepository;
import com.zzd.giligili.domain.Video;
import com.zzd.giligili.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 增量同步视频到 es
 *
 * @author dongdong

 */
// todo 取消注释开启任务
@Component
@Slf4j
public class IncSyncVideoToEs {

    @Resource
    private VideoService videoService;

    @Resource
    private VideoRepository videoRepository;

    /**
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void run() {
        // 查询近 5 分钟内的数据
        Date fiveMinutesAgoDate = new Date(new Date().getTime() - 3 * 60 * 1000L);
        List<Video> videoList = videoService.listVideoWithDelete(fiveMinutesAgoDate);
        if (CollectionUtils.isEmpty(videoList)) {
            log.info("no inc video");
            return;
        }
        final int pageSize = 500;
        int total = videoList.size();
        log.info("IncSyncVideosToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("Inc from {} to {}", i, end);
            videoRepository.saveAll(videoList.subList(i, end));
        }
        log.info("IncSyncVideosToEs end, total {}", total);
    }
}
