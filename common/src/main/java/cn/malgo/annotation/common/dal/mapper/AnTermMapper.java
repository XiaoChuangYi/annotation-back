package cn.malgo.annotation.common.dal.mapper;

import cn.malgo.annotation.common.dal.model.AnTerm;
import cn.malgo.annotation.common.dal.util.CommonMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AnTermMapper extends CommonMapper<AnTerm> {

    /**
     * 通过状态查询术语
     * @param state
     * @return
     */
    List<AnTerm> selectByState(@Param("state") String state);
}