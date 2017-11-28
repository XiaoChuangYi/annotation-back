package cn.malgo.annotation.common.dal.mapper;

import cn.malgo.annotation.common.dal.model.ConceptShow;
import cn.malgo.annotation.common.dal.util.CommonMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ConceptShowMapper extends CommonMapper<ConceptShow> {
    List<ConceptShow> selectConceptByConceptId(@Param("conceptId") String conceptId);

}