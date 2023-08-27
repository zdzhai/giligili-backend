package com.zzd.giligili.service;

import com.zzd.giligili.dao.VideoDao;
import com.zzd.giligili.domain.*;
import com.zzd.giligili.domain.exception.ConditionException;
import com.zzd.giligili.domain.vo.UserInfoVO;
import com.zzd.giligili.domain.vo.UserVO;
import com.zzd.giligili.service.utils.FastDFSUtil;
import com.zzd.giligili.service.utils.IpUtil;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dongdong
 * @Date 2023/7/25 16:26
 */
@Service
public class VideoService {

    @Resource
    private VideoDao videoDao;

    @Autowired
    private VideoViewService videoViewService;

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Autowired
    private UserCoinService userCoinService;

    @Autowired
    private UserService userService;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private VideoOperationService videoOperationService;

    /**
     * 添加用户视频信息
     * @param video
     */
    @Transactional
    public void addVideos(Video video) {
        Date now = new Date();
        video.setCreateTime(now);
        videoDao.addVideo(video);
        Long videoId = video.getId();
        List<VideoTag> videoTagList = video.getVideoTagList();
        if (videoTagList == null) {
            return;
        }
        videoTagList.stream().forEach(item -> {
            item.setCreateTime(now);
            item.setVideoId(videoId);
        });
        videoDao.batchAddVideoTags(videoTagList);
    }

    /**
     * 根据分区分页查询视频
     * @param pageNum
     * @param pageSize
     * @param area
     * @return
     */
    public PageResult<Video> pageListVideos(Integer pageNum, Integer pageSize, String area) {
        if (pageNum == null || pageSize == null) {
            throw new ConditionException("参数异常!");
        }
        Map<String, Object> map = new HashMap<>();
        Integer start = (pageNum - 1) * pageSize;
        Integer limit = pageSize;
        map.put("start",start);
        map.put("limit",limit);
        map.put("area",area);
        List<Video> videoList = new ArrayList<>();
        Integer total = videoDao.countVideos(map);
        if (total > 0) {
            videoList = videoDao.pageListVideos(map);
        }
        return new PageResult<>(videoList, Long.valueOf(total));
    }

    /**
     * 通过分片在线观看视频（分片获取视频资源）
     * @param request
     * @param response
     * @param path
     */
    public void viewVideoOnlineBySlices(HttpServletRequest request,
                                        HttpServletResponse response,
                                        String path) throws Exception {
        fastDFSUtil.viewVideoOnlineBySlices(request, response , path);
//        fastDFSUtil.viewVideoOnline(request, response , path);
    }

