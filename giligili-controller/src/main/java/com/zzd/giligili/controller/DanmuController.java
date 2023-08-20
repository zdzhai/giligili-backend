package com.zzd.giligili.controller;

import com.zzd.giligili.controller.support.UserSupport;
import com.zzd.giligili.domain.Danmu;
import com.zzd.giligili.domain.JsonResponse;
import com.zzd.giligili.domain.vo.DanmuVO;
import com.zzd.giligili.service.DanmuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author dongdong
 * @Date 2023/7/27 10:11
 */
@RestController
public class DanmuController {

    @Autowired
    private DanmuService danmuService;

    @Autowired
    private UserSupport userSupport;

    @GetMapping("/danmus")
    public JsonResponse<List<DanmuVO>> getDanmus(@RequestParam Long videoId,
                                                 String startTime,
                                                 String endTime) throws Exception {
        List<DanmuVO> list;
        try {
            //判断是否是游客模式
            Long userId = userSupport.getUserId();
            list = danmuService.getDanmus(videoId, startTime, endTime);
        } catch (Exception e) {
            //游客模式不允许加时间
            list = danmuService.getDanmus(videoId, null, null);
        }
/*        if (list.size() >= 9) {
            list = list.subList(0, 9);
        }*/
        return new JsonResponse<>(list);
    }

    @GetMapping("/v3")
    public JsonResponse<List<Object[]>> getDanmusForBackend(@RequestParam Long videoId,
                                               String startTime,
                                               String endTime) throws Exception {
        List<Object[]> danmus;
        try {
            //判断是否是游客模式
            Long userId = userSupport.getUserId();
            danmus = danmuService.getDanmusForBackend(videoId, startTime, endTime);
        } catch (Exception e) {
            //游客模式不允许加时间
            danmus = danmuService.getDanmusForBackend(videoId, null, null);
        }
        return new JsonResponse<>(danmus);
    }
}
