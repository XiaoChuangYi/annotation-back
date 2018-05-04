package com.microservice.dataAccessLayer.mapper;

import com.microservice.dataAccessLayer.dynamicSql.AnnotationWordPosDynamicSqlProvider;
//import com.microservice.dataAccessLayer.entity.Annotation;
import com.microservice.dataAccessLayer.entity.AnnotationWordPos;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by cjl on 2018/4/28.
 */
@Component
public interface AnnotationWordPosMapper {

    @SelectProvider(type = AnnotationWordPosDynamicSqlProvider.class,method = "queryAnnotationByCondition")
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
    List<AnnotationWordPos> listAnnotationByCondition(@Param("annotation") AnnotationWordPos annotation, @Param("sort") String sort);


    @SelectProvider(type = AnnotationWordPosDynamicSqlProvider.class,method = "listAnnotationByStatesAndModifier")
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
    List<AnnotationWordPos> listAnnotationByStatesAndModifier(@Param("list") List<String> stateList,@Param("modifier") String modifier);

    @SelectProvider(type = AnnotationWordPosDynamicSqlProvider.class,method = "queryAnnotationByStateList")
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
    List<AnnotationWordPos> listAnnotationByStateList(@Param("list") List<String> stateList,@Param("sort") String sort);

    @SelectProvider(type = AnnotationWordPosDynamicSqlProvider.class,method = "queryAnnotationByStateListUserModifier")
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
    List<AnnotationWordPos> listAnnotationByStateListUseModifier(@Param("list") List<String> stateList,@Param("modifier") String modifier,@Param("sort") String sort);




    @Select("select * from annotation_word_pos where id=#{id}")
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
    AnnotationWordPos getAnnotationById(@Param("id") String id);

    @SelectProvider(type = AnnotationWordPosDynamicSqlProvider.class,method = "queryAnnotationByIdArr")
    List<AnnotationWordPos> listAnnotationByIdArr(@Param("idArr") List<String> idArr);


    @SelectProvider(type=AnnotationWordPosDynamicSqlProvider.class,method = "countAnnotationSizeByState")
    @ResultType(int.class)
    int countAnnotationSizeByState(@Param("state") String state);

    @UpdateProvider(type=AnnotationWordPosDynamicSqlProvider.class,method = "updateAnnotationSelective")
    void updateAnnotationSelective(AnnotationWordPos annotation);

    @UpdateProvider(type=AnnotationWordPosDynamicSqlProvider.class,method = "batchUpdateAnnotation")
    void batchUpdateAnnotation(@Param("list") List<AnnotationWordPos> annotationList);

    @UpdateProvider(type=AnnotationWordPosDynamicSqlProvider.class,method = "batchUpdateAnnotationModifier")
    void batchUpdateAnnotationModifier(@Param("list") List<String> idList,@Param("modifier") String modifier);

    @InsertProvider(type=AnnotationWordPosDynamicSqlProvider.class,method = "saveAnnotationSelective")
    @Options(useGeneratedKeys = true,keyColumn ="id" )
    void saveAnnotationSelective(AnnotationWordPos annotation);
}
