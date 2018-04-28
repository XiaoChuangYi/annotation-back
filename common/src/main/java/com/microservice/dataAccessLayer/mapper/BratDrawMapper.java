package com.microservice.dataAccessLayer.mapper;

import com.microservice.dataAccessLayer.dynamicSql.BratDrawDynamicSqlProvider;
import com.microservice.dataAccessLayer.entity.BratDraw;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by cjl on 2018/4/20.
 */
@Component
public interface BratDrawMapper {
    /**
     * draw表关联到an_type表顺便关联出type_code字段
     */
    @Select("SELECT draw.id,type_code,IFNULL(draw_name,'') as draw_name from brat_draw  draw RIGHT JOIN an_type type  ON type.id=draw.type_id where type.task_id=#{taskId}")
    @Results({
            @Result(id=true,column = "id",property ="id" ),
            @Result(column = "draw_name",property ="drawName" ),
            @Result(column = "type_code",property ="typeCode" )
    })
    List<BratDraw> selectDrawJoinAnType(@Param("taskId") int taskId);

    /**
     * 根据对应的typeCode关联获取对应draw表里的draw_name字段的数据
     */

    @Select("select IFNULL(draw_name,'') as draw_name,type_code from brat_draw d inner join an_type t on d.type_id=t.id where t.type_code=#{typeCode}" +
            " and t.task_id=#{taskId}")
    @Results({
            @Result(id=true,column = "id",property ="id" ),
            @Result(column = "draw_name",property ="drawName" ),
            @Result(column = "type_label",property ="typeLabel" ),
            @Result(column = "type_code",property ="typeCode" )
    })
    BratDraw selectDrawByTypeCode(@Param("typeCode") String typeCode,@Param("taskId") int taskId);

    /**
     * 通过typeCode和drawName字段条件查询draw表
     */

    @Results({
            @Result(id=true,column = "id",property ="id" ),
            @Result(column = "draw_name",property ="drawName" ),
            @Result(column = "type_label",property ="typeLabel" ),
            @Result(column = "type_code",property ="typeCode" )
    })
    @SelectProvider(type = BratDrawDynamicSqlProvider.class,method = "selectDrawByCondition")
    List<BratDraw> selectDrawByCondition(BratDraw draw);

    /**
     * 根据主键id更新draw表
     */
    @UpdateProvider(type = BratDrawDynamicSqlProvider.class,method = "updateDrawBySelective")
    void updateDrawSelectiveByPrimaryKey(BratDraw draw);

    /**
     * 新增draw表
     */
    @InsertProvider(type=BratDrawDynamicSqlProvider.class,method = "insertDrawSelective")
    @Options(useGeneratedKeys = true, keyColumn = "id")
    void insertDrawSelective(BratDraw draw);
}
