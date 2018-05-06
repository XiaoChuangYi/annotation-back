package com.microservice.dataAccessLayer.mapper;

import com.microservice.dataAccessLayer.dynamicSql.AnnotationApposeDynamicSqlProvider;
import com.microservice.dataAccessLayer.entity.AnnotationAppose;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by cjl on 2018/4/19.
 */
@Component
public interface AnnotationApposeMapper {

    @SelectProvider(type = AnnotationApposeDynamicSqlProvider.class,method = "queryAnnotationParallelSelective")
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
    List<AnnotationAppose> listAnnotationParallelByCondition(@Param("annotationParallel") AnnotationAppose annotationParallel,
                                                             @Param("stateList") List<String> stateList);



    @Select("select * from annotation_appose where id=#{id}")
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
    AnnotationAppose getAnnotationParallelById(@Param("id") int id);

    @UpdateProvider(type=AnnotationApposeDynamicSqlProvider.class,method = "updateAnnotationParallelSelective")
    int updateAnnotationParallelSelective(AnnotationAppose annotationParallel);

    @UpdateProvider(type=AnnotationApposeDynamicSqlProvider.class,method = "updateAnnotationParallelUserModifierByIdArr")
    void updateAnnotationParallelUserModifierByIdArr(@Param("idArr") List<Integer> idArr,@Param("userModifier") int userModifier,@Param("state") String state);

    @UpdateProvider(type = AnnotationApposeDynamicSqlProvider.class,method = "batchUpdateAnnotationParallelStateByIdArr")
    void batchUpdateAnnotationParallelStateByIdArr(@Param("idArr") List<Integer> idArr,@Param("state") String state);
}
