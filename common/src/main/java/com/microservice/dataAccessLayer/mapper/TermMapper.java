package com.microservice.dataAccessLayer.mapper;

import com.microservice.dataAccessLayer.dynamicSql.TermDynamicSqlProvider;
import com.microservice.dataAccessLayer.entity.Term;
import com.microservice.pojo.OriginNameGroup;
import com.microservice.pojo.TermLabel;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by cjl on 2018/4/11.
 */
public interface TermMapper {

    @SelectProvider(type = TermDynamicSqlProvider.class,method = "queryTermByCondition")
    @Results({
            @Result(id=true,column = "id",property = "id"),
            @Result(column = "term_id",property = "termId"),
            @Result(column = "pterm_id",property = "ptermId"),
            @Result(column = "term_code",property = "termCode"),
            @Result(column = "term_type",property = "termType"),
            @Result(column = "term_name",property = "termName"),
            @Result(column = "origin_name",property = "originName"),
            @Result(column = "has_children",property = "hasChildren"),
            @Result(column = "label",property = "label"),
            @Result(column = "state",property = "state"),
            @Result(column = "concept_id",property = "conceptId")
    })
    List<Term> listTermByCondition(Term term);

    @SelectProvider(type = TermDynamicSqlProvider.class,method = "queryTermJoinConcept")
    @Results({
            @Result(id=true,column = "id",property = "id"),
            @Result(column = "term_id",property = "termId"),
            @Result(column = "pterm_id",property = "ptermId"),
            @Result(column = "term_code",property = "termCode"),
            @Result(column = "term_type",property = "termType"),
            @Result(column = "term_name",property = "termName"),
            @Result(column = "origin_name",property = "originName"),
            @Result(column = "has_children",property = "hasChildren"),
            @Result(column = "label",property = "label"),
            @Result(column = "state",property = "state"),
            @Result(column = "concept_id",property = "conceptId"),
            @Result(column = "standard_name",property = "standardName")
    })
    List<Term> listTermJoinConcept(@Param("term") Term term,@Param("checked") String checked);


    @SelectProvider(type = TermDynamicSqlProvider.class,method = "queryTermJoinConceptByTerm")
    @Results({
            @Result(id=true,column = "id",property = "id"),
            @Result(column = "term_id",property = "termId"),
            @Result(column = "pterm_id",property = "ptermId"),
            @Result(column = "term_code",property = "termCode"),
            @Result(column = "term_type",property = "termType"),
            @Result(column = "term_name",property = "termName"),
            @Result(column = "origin_name",property = "originName"),
            @Result(column = "has_children",property = "hasChildren"),
            @Result(column = "label",property = "label"),
            @Result(column = "state",property = "state"),
            @Result(column = "concept_id",property = "conceptId"),
            @Result(column = "standard_name",property = "standardName")
    })
    List<Term> listTermJoinConceptByTerm(@Param("term") Term term);


    @Select("select distinct(term_type) from term")
    @ResultType(List.class)
    List<String> listDistinctTermType();

    @Select("select count(id) as id_groups,origin_name from term GROUP BY origin_name order by id limit #{groupIndex},#{groupSize}")
    @Results({
            @Result(column = "id_groups",property = "idGroups"),
            @Result(column = "origin_name",property ="originName")
    })
    List<OriginNameGroup> listTermGroupByOriginName(@Param("groupIndex") int groupIndex,@Param("groupSize") int groupSize);


    @Select("select * from term where id=#{id}")
    @Results({
            @Result(id=true,column = "id",property = "id"),
            @Result(column = "term_id",property = "termId"),
            @Result(column = "pterm_id",property = "ptermId"),
            @Result(column = "term_code",property = "termCode"),
            @Result(column = "term_type",property = "termType"),
            @Result(column = "term_name",property = "termName"),
            @Result(column = "origin_name",property = "originName"),
            @Result(column = "has_children",property = "hasChildren"),
            @Result(column = "label",property = "label"),
            @Result(column = "state",property = "state"),
            @Result(column = "concept_id",property = "conceptId")
    })
    Term getTermById(@Param("id") int id);


    @Update("update term set concept_id='' where id=#{id}")
    void blankTermConceptId(@Param("id") int id);

    @UpdateProvider(type = TermDynamicSqlProvider.class,method = "updateTermSelective")
    void updateTermSelective(@Param("term") Term term);

    @UpdateProvider(type = TermDynamicSqlProvider.class,method = "batchUpdateTermLabel")
    void batchUpdateTermLabel(@Param("termLabelArr") List<TermLabel> termLabelList);

    @UpdateProvider(type = TermDynamicSqlProvider.class,method = "batchUpdateTermConceptId")
    void batchUpdateTermConceptId(@Param("idArr") List<Integer> idArr,@Param("conceptId") String conceptId);

    @UpdateProvider(type = TermDynamicSqlProvider.class,method = "batchCoverTermLabel")
    void batchCoverTermLabel(@Param("idArr") List<Integer> idArr,@Param("label") String label);

    @InsertProvider(type = TermDynamicSqlProvider.class,method = "insertTermSelective")
    @Options(useGeneratedKeys = true, keyColumn = "id")
    void insertTermSelective(Term term);


    @Delete("delete from term where id=#{id}")
    void deleteByPrimaryKey(@Param("id") int id);

    @Select("select count(idGroup) from (select count(id) as idGroup from term group by origin_name order by id) a")
    @ResultType(Integer.class)
    int countGroupsByOriginName();

}
