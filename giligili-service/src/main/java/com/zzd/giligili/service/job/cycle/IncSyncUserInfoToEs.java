package com.zzd.giligili.service.job.cycle;

import com.zzd.giligili.dao.es.UserInfoRepository;
import com.zzd.giligili.dao.es.VideoRepository;
import com.zzd.giligili.domain.UserInfo;
import com.zzd.giligili.domain.Video;
import com.zzd.giligili.service.UserInfoService;
import com.zzd.giligili.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 增量同步用户信息到 es
 *
 * @author dongdong

 */
// todo 取消注释开启任务
@Component
@Slf4j
public class IncSyncUserInfoToEs {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private UserInfoRepository userInfoRepository;

    /**
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void run() {
        // 查询近 5 分钟内的数据
        Date fiveMinutesAgoDate = new Date(new Date().getTime() - 3 * 60 * 1000L);
        List<UserInfo> userInfoList = userInfoService.listUserInfoWithDelete(fiveMinutesAgoDate);
        if (CollectionUtils.isEmpty(userInfoList)) {
            log.info("no inc userInfo");
            return;
        }
        final int pageSize = 500;
        int total = userInfoList.size();
        log.info("IncSyncUserInfoToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("Inc from {} to {}", i, end);
            userInfoRepository.saveAll(userInfoList.subList(i, end));
        }
        log.info("IncSyncUserInfoToEs end, total {}", total);
    }
}
