package cn.malgo.annotation.common.dal.mapper;

import cn.malgo.annotation.common.dal.model.AnType;
import cn.malgo.annotation.common.dal.util.CommonMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AnTypeMapper extends CommonMapper<AnType> {
    List<AnType> selectEnableTypes();
    List<AnType> selectTypeByTypeId(@Param("Id") String Id);
    List<AnType> selectEnableTypeAndShowParent(@Param("typeCode") String typeCode,@Param("typeName") String typeName);
    AnType selectTypeByTypeCodeEnable(String typeCode);
    AnType selectTypeByTypeCodeDisable(String typeCode);
    AnType selectTypeByTypeCode(String typeCode);
    AnType selectTypeByParentId(@Param("parentId") String parentId);

    int selectTypeCount();
    int updateByTypeCodeSelective(AnType TypeEntity);
    int updateTypeCodeById(@Param("typeCode") String typeCode,@Param("id") String id);
    AnType selectTypeLabelByTypeCode(@Param("typeCode") String typeCode);
}