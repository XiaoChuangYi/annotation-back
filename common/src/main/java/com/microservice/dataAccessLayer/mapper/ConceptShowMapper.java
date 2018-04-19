package com.microservice.dataAccessLayer.mapper;

import com.microservice.dataAccessLayer.entity.ConceptShow;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

/**
 * Created by cjl on 2018/4/11.
 */
public interface ConceptShowMapper {

    @Select("select * from concept_show where pconcept_id=#{conceptId}")
    @Results({
            @Result(id=true,column = "concept_id",property = "conceptId",jdbcType = JdbcType.VARCHAR),
            @Result(column = "pconcept_id",property = "pconceptId"),
            @Result(column = "concept_code",property = "conceptCode"),
            @Result(column = "concept_type",property = "conceptType"),
            @Result(column = "concept_name",property = "conceptName"),
            @Result(column = "has_children",property = "hasChildren"),
            @Result(column = "atomic_id",property = "atomicId"),
            @Result(column = "state",property = "state")
    })
    List<ConceptShow> listConceptShowByConceptId(@Param("conceptId") String conceptId);
}
