package com.zzd.giligili.domain.constant;

import cn.hutool.core.util.RandomUtil;

/**
 * @author dongdong
 * @Date 2023/7/19 16:17
 */
public interface UserConstant {

    public static final String GENDER_MALE = "0";

    public static final String GENDER_FEMAL = "1";

    public static final String GENDER_UNKNOW = "0";

    public static final String DEFAULT_BIRTH = "1999-10-01";

    public static final String DEFAULT_NICK_NAME = "小萌新" + RandomUtil.randomString(4);

    public static final Integer DEFAULT_GROUP_TYPE = 2;

    public static final String DEFAULT_ALL_USER_GROUP = "全部分组";

    public static final String DEFAULT_USER_GROUP_NEW = "3";
}
