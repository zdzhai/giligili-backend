package com.zzd.giligili.dao.es;

import com.zzd.giligili.domain.UserInfo;
import com.zzd.giligili.domain.Video;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author dongdong
 * @Date 2023/7/27 10:52
 */
public interface UserInfoRepository extends ElasticsearchRepository<UserInfo, Long> {


    UserInfo findByNickLike(String nick);
}
