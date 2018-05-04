package com.microservice.dataAccessLayer.mapper;

import com.microservice.dataAccessLayer.dynamicSql.AnnotationSentenceExerciseDynamicSqlProvider;
import com.microservice.dataAccessLayer.entity.AnnotationSentenceExercise;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by cjl on 2018/5/3.
 */
@Component
public interface AnnotationSentenceExerciseMapper {

    @SelectProvider(type = AnnotationSentenceExerciseDynamicSqlProvider.class,method = "queryAnnotationSentExerciseSelective")
    @Results({
            @Result(id=true,column ="id" ,property = "id"),
            @Result(column ="origin_text" ,property = "originText"),
            @Result(column = "standard_annotation",property = "standardAnnotation"),
            @Result(column = "auto_annotation",property = "autoAnnotation"),
            @Result(column = "gmt_created",property = "gmtCreated"),
            @Result(column = "memo",property = "memo")
    })
    List<AnnotationSentenceExercise> listAnnotationSentExerciseByCondition(AnnotationSentenceExercise annotationSentExercise);


    @Select("select * from annotation_sentence_exercises")
    @Results({
            @Result(id=true,column ="id" ,property = "id"),
            @Result(column ="origin_text" ,property = "originText"),
            @Result(column = "standard_annotation",property = "standardAnnotation"),
            @Result(column = "auto_annotation",property = "autoAnnotation"),
            @Result(column = "gmt_created",property = "gmtCreated"),
            @Result(column = "memo",property = "memo")
    })
    List<AnnotationSentenceExercise> listAnnotationSentExerciseAll();


    @Select("select * from annotation_sentence_exercises where id in (${idArrTemp})")
    @Results({
            @Result(id=true,column ="id" ,property = "id"),
            @Result(column ="origin_text" ,property = "originText"),
            @Result(column = "standard_annotation",property = "standardAnnotation"),
            @Result(column = "auto_annotation",property = "autoAnnotation"),
            @Result(column = "gmt_created",property = "gmtCreated"),
            @Result(column = "memo",property = "memo")
    })
    List<AnnotationSentenceExercise> listAnnotationSentExerciseByIdArr(@Param("idArrTemp") String idArrTemp);


    @Select("select * from annotation_sentence_exercises where id=#{id}")
    @Results({
            @Result(id=true,column ="id" ,property = "id"),
            @Result(column ="origin_text" ,property = "originText"),
            @Result(column = "standard_annotation",property = "standardAnnotation"),
            @Result(column = "auto_annotation",property = "autoAnnotation"),
            @Result(column = "gmt_created",property = "gmtCreated"),
            @Result(column = "memo",property = "memo")
    })
    AnnotationSentenceExercise  getAnnotationSentExerciseById(@Param("id") int id);



}
