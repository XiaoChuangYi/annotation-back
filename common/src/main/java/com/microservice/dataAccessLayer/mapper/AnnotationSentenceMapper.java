package com.microservice.dataAccessLayer.mapper;

import com.microservice.dataAccessLayer.dynamicSql.AnnotationSentenceDynamicSqlProvider;
import com.microservice.dataAccessLayer.entity.AnnotationSentence;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by cjl on 2018/4/16.
 */
public interface AnnotationSentenceMapper {



    @SelectProvider(type = AnnotationSentenceDynamicSqlProvider.class,method = "queryAnnotationSentenceSelective")
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
    List<AnnotationSentence> listAnnotationSentenceByCondition(@Param("annotationSentence") AnnotationSentence annotationSentence,
                                                               @Param("stateList") List<String> stateList);


    @SelectProvider(type = AnnotationSentenceDynamicSqlProvider.class,method = "queryAutoDistributionAnnotation")
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
    List<AnnotationSentence> listAutoDistributionAnnotationSentence(@Param("annotationSentence") AnnotationSentence annotationSentence,
                                                               @Param("stateList") List<String> stateList);

    @Select("select * from annotation_sentence where id=#{id}")
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
    AnnotationSentence getAnnotationSentenceById(@Param("id") int id);

    @UpdateProvider(type=AnnotationSentenceDynamicSqlProvider.class,method = "updateAnnotationSentenceSelective")
    int updateAnnotationSentenceSelective(AnnotationSentence annotationSentence);

    @UpdateProvider(type=AnnotationSentenceDynamicSqlProvider.class,method = "updateAnnotationSentenceUserModifierByIdArr")
    void updateAnnotationSentenceUserModifierByIdArr(@Param("idArr") List<Integer> idArr,@Param("userModifier") int userModifier,@Param("state") String state);

    @UpdateProvider(type = AnnotationSentenceDynamicSqlProvider.class,method = "batchUpdateAnnotationSentenceStateByIdArr")
    void batchUpdateAnnotationSentenceStateByIdArr(@Param("idArr") List<Integer> idArr,@Param("state") String state);
}
