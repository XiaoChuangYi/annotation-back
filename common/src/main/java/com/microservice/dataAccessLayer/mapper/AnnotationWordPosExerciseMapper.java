package com.microservice.dataAccessLayer.mapper;

import com.microservice.dataAccessLayer.dynamicSql.AnnotationWordPosExerciseDynamicSqlProvider;
import com.microservice.dataAccessLayer.entity.AnnotationWordPosExercise;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by cjl on 2018/5/17.
 */
@Component
public interface AnnotationWordPosExerciseMapper {


    @Select("select * from annotation_word_exercise")
    @Results({
            @Result(id=true,column ="id" ,property = "id"),
            @Result(column ="origin_text" ,property = "originText"),
            @Result(column = "standard_annotation",property = "standardAnnotation"),
            @Result(column = "auto_annotation",property = "autoAnnotation"),
            @Result(column = "gmt_created",property = "gmtCreated"),
            @Result(column = "gmt_modified",property = "gmtModified"),
            @Result(column = "memo",property = "memo")
    })
    List<AnnotationWordPosExercise> listAnnotationWordExerciseAll();

    @SelectProvider(type = AnnotationWordPosExerciseDynamicSqlProvider.class,method = "listAnnotationWordExerciseByCondition")
    @Results({
            @Result(id=true,column ="id" ,property = "id"),
            @Result(column ="origin_text" ,property = "originText"),
            @Result(column = "standard_annotation",property = "standardAnnotation"),
            @Result(column = "auto_annotation",property = "autoAnnotation"),
            @Result(column = "gmt_created",property = "gmtCreated"),
            @Result(column = "gmt_modified",property = "gmtModified"),
            @Result(column = "memo",property = "memo")
    })
    List<AnnotationWordPosExercise> listAnnotationWordExerciseByCondition(AnnotationWordPosExercise annotationWordPosExercise);

    @Select("select * from annotation_word_exercise where id in (${idArrTemp})")
    @Results({
            @Result(id=true,column ="id" ,property = "id"),
            @Result(column ="origin_text" ,property = "originText"),
            @Result(column = "standard_annotation",property = "standardAnnotation"),
            @Result(column = "auto_annotation",property = "autoAnnotation"),
            @Result(column = "gmt_created",property = "gmtCreated"),
            @Result(column = "gmt_modified",property = "gmtModified"),
            @Result(column = "memo",property = "memo")
    })
    List<AnnotationWordPosExercise> listAnnotationWordExerciseByIdArr(@Param("idArrTemp") String idArrTemp);

    @Select("select * from annotation_word_exercise where id=#{id}")
    @Results({
            @Result(id=true,column ="id" ,property = "id"),
            @Result(column ="origin_text" ,property = "originText"),
            @Result(column = "standard_annotation",property = "standardAnnotation"),
            @Result(column = "auto_annotation",property = "autoAnnotation"),
            @Result(column = "gmt_created",property = "gmtCreated"),
            @Result(column = "gmt_modified",property = "gmtModified"),
            @Result(column = "memo",property = "memo")
    })
    AnnotationWordPosExercise getAnnotationWordPosExerciseById(@Param("id") int id);

    @UpdateProvider(type = AnnotationWordPosExerciseDynamicSqlProvider.class,method = "updateAnnotationWordExercise")
    void updateAnnotationWordExercise(AnnotationWordPosExercise annotationWordPosExercise);
}