    /**
     * 点赞
     * @param userId
     * @param videoId
     */
    @Transactional(rollbackFor = {ConditionException.class})
    public void addVideoLikes(Long userId, Long videoId) {
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频!");
        }
        VideoLike videoLike = new VideoLike();
        videoLike.setUserId(userId);
        videoLike.setVideoId(videoId);
        videoLike.setCreateTime(new Date());
        videoDao.addVideoLike(videoLike);
        //添加点赞操作
        VideoOperation videoOperation = new VideoOperation();
        videoOperation.setUserId(userId);
        videoOperation.setVideoId(videoId);
        videoOperation.setOperationType("0");
        videoOperation.setCreateTime(new Date());
        videoOperationService.addLikeOperation(videoOperation);
    }

    /**
     * 取消点赞
     * @param userId
     * @param videoId
     */
    @Transactional(rollbackFor = {ConditionException.class})
    public void deleteVideoLikes(Long userId, Long videoId) {
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频!");
        }
        videoDao.deleteVideoLike(videoId, userId);
        //删除点赞操作
        VideoOperation videoOperation = new VideoOperation();
        videoOperation.setUserId(userId);
        videoOperation.setVideoId(videoId);
        videoOperation.setOperationType("0");
        videoOperationService.deleteLikeOperation(videoOperation);
    }

    /**
     * 查询视频的总点赞数以及当前用户是否点赞
     * @param userId
     * @param videoId
     * @return
     */
    public Map<String, Object> getVideoLikes(Long userId, Long videoId) {
        Long countLikes = videoDao.getVideoLikes(videoId);
        VideoLike videoLike = videoDao.getVideoLikeByVideoIdAndUserId(videoId, userId);
        boolean like = videoLike != null;
        HashMap<String, Object> result = new HashMap<>(2);
        result.put("countLikes", countLikes);
        result.put("like", like);
        return  result;
    }

    /**
     * 收藏
     * @param videoCollection
     */
    @Transactional(rollbackFor = {ConditionException.class})
    public void addVideoCollections(VideoCollection videoCollection) {
        //参数校验
        Long videoId = videoCollection.getVideoId();
        Long groupId = videoCollection.getGroupId();
        Long userId = videoCollection.getUserId();
        if (videoId == null || groupId == null) {
            throw new ConditionException("参数异常!");
        }
        //查询视频收藏视频是否存在
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("收藏视频不存在!");
        }
        //删除原有视频收藏记录
        videoDao.deleteVideoCollection(videoId, userId);
        //添加新收藏记录
        videoCollection.setCreateTime(new Date());
        videoDao.addVideoCollection(videoCollection);
        //添加收藏操作
        VideoOperation videoOperation = new VideoOperation();
        videoOperation.setUserId(userId);
        videoOperation.setVideoId(videoId);
        videoOperation.setOperationType("1");
        videoOperation.setCreateTime(new Date());
        videoOperationService.addStarOperation(videoOperation);
    }

    /**
     * 取消收藏
     * @param videoId
     * @param userId
     */
    @Transactional(rollbackFor = {ConditionException.class})
    public void deleteVideoCollections(Long videoId, Long userId) {
        //参数校验
        if (videoId == null) {
            throw new ConditionException("参数异常!");
        }
        videoDao.deleteVideoCollection(videoId, userId);
        //删除收藏操作
        VideoOperation videoOperation = new VideoOperation();
        videoOperation.setUserId(userId);
        videoOperation.setVideoId(videoId);
        videoOperation.setOperationType("1");
        videoOperationService.deleteStarOperation(videoOperation);
    }

    /**
     * 查询视频的总收藏数以及当前用户是否收藏
     * @param userId
     * @param videoId
     * @return
     */
    public Map<String, Object> getVideoCollections(Long userId, Long videoId) {
        Long countCollections = videoDao.getVideoCollections(videoId);
        VideoCollection videoCollection = videoDao.getVideoCollectionByVideoIdAndUserId(videoId, userId);
        boolean star = videoCollection != null;
        HashMap<String, Object> result = new HashMap<>(2);
        result.put("countCollections", countCollections);
        result.put("star", star);
        return  result;
    }

    /**
     * 投币
     * @param videoCoin
     */
    @Transactional
    public void addVideoCoins(VideoCoin videoCoin) {
        //参数校验
        Long videoId = videoCoin.getVideoId();
        Integer amount = videoCoin.getAmount();
        Long userId = videoCoin.getUserId();
        if (videoId == null) {
            throw new ConditionException("参数异常!");
        }
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频!");
        }
        //用户拥有的币数量 > 用户要投的币数量比较
        Integer userCoinsAmount = userCoinService.getUserCoinsAmount(userId);
        userCoinsAmount = userCoinsAmount == null ? 0 : userCoinsAmount;
        if (amount > userCoinsAmount) {
            throw new ConditionException("用户硬币不足!");
        }
        VideoCoin dbVideoCoin = videoDao.getVideoCoinByVideoIdAndUserId(videoId, userId);
        //根据userId,videoId,更新用户投币数
        if (dbVideoCoin == null) {
            videoCoin.setCreateTime(new Date());
            videoDao.addVideoCoin(videoCoin);
        } else {
            amount += dbVideoCoin.getAmount();
            videoCoin.setAmount(amount);
            videoCoin.setUpdateTime(new Date());
            videoDao.updateVideoCoin(videoCoin);
        }
        //更新用户当前硬币数
        userCoinService.updateUserCoinsAmount(userId, userCoinsAmount - amount);
    }

    /**
     * 获取视频总投币数
     * @param userId
     * @param videoId
     * @return
     */
    public Map<String, Object> getVideoCoins(Long userId, Long videoId) {
        //获取视频总硬币数
        Long countCoins = videoDao.getVideoCoinsAmount(videoId);
        //查看当前用户是否已投币
        VideoCoin videoCoin = videoDao.getVideoCoinByVideoIdAndUserId(videoId, userId);
        boolean coin = videoCoin != null;
        HashMap<String, Object> result = new HashMap<>(2);
        result.put("countCoins", countCoins);
        result.put("coin", coin);
        return  result;
    }

    /**
     * 获取视频评论功能（待完善）
     */

    /**
     * 获取视频详细信息
     * @param videoId
     * @return
     */
    public Map<String, Object> getVideoDetails(Long videoId) {
        if (videoId == null) {
            throw new ConditionException("参数异常!");
        }
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频!");
        }
        Long userId = video.getUserId();
        UserVO userVO = userService.getUserById(userId);
        UserInfoVO userInfoVO = userVO.getUserInfo();
        HashMap<String, Object> result = new HashMap<>(2);
        result.put("video", video);
        result.put("userInfo", userInfoVO);
        return  result;
    }

    /**
     * 区分游客和用户添加观看记录
     * @param videoView
     * @param request
     */
    public void addVideoView(VideoView videoView, HttpServletRequest request) {
        //首先获取userId,videoId,clientId,IP
        Long userId = videoView.getUserId();
        Long videoId = videoView.getVideoId();
        //生成clientId
        String agent = request.getHeader("User-Agent");
        UserAgent userAgent = UserAgent.parseUserAgentString(agent);
        String clientId = String.valueOf(userAgent.getId());
        String ip = IpUtil.getIP(request);
        Map<String, Object> params = new HashMap<>();
        if (userId != null) {
            //用户模式
            params.put("userId", userId);
        } else {
            //游客模式
            params.put("clientId", clientId);
            params.put("ip", ip);
        }
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
        params.put("today", sdf.format(date));
        params.put("videoId", videoId);
        //添加观看记录 先查询（区分游客和用户）
        VideoView dbVideoVIew = videoViewService.getVideoView(params);
        if (dbVideoVIew == null) {
            videoView.setIp(ip);
            videoView.setClientId(clientId);
            videoView.setCreateTime(date);
            videoViewService.addVideoView(videoView);
        }
    }

    /**
     * 根据视频id获取视频观看总量
     * @param videoId
     * @return
     */
    public Long getVideoViewCount(Long videoId){
        return videoViewService.getVideoViewCount(videoId);
    }

    /**
     * 基于用户的协同推荐
     * @param userId 用户id
     */
    public List<Video> recommend(Long userId) throws TasteException {
        List<UserPreference> list = videoDao.getAllUserPreference();
        //创建数据模型
        DataModel dataModel = this.createDataModel(list);
        //获取用户相似程度
        UserSimilarity similarity = new UncenteredCosineSimilarity(dataModel);
/*        System.out.println(similarity.userSimilarity(11, 12));
        System.out.println(similarity.userSimilarity(11, 13));
        System.out.println(similarity.userSimilarity(11, 14));*/
        //获取用户邻居
        UserNeighborhood userNeighborhood = new NearestNUserNeighborhood(2, similarity, dataModel);
        long[] ar = userNeighborhood.getUserNeighborhood(userId);
        //构建推荐器
        Recommender recommender = new GenericUserBasedRecommender(dataModel, userNeighborhood, similarity);
        //推荐视频
        List<RecommendedItem> recommendedItems = recommender.recommend(userId, 6);
        List<Long> itemIds = recommendedItems.stream().map(RecommendedItem::getItemID).collect(Collectors.toList());
        List<Video> videoList = videoDao.batchGetVideosByIds(itemIds);
        if (videoList.size() > 6) {
            videoList = videoList.subList(0, 6);
        }
        for (Video video : videoList) {
            Long videoUserId = video.getUserId();
            UserInfoVO userInfoById = userInfoService.getUserInfoById(videoUserId);
            video.setUserInfo(userInfoById);
        }
        return videoList;
    }

    private DataModel createDataModel(List<UserPreference> userPreferenceList) {
        FastByIDMap<PreferenceArray> fastByIdMap = new FastByIDMap<>();
        Map<Long, List<UserPreference>> map = userPreferenceList.stream().collect(Collectors.groupingBy(UserPreference::getUserId));
        Collection<List<UserPreference>> list = map.values();
        for(List<UserPreference> userPreferences : list){
            GenericPreference[] array = new GenericPreference[userPreferences.size()];
            for(int i = 0; i < userPreferences.size(); i++){
                UserPreference userPreference = userPreferences.get(i);
                GenericPreference item = new GenericPreference(userPreference.getUserId(), userPreference.getVideoId(), userPreference.getValue());
                array[i] = item;
            }
            fastByIdMap.put(array[0].getUserID(), new GenericUserPreferenceArray(Arrays.asList(array)));
        }
        return new GenericDataModel(fastByIdMap);
    }

    /**
     * 根据videoID获取视频详情
     * @param videoId
     * @return
     */
    public Video getVideoById(Long videoId) {
        return videoDao.getVideoById(videoId);
    }

    public List<Video> listAll() {
            return videoDao.listAll();
    }

    /**
     * 根据userId获取用户所属视频信息
     * @param userId
     * @return
     */
    public List<Video> getVideosByUserId(Long userId) {
        List<Video> videoList = videoDao.getVideosByUserId(userId);
        for (Video video : videoList) {
            Long videoUserId = video.getUserId();
            UserInfoVO userInfoById = userInfoService.getUserInfoById(videoUserId);
            video.setUserInfo(userInfoById);
        }
        return videoList;
    }

    /**
     * 根据userId获取用户收藏视频信息
     * @param userId
     * @return
     */
    public List<Video> getStarVideosByUserId(Long userId) {
        List<Video> starVideoList = videoDao.getStarVideosByUserId(userId);
        for (Video video : starVideoList) {
            Long videoUserId = video.getUserId();
            UserInfoVO userInfoById = userInfoService.getUserInfoById(videoUserId);
            video.setUserInfo(userInfoById);
        }
        return starVideoList;
    }
    /**
     * 查询视频列表（包括已被删除的数据）
     */
    public List<Video> listVideoWithDelete(Date fiveMinutesAgoDate) {
        return videoDao.listVideoWithDelete(fiveMinutesAgoDate);
    }
}
