package com.zzd.giligili.service;

import com.zzd.giligili.dao.es.UserInfoRepository;
import com.zzd.giligili.dao.es.VideoRepository;
import com.zzd.giligili.domain.UserInfo;
import com.zzd.giligili.domain.Video;
import org.elasticsearch.action.search.SearchRequest;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author dongdong
 * @Date 2023/7/27 10:42
 */
@Service
public class ElasticSearchService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Resource
    private RestHighLevelClient restHighLevelClient;

    /**
     * 往es中添加用户详情信息
     * @param userInfo
     */
    public void addUserInfo(UserInfo userInfo) {
        userInfoRepository.save(userInfo);
    }

    /**
     * 往es中添加视频信息
     * @param video
     */
    public void addVideo(Video video) {
        videoRepository.save(video);
    }

    /**
     * 往es中删除视频信息
     * @param url
     */
    public void deleteVideo(String url) {
        videoRepository.deleteByUrl(url);
    }

    /**
     * 根据keyword搜索出video和user-info中的相关信息
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @return
     * @throws IOException
     */
    public Map<String, Object> getContents(String keyword,
                            Integer pageNum,
                            Integer pageSize) throws IOException {
        Map<String, Object> resMap = new HashMap<>();
        //es索引信息
        String indices = "videos";
        String[] highlightArray = new String[2];
        highlightArray[0] =  "title";
        highlightArray[1] =  "description";
        List<Map<String, Object>> videoList =
                getContentsByType(keyword, pageNum, pageSize, indices, highlightArray);
        if (videoList != null) {
            resMap.put("videoList", videoList);
        }
        indices = "user-infos";
        highlightArray[0] =  "nick";
        highlightArray[1] =  " ";
        List<Map<String, Object>> userList =
                getContentsByType(keyword, pageNum, pageSize, indices, highlightArray);
        if (userList != null) {
            resMap.put("userList", userList);
        }
        return resMap;
    }

    public List<Map<String, Object>> getContentsByType(String keyword,
                                                       Integer pageNum,
                                                       Integer pageSize,
                                                       String indices,
                                                       String[] highlightArray) throws IOException {
        //es索引信息
        SearchRequest searchRequest = new SearchRequest(indices);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //分页
        sourceBuilder.from(pageNum - 1);
        sourceBuilder.size(pageSize);
        //查询的关键词和字段
        int length = highlightArray.length;
        MultiMatchQueryBuilder matchQueryBuilder =
                new MultiMatchQueryBuilder(keyword, highlightArray[0], highlightArray[1]);
        sourceBuilder.query(matchQueryBuilder);
        searchRequest.source(sourceBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        //高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        for (String key : highlightArray) {
            highlightBuilder.fields().add(new HighlightBuilder.Field(key));
        }
        //如果要多个字段进行高亮，需要设置为false
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.preTags("<span style=\"color:red\">");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);
        //执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        List<Map<String, Object>> mapList = new ArrayList<>();
        SearchHits searchHits = searchResponse.getHits();
        //先获取每一条命中的记录
        for (SearchHit hit : searchHits) {
            //获取命中记录的结果映射
            Map<String, Object> sourceMap = hit.getSourceAsMap();
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            for (String key : highlightArray) {
                HighlightField highlightField = highlightFields.get(key);
                if (highlightField != null) {
                    Text[] fragments = highlightField.getFragments();
                    String str = Arrays.toString(fragments);
                    str = str.substring(1, str.length() - 1);
                    sourceMap.put(key, str);
                }
            }
            mapList.add(sourceMap);
        }
        return mapList;
    }

    public Video getVideo(String keyword) {
        return videoRepository.findByTitleLike(keyword);
    }
}
