package cn.malgo.annotation.common.dal.mapper;

import cn.malgo.annotation.common.dal.model.Concept;
import cn.malgo.annotation.common.dal.util.CommonMapper;
import org.apache.ibatis.annotations.Param;

public interface ConceptMapper extends CommonMapper<Concept> {
    Concept selectConceptByConceptId(@Param("conceptId") String conceptId);
}