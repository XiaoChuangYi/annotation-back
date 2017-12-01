package cn.malgo.annotation.common.dal.mapper;

import cn.malgo.annotation.common.dal.model.Concept;
import cn.malgo.annotation.common.dal.util.CommonMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ConceptMapper extends CommonMapper<Concept> {
    Concept selectConceptByConceptId(@Param("conceptId") String conceptId);
    List<Concept> selectConceptByStandardName(@Param("standardName") String standardName);
}