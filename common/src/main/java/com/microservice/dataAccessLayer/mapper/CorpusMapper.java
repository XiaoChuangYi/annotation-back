package com.microservice.dataAccessLayer.mapper;

import com.microservice.dataAccessLayer.dynamicSql.CorpusDynamicSqlProvider;
import com.microservice.dataAccessLayer.entity.Corpus;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by cjl on 2018/4/11.
 */
@Component
public interface CorpusMapper {

    @SelectProvider(type = CorpusDynamicSqlProvider.class,method = "queryCorpusByCondition")
    @Results({
            @Result(id=true,column = "id",property = "id",jdbcType = JdbcType.VARCHAR),
            @Result(column = "term",property = "term"),
            @Result(column = "type",property = "type"),
            @Result(column = "state",property = "state"),
            @Result(column = "memo",property = "memo"),
            @Result(column = "gmt_created",property = "gmtCreated"),
            @Result(column = "gmt_modified",property = "gmtModified")
    })
    List<Corpus> listCorpusByCondition(@Param("corpus") Corpus corpus);

    @UpdateProvider(type = CorpusDynamicSqlProvider.class,method = "batchUpdateCorpusByIdArr")
    void batchUpdateCorpusByIdArr(@Param("idArr") List<String> idArr,@Param("type") String type);

    @Update("update corpus set state=#{state} where id=#{id}")
    void updateCorpusStateById(@Param("state") String state,@Param("id") String id);
}
