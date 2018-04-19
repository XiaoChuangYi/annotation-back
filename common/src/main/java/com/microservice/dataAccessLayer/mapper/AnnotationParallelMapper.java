package com.microservice.dataAccessLayer.mapper;

import com.microservice.dataAccessLayer.dynamicSql.AnnotationParallelDynamicSqlProvider;
import com.microservice.dataAccessLayer.entity.AnnotationParallel;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by cjl on 2018/4/19.
 */
public interface AnnotationParallelMapper {

    @SelectProvider(type = AnnotationParallelDynamicSqlProvider.class,method = "queryAnnotationParallelSelective")
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
    List<AnnotationParallel> listAnnotationParallelByCondition(@Param("annotationParallel") AnnotationParallel annotationParallel,
                                                               @Param("stateList") List<String> stateList);



    @Select("select * from annotation_parallel where id=#{id}")
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
    AnnotationParallel getAnnotationParallelById(@Param("id") int id);

    @UpdateProvider(type=AnnotationParallelDynamicSqlProvider.class,method = "updateAnnotationParallelSelective")
    int updateAnnotationParallelSelective(AnnotationParallel annotationParallel);

    @UpdateProvider(type=AnnotationParallelDynamicSqlProvider.class,method = "updateAnnotationParallelUserModifierByIdArr")
    void updateAnnotationParallelUserModifierByIdArr(@Param("idArr") List<Integer> idArr,@Param("userModifier") int userModifier,@Param("state") String state);

    @UpdateProvider(type = AnnotationParallelDynamicSqlProvider.class,method = "batchUpdateAnnotationParallelStateByIdArr")
    void batchUpdateAnnotationParallelStateByIdArr(@Param("idArr") List<Integer> idArr,@Param("state") String state);
}
