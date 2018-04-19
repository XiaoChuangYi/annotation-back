package com.microservice.dataAccessLayer.mapper;

import com.microservice.dataAccessLayer.dynamicSql.DrawDynamicSqlProvider;
import com.microservice.dataAccessLayer.entity.Draw;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by cjl on 2018/4/2.
 */

public interface DrawMapper {

    /**
     * draw表关联到an_type表顺便关联出type_code字段
     */
    @Select("SELECT draw.id,type_code,IFNULL(draw_name,'') as draw_name from draw  draw RIGHT JOIN an_type type  ON type.id=draw.id")
    @Results({
            @Result(id=true,column = "id",property ="id" ),
            @Result(column = "draw_name",property ="drawName" ),
            @Result(column = "type_code",property ="typeCode" )
    })
    List<Draw> selectDrawJoinAnType();

    /**
     * 根据对应的typeCode关联获取对应draw表里的draw_name字段的数据
     */

    @Select("select IFNULL(draw_name,'') as draw_name,type_code from draw d inner join an_type t on d.id=t.id where t.type_code=#{typeCode}")
    @Results({
            @Result(id=true,column = "id",property ="id" ),
            @Result(column = "draw_name",property ="drawName" ),
            @Result(column = "type_label",property ="typeLabel" ),
            @Result(column = "type_code",property ="typeCode" )
    })
    Draw selectDrawByTypeCode(String typeCode);

    /**
     * 通过typeCode和drawName字段条件查询draw表
     */

    @Results({
            @Result(id=true,column = "id",property ="id" ),
            @Result(column = "draw_name",property ="drawName" ),
            @Result(column = "type_label",property ="typeLabel" ),
            @Result(column = "type_code",property ="typeCode" )
    })
    @SelectProvider(type = DrawDynamicSqlProvider.class,method = "selectDrawByCondition")
    List<Draw> selectDrawByCondition(Draw draw);

    /**
     * 根据主键id更新draw表
     */
    @UpdateProvider(type = DrawDynamicSqlProvider.class,method = "updateDrawBySelective")
    void updateDrawSelectiveByPrimaryKey(Draw draw);

    /**
     * 新增draw表
     */
    @InsertProvider(type=DrawDynamicSqlProvider.class,method = "insertDrawSelective")
    @Options(useGeneratedKeys = true, keyColumn = "id")
    void insertDrawSelective(Draw draw);
}
