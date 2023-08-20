package com.zzd.giligili.controller;

import com.zzd.giligili.domain.JsonResponse;
import com.zzd.giligili.service.ElasticSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author dongdong
 * @Date 2023/7/27 16:58
 */
@RestController
public class SystemController {

    @Autowired
    private ElasticSearchService elasticSearchService;


    @GetMapping("/contents")
    public JsonResponse<Map<String, Object>> getContents(@RequestParam("text") String text,
                                                               @RequestParam Integer pageNum,
                                                               @RequestParam Integer pageSize) throws IOException {
        Map<String, Object> contents = elasticSearchService.getContents(text, pageNum, pageSize);
        return new JsonResponse<>(contents);
    }
}
