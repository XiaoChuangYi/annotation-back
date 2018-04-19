package com.microservice.dataAccessLayer.mapper;

import com.microservice.dataAccessLayer.dynamicSql.AnnotationNegationDynamicSqlProvider;
import com.microservice.dataAccessLayer.entity.AnnotationNegation;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by cjl on 2018/4/19.
 */
public interface AnnotationNegationMapper {

    @SelectProvider(type = AnnotationNegationDynamicSqlProvider.class,method = "queryAnnotationNegationSelective")
    @Results({
            @Result(id=true,column ="id" ,property = "id"),
            @Result(column ="origin_text" ,property = "originText"),
            @Result(column = "annotation_text",property = "annotationText"),
            @Result(column = "state",property = "state"),
            @Result(column = "user_modifier",property = "userModifier"),
            @Result(column = "gmt_created",property = "gmtCreated"),
            @Result(column = "gmt_modified",property = "gmtModified"),
            @Result(column = "memo",property = "memo")
    })
    List<AnnotationNegation> listAnnotationNegationByCondition(@Param("annotationNegation") AnnotationNegation annotationNegation,
                                                               @Param("stateList") List<String> stateList);



    @Select("select * from annotation_negation where id=#{id}")
    @Results({
            @Result(id=true,column ="id" ,property = "id"),
            @Result(column ="origin_text" ,property = "originText"),
            @Result(column = "annotation_text",property = "annotationText"),
            @Result(column = "state",property = "state"),
            @Result(column = "user_modifier",property = "userModifier"),
            @Result(column = "gmt_created",property = "gmtCreated"),
            @Result(column = "gmt_modified",property = "gmtModified"),
            @Result(column = "memo",property = "memo")
    })
    AnnotationNegation getAnnotationNegationById(@Param("id") int id);

    @UpdateProvider(type=AnnotationNegationDynamicSqlProvider.class,method = "updateAnnotationNegationSelective")
    int updateAnnotationNegationSelective(AnnotationNegation annotationNegation);

    @UpdateProvider(type=AnnotationNegationDynamicSqlProvider.class,method = "updateAnnotationNegationUserModifierByIdArr")
    void updateAnnotationNegationUserModifierByIdArr(@Param("idArr") List<Integer> idArr,@Param("userModifier") int userModifier,@Param("state") String state);

    @UpdateProvider(type = AnnotationNegationDynamicSqlProvider.class,method = "batchUpdateAnnotationNegationStateByIdArr")
    void batchUpdateAnnotationNegationStateByIdArr(@Param("idArr") List<Integer> idArr,@Param("state") String state);
}
