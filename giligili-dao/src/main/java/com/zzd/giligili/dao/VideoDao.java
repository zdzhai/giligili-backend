package com.zzd.giligili.dao;

import com.zzd.giligili.domain.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
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

    /**
     * 根据视频id查询视频
     * @param id
     * @return
     */
    Video getVideoById(Long id);

    /**
     * 根据videoId和userId查询视频点赞记录
     * @param videoId
     * @param userId
     * @return
     */
    VideoLike getVideoLikeByVideoIdAndUserId(@Param("videoId") Long videoId, @Param("userId") Long userId);

    /**
     * 添加视频点赞记录
     * @param videoLike
     * @return
     */
    Integer addVideoLike(VideoLike videoLike);

    /**
     * 删除视频点赞
     * @param videoId
     * @param userId
     * @return
     */
    Integer deleteVideoLike(@Param("videoId") Long videoId,
                            @Param("userId") Long userId);

    /**
     * 获取视频总点赞数
     * @param videoId
     * @return
     */
    Long getVideoLikes(Long videoId);

    /**
     * 添加视频收藏记录
     * @param videoCollection
     * @return
     */
    Integer addVideoCollection(VideoCollection videoCollection);

    /**
     * 删除视频收藏记录
     * @param videoId
     * @param userId
     * @return
     */
    Integer deleteVideoCollection(@Param("videoId") Long videoId,
                                  @Param("userId") Long userId);

    /**
     * 获取视频收藏总数
     * @param videoId
     * @return
     */
    Long getVideoCollections(Long videoId);

    VideoCollection getVideoCollectionByVideoIdAndUserId(@Param("videoId") Long videoId,
                                                         @Param("userId") Long userId);

    VideoCoin getVideoCoinByVideoIdAndUserId(@Param("videoId") Long videoId,
                                             @Param("userId") Long userId);

    /**
     * 添加投币记录
     * @param videoCoin
     * @return
     */
    Integer addVideoCoin(VideoCoin videoCoin);

    /**
     * 更新投币记录
     * @param videoCoin
     * @return
     */
    Integer updateVideoCoin(VideoCoin videoCoin);

    /**
     * 获取当前视频已投币总数
     * @param videoId
     * @return
     */
    Long getVideoCoinsAmount(Long videoId);

    Integer addVideoComment(VideoComment videoComment);

    Integer pageCountVideoComments(Map<String, Object> params);

    List<VideoComment> pageListVideoComments(Map<String, Object> params);

    List<VideoComment> batchGetVideoCommentsByRootIds(@Param("rootIdList") List<Long> rootIdList);

    Video getVideoDetails(Long videoId);

    Integer addVideoView(VideoView videoView);

    Integer getVideoViewCounts(Long videoId);

    VideoView getVideoView(Map<String, Object> params);

    List<Video> batchGetVideosByIds(@Param("idList") List<Long> idList);


    List<VideoTag> getVideoTagsByVideoId(Long videoId);

    Integer deleteVideoTags(@Param("tagIdList") List<Long> tagIdList,
                            @Param("videoId") Long videoId);

    /**
     * 获取用户的偏好视频
     * @return
     */
    List<UserPreference> getAllUserPreference();

    List<Video> listAll();

    void deleteVideoByUrl(String url);

    /**
     * 根据userId获取用户所属视频信息
     * @param userId
     * @return
     */
    List<Video> getVideosByUserId(Long userId);

    /**
     * 根据userId获取用户收藏视频信息
     * @param userId
     * @return
     */
    List<Video> getStarVideosByUserId(Long userId);

    /**
     * 查询视频列表（包括已被删除的数据）
     */
    List<Video> listVideoWithDelete(Date fiveMinutesAgoDate);

}
