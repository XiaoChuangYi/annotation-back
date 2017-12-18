package cn.malgo.annotation.common.dal.mapper;

import cn.malgo.annotation.common.dal.model.AnType;
import cn.malgo.annotation.common.dal.util.CommonMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AnTypeMapper extends CommonMapper<AnType> {
    AnType selectTypeByTypeCodeEnable(String typeCode);
    AnType selectTypeByTypeCodeDisable(String typeCode);
    AnType selectTypeByTypeCode(String typeCode);
    int selectTypeCount();
    List<AnType> selectEnableTypes();
    int updateByTypeCodeSelective(AnType TypeEntity);
    int updateTypeCodeById(@Param("typeCode") String typeCode,@Param("id") String id);
    List<AnType> selectTypeByTypeId(@Param("Id") String Id);
    AnType selectTypeLabelByTypeCode(@Param("typeCode") String typeCode);
}