package com.zzd.giligili.dao;

import com.zzd.giligili.domain.Video;
import com.zzd.giligili.domain.VideoTag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author dongdong
 * @Date 2023/7/25 16:26
 */
@Mapper
public interface VideoDao {

    /**
     * 添加用户视频信息
     * @param video
     */
    void addVideo(Video video);

    /**
     * 批量添加视频相关联的tag
     * @param videoTagList
     */
    void batchAddVideoTags(List<VideoTag> videoTagList);

    /**
     * 查分区视频总数
     * @param map
     * @return
     */
    Integer countVideos(Map<String, Object> map);

    /**
     * 分页查询分类视频
     * @param map
     * @return
     */
    List<Video> pageListVideos(Map<String, Object> map);
}
