package com.microservice.dataAccessLayer.mapper;

import com.microservice.dataAccessLayer.dynamicSql.ConceptDynamicSqlProvider;
import com.microservice.dataAccessLayer.dynamicSql.TypeDynamicSqlProvider;
import com.microservice.dataAccessLayer.entity.Concept;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by cjl on 2018/4/11.
 */
public interface ConceptMapper {

    @Select("select * from concept where id=#{id}")
    @Results({
            @Result(id=true,column = "id",property = "id"),
            @Result(column = "standard_name",property = "standardName"),
            @Result(column = "concept_id",property = "conceptId")
    })
    Concept getConceptById(@Param("id") String id);

    @SelectProvider(type = ConceptDynamicSqlProvider.class,method = "queryConceptByCondition")
    @Results({
            @Result(id=true,column = "id",property = "id"),
            @Result(column = "standard_name",property = "standardName"),
            @Result(column = "concept_id",property = "conceptId")
    })
    List<Concept> listConceptByCondition(@Param("concept") Concept concept);


    @UpdateProvider(type = ConceptDynamicSqlProvider.class,method = "updateConceptSelective")
    void updateConceptSelective(Concept concept);

    @InsertProvider(type = ConceptDynamicSqlProvider.class,method = "insertConceptSelective")
    @Options(useGeneratedKeys = true,keyColumn = "id")
    void insertConceptSelective(Concept concept);


}
