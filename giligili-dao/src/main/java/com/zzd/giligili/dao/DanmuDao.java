package com.zzd.giligili.dao;

import com.zzd.giligili.domain.Danmu;
import com.zzd.giligili.domain.vo.DanmuVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DanmuDao {

    Integer addDanmu(Danmu danmu);

    List<Danmu> getDanmus(Map<String,Object> params);

    List<DanmuVO> getDanmuVOs(Map<String,Object> params);

}
