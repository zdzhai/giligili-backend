package com.zzd.giligili.service;

import com.alibaba.fastjson.JSONObject;
import com.zzd.giligili.dao.UserInfoDao;
import com.zzd.giligili.domain.PageResult;
import com.zzd.giligili.domain.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
* @author dongdong
* @description 针对表【t_user_info(用户基本信息表)】的数据库操作Service
* @createDate 2023-07-18 15:48:22
*/
@Service
public class UserInfoService  {

    @Resource
    private UserInfoDao userInfoDao;

    /**
     * 根据用户id集合获取用户信息
     * @param userIdSet
     * @return
     */
    public List<UserInfo> getUserInfoByUserIds(Set<Long> userIdSet){
        return userInfoDao.getUserInfoByUserIds(userIdSet);
    }

    /**
     * 页获取用户信息
     * @param params
     * @return
     */
    public PageResult<UserInfo> pageListUserInfos(JSONObject params) {
        Integer pageNum = params.getInteger("pageNum");
        Integer pageSize = params.getInteger("pageSize");
        params.put("start", (pageNum - 1) * pageSize);
        params.put("limit", pageSize);
        Long total = userInfoDao.pageCountUserInfos(params);
        List<UserInfo> list = new ArrayList<>();
        if (total > 0){
            list = userInfoDao.pageListUserInfos(params);
        }
        return new PageResult<>(list, total);
    }
}
