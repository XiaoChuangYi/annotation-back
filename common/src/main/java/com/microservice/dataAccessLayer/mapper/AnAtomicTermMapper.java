package com.microservice.dataAccessLayer.mapper;

import com.microservice.dataAccessLayer.dynamicSql.AnAtomicTermDynamicSqlProvider;
import com.microservice.dataAccessLayer.entity.AnAtomicTerm;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by cjl on 2018/4/11.
 */
public interface AnAtomicTermMapper {


    @SelectProvider(type = AnAtomicTermDynamicSqlProvider.class,method ="queryAnAtomicTermCondition" )
    @Results({
            @Result(id=true,column = "id",property ="id" ),
            @Result(column = "term",property ="term" ),
            @Result(column = "type",property ="type" ),
            @Result(column = "from_anid",property ="fromAnid" ),
            @Result(column = "state",property ="state" ),
            @Result(column = "gmt_created",property ="gmtCreated" ),
            @Result(column = "gmt_modified",property ="gmtModified" ),
            @Result(column = "concept_id",property ="conceptId" )
    })
    List<AnAtomicTerm>  listAnAtomicTermBySelective(AnAtomicTerm anAtomicTerm);

    @SelectProvider(type = AnAtomicTermDynamicSqlProvider.class,method = "queryAnAtomicTermJoinConcept")
    @Results({
            @Result(id=true,column = "id",property ="id" ),
            @Result(column = "term",property ="term" ),
            @Result(column = "type",property ="type" ),
            @Result(column = "from_anid",property ="fromAnid" ),
            @Result(column = "state",property ="state" ),
            @Result(column = "gmt_created",property ="gmtCreated" ),
            @Result(column = "gmt_modified",property ="gmtModified" ),
            @Result(column = "concept_id",property ="conceptId" )
    })
    List<AnAtomicTerm> listAnAtomicTermJoinConcept(@Param("anAtomicTerm") AnAtomicTerm anAtomicTerm,@Param("checked") String checked);

    @Select("SELECT * FROM an_atomic_term WHERE state='ENABLE' AND term = #{term} AND type = #{type} ORDER BY id")
    @Results({
            @Result(id=true,column = "id",property ="id" ),
            @Result(column = "term",property ="term" ),
            @Result(column = "type",property ="type" ),
            @Result(column = "from_anid",property ="fromAnid" ),
            @Result(column = "state",property ="state" ),
            @Result(column = "gmt_created",property ="gmtCreated" ),
            @Result(column = "gmt_modified",property ="gmtModified" ),
            @Result(column = "concept_id",property ="conceptId" )
    })
    List<AnAtomicTerm> listAnAtomicTermByTermAndType(@Param("term") String term,@Param("type") String type);


    @Select("SELECT a.id,term,type,state,a.concept_id,gmt_created,gmt_modified,c.standard_name FROM an_atomic_term a LEFT JOIN concept c ON a.concept_id=c.concept_id where a.concept_id=#{conceptId}")
    @Results({
            @Result(id=true,column = "id",property ="id" ),
            @Result(column = "term",property ="term" ),
            @Result(column = "type",property ="type" ),
            @Result(column = "gmt_created",property ="gmtCreated" ),
            @Result(column = "gmt_modified",property ="gmtModified" ),
            @Result(column = "concept_id",property ="conceptId" ),
            @Result(column ="standard_name" ,property = "standardName")
    })
    List<AnAtomicTerm> listAnAtomicTermByConceptId(@Param("conceptId") String conceptId);


    @Select("SELECT id FROM an_atomic_term WHERE type = #{type}")
    @Results({
            @Result(id=true,column = "id",property ="id" )
    })
    List<Integer> listAnAtomicTermId(@Param("conceptId") String conceptId);


    @Select("SELECT * FROM an_atomic_term WHERE state='ENABLE' and id=#{id}")
    @Results({
            @Result(id=true,column = "id",property ="id" ),
            @Result(column = "term",property ="term" ),
            @Result(column = "type",property ="type" ),
            @Result(column = "from_anid",property ="fromAnid" ),
            @Result(column = "state",property ="state" ),
            @Result(column = "gmt_created",property ="gmtCreated" ),
            @Result(column = "gmt_modified",property ="gmtModified" ),
            @Result(column = "concept_id",property ="conceptId" )
    })
    AnAtomicTerm getAnAtomicTerm(@Param("id") String id);

    @Select("SELECT * FROM an_atomic_term WHERE state='ENABLE' and type=#{type} and term=#{term} ")
    @Results({
            @Result(id=true,column = "id",property ="id" ),
            @Result(column = "term",property ="term" ),
            @Result(column = "type",property ="type" ),
            @Result(column = "from_anid",property ="fromAnid" ),
            @Result(column = "state",property ="state" ),
            @Result(column = "gmt_created",property ="gmtCreated" ),
            @Result(column = "gmt_modified",property ="gmtModified" ),
            @Result(column = "concept_id",property ="conceptId" )
    })
    AnAtomicTerm getAnAtomicTermAndTypeTermNotNull(@Param("type") String type,@Param("term") String term);

    @Update("update an_atomic_term set concept_id='' where id=#{id}")
    void blankAnAtomicTermConceptId(@Param("id") String id);

    @UpdateProvider(type = AnAtomicTermDynamicSqlProvider.class,method = "updateAnAtomicTermSelective")
    void updateAnAtomicTermSelective(AnAtomicTerm anAtomicTerm);

    @Update("update UPDATE an_atomic_term SET concept_id =#{conceptId} WHERE id =#{id,jdbcType=VARCHAR}")
    void updateAnAtomicTermConceptIdById(@Param("conceptId") String conceptId,@Param("id") String id);

    @UpdateProvider(type = AnAtomicTermDynamicSqlProvider.class,method = "batchUpdateAnAtomicTermConceptId")
    void batchUpdateAnAtomicTermConceptIdByIdArr(@Param("idArr") List<String> idArr,@Param("conceptId") String conceptId);

    @UpdateProvider(type = AnAtomicTermDynamicSqlProvider.class,method = "batchUpdateAnAtomicTermType")
    void batchUpdateAnAtomicTermTypeByIdArr(@Param("idArr") List<String> idArr,@Param("type") String type);

    @InsertProvider(type=AnAtomicTermDynamicSqlProvider.class,method = "insertAnAtomicTermSelective")
    void insertAnAtomicTermSelective(AnAtomicTerm anAtomicTerm);

    @Delete("delete from an_atomic_term where id=#{id}")
    void deleteAnAtomicTermById(@Param("id") String id);
 }
