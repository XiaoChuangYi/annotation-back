package com.microservice.dataAccessLayer.mapper;

import com.microservice.dataAccessLayer.dynamicSql.TagDynamicSqlProvider;
import com.microservice.dataAccessLayer.entity.Tag;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by cjl on 2018/4/11.
 */
public interface TagMapper {

    @InsertProvider(type = TagDynamicSqlProvider.class,method = "batchInsertTag")
    @Options(useGeneratedKeys = true, keyColumn = "id")
    void batchInsertTag(@Param("listTag")List<String> listTag);

    @Select("select * from tag")
    @Results({
            @Result(id=true,column = "id",property = "id"),
            @Result(column = "tag_name",property = "tagName")
    })
    List<Tag> listTag();

    @Insert("insert into tag (tag_name) values(#{tagName})")
    @Options(useGeneratedKeys = true,keyColumn = "id")
    void insertTag(@Param("tagName") String tagName);
}
