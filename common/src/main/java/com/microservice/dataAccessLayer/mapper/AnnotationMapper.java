package com.microservice.dataAccessLayer.mapper;

import com.microservice.dataAccessLayer.dynamicSql.AnnotationDynamicSqlProvider;
import com.microservice.dataAccessLayer.entity.Annotation;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by cjl on 2018/3/30.
 */
public interface AnnotationMapper {

    @SelectProvider(type = AnnotationDynamicSqlProvider.class,method = "queryAnnotationByCondition")
    @Results({
            @Result(id=true,column = "id",property ="id" ),
            @Result(column = "term_id",property ="termId" ),
            @Result(column = "term",property ="term" ),
            @Result(column = "auto_annotation",property ="autoAnnotation" ),
            @Result(column = "manual_annotation",property ="manualAnnotation" ),
            @Result(column = "final_annotation",property ="finalAnnotation" ),
            @Result(column = "new_terms",property ="newTerms" ),
            @Result(column = "state",property ="state" ),
            @Result(column = "modifier",property ="modifier" ),
            @Result(column = "gmt_created",property ="gmtCreated" ),
            @Result(column = "gmt_modified",property ="gmtModified" ),
            @Result(column = "memo",property ="memo" )
    })
    List<Annotation> listAnnotationByCondition(@Param("annotation") Annotation annotation,@Param("sort") String sort);

    @SelectProvider(type = AnnotationDynamicSqlProvider.class,method = "queryAnnotationByStateList")
    @Results({
            @Result(id=true,column = "id",property ="id" ),
            @Result(column = "term_id",property ="termId" ),
            @Result(column = "term",property ="term" ),
            @Result(column = "auto_annotation",property ="autoAnnotation" ),
            @Result(column = "manual_annotation",property ="manualAnnotation" ),
            @Result(column = "final_annotation",property ="finalAnnotation" ),
            @Result(column = "new_terms",property ="newTerms" ),
            @Result(column = "state",property ="state" ),
            @Result(column = "modifier",property ="modifier" ),
            @Result(column = "gmt_created",property ="gmtCreated" ),
            @Result(column = "gmt_modified",property ="gmtModified" ),
            @Result(column = "memo",property ="memo" )
    })
    List<Annotation> listAnnotationByStateList(@Param("list") List<String> stateList,@Param("sort") String sort);

    @Select("select * from annotation where id=#{id}")
    @Results({
            @Result(id=true,column = "id",property ="id" ),
            @Result(column = "term_id",property ="termId" ),
            @Result(column = "term",property ="term" ),
            @Result(column = "auto_annotation",property ="autoAnnotation" ),
            @Result(column = "manual_annotation",property ="manualAnnotation" ),
            @Result(column = "final_annotation",property ="finalAnnotation" ),
            @Result(column = "new_terms",property ="newTerms" ),
            @Result(column = "state",property ="state" ),
            @Result(column = "modifier",property ="modifier" ),
            @Result(column = "gmt_created",property ="gmtCreated" ),
            @Result(column = "gmt_modified",property ="gmtModified" ),
            @Result(column = "memo",property ="memo" )
    })
    Annotation getAnnotationById(@Param("id") String id);

    @SelectProvider(type = AnnotationDynamicSqlProvider.class,method = "queryAnnotationByIdArr")
    List<Annotation> listAnnotationByIdArr(@Param("idArr") List<String> idArr);


    @SelectProvider(type=AnnotationDynamicSqlProvider.class,method = "countAnnotationSizeByState")
    @ResultType(int.class)
    int countAnnotationSizeByState(@Param("state") String state);

    @UpdateProvider(type=AnnotationDynamicSqlProvider.class,method = "updateAnnotationSelective")
    void updateAnnotationSelective(@Param("annotation") Annotation annotation);

    @UpdateProvider(type=AnnotationDynamicSqlProvider.class,method = "batchUpdateAnnotation")
    void batchUpdateAnnotation(@Param("list") List<Annotation> annotationList);

    @UpdateProvider(type=AnnotationDynamicSqlProvider.class,method = "batchUpdateAnnotationModifier")
    void batchUpdateAnnotationModifier(@Param("list") List<String> idList,@Param("modifier") String modifier);


}
