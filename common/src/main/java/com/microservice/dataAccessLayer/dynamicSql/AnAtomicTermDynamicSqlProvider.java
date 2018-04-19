package com.microservice.dataAccessLayer.dynamicSql;

import com.microservice.dataAccessLayer.entity.AnAtomicTerm;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by cjl on 2018/4/11.
 */
public class AnAtomicTermDynamicSqlProvider {

    public String queryAnAtomicTermCondition(final AnAtomicTerm anAtomicTerm){
        return new SQL(){
            {
                SELECT("*");
                FROM("an_atomic_term");
                WHERE(" 1=1 ");
                if(StringUtils.isNotBlank(anAtomicTerm.getState())){
                    WHERE("state='"+anAtomicTerm.getState()+"'");
                }
                if(StringUtils.isNotBlank(anAtomicTerm.getTerm())){
                    WHERE("term like'%"+anAtomicTerm.getTerm()+"%'");
                }
                if(StringUtils.isNotBlank(anAtomicTerm.getType())){
                    WHERE("type='"+anAtomicTerm.getType()+"'");
                }
                ORDER_BY("id");
            }
        }.toString();
    }

    public String queryAnAtomicTermJoinConcept(Map map){
        AnAtomicTerm anAtomicTerm=(AnAtomicTerm) map.get("anAtomicTerm");
        String checked=map.get("checked").toString();
        return new SQL(){
            {
                SELECT("a.id,\n" +
                        "        term,\n" +
                        "        type,\n" +
                        "        state,\n" +
                        "        a.concept_id,\n" +
                        "        gmt_created,\n" +
                        "        gmt_modified,\n" +
                        "        c.standard_name");
                FROM("an_atomic_term a");
                LEFT_OUTER_JOIN("concept c on a.concept_id=c.concept_id");
                WHERE("state='ENABLE'");
                if(StringUtils.isNotBlank(anAtomicTerm.getTerm())){
                    WHERE("term like'%"+anAtomicTerm.getTerm()+"%'");
                }
                if(StringUtils.isNotBlank(anAtomicTerm.getType())){
                    WHERE("type='"+anAtomicTerm.getType()+"'");
                }
                if(StringUtils.isNotBlank(anAtomicTerm.getId())){
                    WHERE("a.id='"+anAtomicTerm.getId()+"'");
                }
                if("true".equals(checked)){
                    WHERE("a.concept_id!=''");
                }else {
                    WHERE("a.concept_id=''");
                }
                ORDER_BY("a.id");
            }
        }.toString();
    }

    public  String updateAnAtomicTermSelective(final AnAtomicTerm anAtomicTerm){
        return new SQL(){
            {
                UPDATE("an_atomic_term");
                if(StringUtils.isNotBlank(anAtomicTerm.getTerm()))
                    SET("term=#{term}");
                if(StringUtils.isNotBlank(anAtomicTerm.getType()))
                    SET("type=#{type}");
                if(StringUtils.isNotBlank(anAtomicTerm.getState()))
                    SET("state=#{state}");
                if(StringUtils.isNotBlank(anAtomicTerm.getConceptId()))
                    SET("concept_id=#{conceptId}");
                if(StringUtils.isNotBlank(anAtomicTerm.getFromAnid()))
                    SET("from_anid=#{fromAnid}");
                if(StringUtils.isNotBlank(anAtomicTerm.getStandardName()))
                    SET("standard_name=#{standardName}");
                if(anAtomicTerm.getGmtCreated()!=null)
                    SET("gmt_created=#{gmtCreated}");
                if(anAtomicTerm.getGmtModified()!=null)
                    SET("gmt_modified=#{gmtModified}");
                WHERE("id=#{id}");
            }
        }.toString();
    }

    public String insertAnAtomicTermSelective(final AnAtomicTerm anAtomicTerm){
        return new SQL(){
            {
                INSERT_INTO("an_atomic_term");
                if(StringUtils.isNotBlank(anAtomicTerm.getStandardName()))
                    VALUES("standard_name","#{standardName}");
                if(StringUtils.isNotBlank(anAtomicTerm.getFromAnid()))
                    VALUES("from_anid","#{fromAnid}");
                if(StringUtils.isNotBlank(anAtomicTerm.getConceptId()))
                    VALUES("concept_id","#{conceptId}");
                if(StringUtils.isNotBlank(anAtomicTerm.getState()))
                    VALUES("state","#{state}");
                if(StringUtils.isNotBlank(anAtomicTerm.getType()))
                    VALUES("type","#{type}");
                if(StringUtils.isNotBlank(anAtomicTerm.getTerm()))
                    VALUES("term","#{term}");
                if(StringUtils.isNotBlank(anAtomicTerm.getId()))
                    VALUES("id","#{id}");
                if(anAtomicTerm.getGmtCreated()!=null)
                    VALUES("gmt_created","#{gmtCreated}");
                if(anAtomicTerm.getGmtModified()!=null)
                    VALUES("gmt_modified","#{gmtModified}");
            }
        }.toString();
    }

    public String batchUpdateAnAtomicTermConceptId(Map map){
        List<Integer> idArr=(List<Integer>)map.get("idArr");
        String idStr=idArr.stream().map(x->x.toString()).collect(Collectors.joining(","));
        String conceptId=map.get("conceptId").toString();
        return new SQL(){
            {
                UPDATE("an_atomic_term");
                SET("concept_id='"+conceptId+"'");
                WHERE("id in ("+idStr+")");
            }
        }.toString();
    }

    public String batchUpdateAnAtomicTermType(Map map){
        List<Integer> idArr=(List<Integer>)map.get("idArr");
        String idStr=idArr.stream().map(x->x.toString()).collect(Collectors.joining(","));
        String type=map.get("type").toString();
        return new SQL(){
            {
                UPDATE("an_atomic_term");
                SET("type='"+type+"'");
                WHERE("id in ("+idStr+")");
            }
        }.toString();
    }
}
