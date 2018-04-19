package com.microservice.dataAccessLayer.mapper;

import com.microservice.dataAccessLayer.dynamicSql.TypeDynamicSqlProvider;
import com.microservice.dataAccessLayer.entity.Type;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by cjl on 2018/4/2.
 */
public interface TypeMapper {


    @SelectProvider(type = TypeDynamicSqlProvider.class,method = "queryTypeByCondition")
    @Results({
            @Result(id=true,property = "id",column = "id"),
            @Result(property = "typeName",column = "type_name"),
            @Result(property = "typeCode",column = "type_code"),
            @Result(property = "parentId",column = "parent_id"),
            @Result(property = "state",column = "state"),
            @Result(property = "hasChildren",column = "has_children"),
            @Result(property = "gmtCreated",column = "gmt_created"),
            @Result(property = "gmtModified",column = "gmt_modified")
    })
    List<Type> listTypeByCondition(Type type);


    @SelectProvider(type = TypeDynamicSqlProvider.class,method = "queryTypeAndShowParent")
    @Results({
            @Result(id=true,property = "id",column = "id"),
            @Result(property = "typeName",column = "type_name"),
            @Result(property = "typeCode",column = "type_code"),
            @Result(property = "parentId",column = "parent_id"),
            @Result(property = "state",column = "state"),
            @Result(property = "hasChildren",column = "has_children"),
            @Result(property = "gmtCreated",column = "gmt_created"),
            @Result(property = "gmtModified",column = "gmt_modified"),
            @Result(property = "parentType",column = "parent_type")
    })
    List<Type> listTypeAndShowParent(Type type);


    @Select("select * from an_type where parent_id=#{id}")
    @Results({
            @Result(id=true,property = "id",column = "id"),
            @Result(property = "typeName",column = "type_name"),
            @Result(property = "typeCode",column = "type_code"),
            @Result(property = "parentId",column = "parent_id"),
            @Result(property = "state",column = "state"),
            @Result(property = "hasChildren",column = "has_children"),
            @Result(property = "gmtCreated",column = "gmt_created"),
            @Result(property = "gmtModified",column = "gmt_modified")
    })
    List<Type> listChildrenTypeById(@Param("id") String id);

    @Select("select * from an_type where state='ENABLE' order by type_code")
    @Results({
            @Result(id=true,property = "id",column = "id"),
            @Result(property = "typeName",column = "type_name"),
            @Result(property = "typeCode",column = "type_code"),
            @Result(property = "parentId",column = "parent_id"),
            @Result(property = "state",column = "state"),
            @Result(property = "hasChildren",column = "has_children"),
            @Result(property = "gmtCreated",column = "gmt_created"),
            @Result(property = "gmtModified",column = "gmt_modified")
    })
    List<Type> listEnableType();

    @Select("select * from an_type where type_code=#{typeCode}")
    @Results({
            @Result(id=true,property = "id",column = "id"),
            @Result(property = "typeName",column = "type_name"),
            @Result(property = "typeCode",column = "type_code"),
            @Result(property = "parentId",column = "parent_id"),
            @Result(property = "state",column = "state"),
            @Result(property = "hasChildren",column = "has_children"),
            @Result(property = "gmtCreated",column = "gmt_created"),
            @Result(property = "gmtModified",column = "gmt_modified")
    })
    Type getTypeByTypeCode(@Param("typeCode") String typeCode);


    @Select("select * from an_type where type_code=#{typeCode} and state='ENABLE'")
    @Results({
            @Result(id=true,property = "id",column = "id"),
            @Result(property = "typeName",column = "type_name"),
            @Result(property = "typeCode",column = "type_code"),
            @Result(property = "parentId",column = "parent_id"),
            @Result(property = "state",column = "state"),
            @Result(property = "hasChildren",column = "has_children"),
            @Result(property = "gmtCreated",column = "gmt_created"),
            @Result(property = "gmtModified",column = "gmt_modified")
    })
    Type getDisableTypeByTypeCode(@Param("typeCode") String typeCode);

    @Select("select * from an_type where id=#{parentId}")
    @Results({
            @Result(id=true,property = "id",column = "id"),
            @Result(property = "typeName",column = "type_name"),
            @Result(property = "typeCode",column = "type_code"),
            @Result(property = "parentId",column = "parent_id"),
            @Result(property = "state",column = "state"),
            @Result(property = "hasChildren",column = "has_children"),
            @Result(property = "gmtCreated",column = "gmt_created"),
            @Result(property = "gmtModified",column = "gmt_modified")
    })
    Type getTypeByParentId(@Param("parentId") String id);


    @Update("update type set type_code=#{typeCode} where id=#{id}")
    void updateTypeCodeById(@Param("typeCode") String typeCode,@Param("id") String id);

    @UpdateProvider(type = TypeDynamicSqlProvider.class,method = "updateTypeSelectiveById")
    void updateTypeSelectiveById(Type type);

    @InsertProvider(type=TypeDynamicSqlProvider.class,method = "insertTypeSelective")
    void insertTypeSelective(Type type);

    @Select("SELECT MAX(id+0) FROM an_type")
    @ResultType(Integer.class)
    int getTypeMaxId();

}
