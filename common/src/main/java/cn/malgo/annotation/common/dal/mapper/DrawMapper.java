package cn.malgo.annotation.common.dal.mapper;

import cn.malgo.annotation.common.dal.model.Draw;
import cn.malgo.annotation.common.dal.util.CommonMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DrawMapper extends CommonMapper<Draw> {
    List<Draw> selectDrawLeftJoinType();
    Draw selectDrawNameByTypeCode(@Param("typeCode") String typeCode);
    List<Draw> selectDrawByCondition(@Param("typeCode") String typeCode,@Param("color") String color);
}