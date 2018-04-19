package com.microservice.dataAccessLayer.dynamicSql;

import com.microservice.dataAccessLayer.entity.Term;
import com.microservice.pojo.TermLabel;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by cjl on 2018/4/11.
 */
public class TermDynamicSqlProvider {

    public String queryTermByCondition(final Term term){
        return new SQL(){
            {
                SELECT("*");
                FROM("term");
                WHERE("state='ENABLE'");
                if(StringUtils.isNotBlank(term.getTermName())){
                    WHERE("term_name like concat('%',#{termName},'%')");
                }
                OR();
                WHERE("state is null");
                if(StringUtils.isNotBlank(term.getTermName())){
                    WHERE("term_name like concat('%',#{termName},'%')");
                }
            }
        }.toString();
    }

    public String queryTermJoinConceptByTerm(Map map){
        Term term=(Term)map.get("term");
        StringBuilder sb=new StringBuilder();
        sb.append("select t.id,t.term_id,pterm_id,term_code,term_type,term_name," +
                "origin_name,has_children,label,state,t.concept_id,c.standard_name ");
        sb.append("from term t left join concept c on t.concept_id=c.concept_id ");
        sb.append("where state='ENABLE' ");
        if(StringUtils.isNotBlank(term.getTermId())){
            sb.append("  and t.term_id='"+term.getTermId()+"' ");
        }
        if(StringUtils.isNotBlank(term.getOriginName())){
            sb.append(" and t.origin_name='"+term.getOriginName()+"' ");
        }
        if(StringUtils.isNotBlank(term.getConceptId())){
            sb.append(" and t.concept_id='"+term.getConceptId()+"' ");
        }
        sb.append("or state is null ");
        if(StringUtils.isNotBlank(term.getTermId())){
            sb.append(" and t.term_id='"+term.getTermId()+"' ");
        }
        if(StringUtils.isNotBlank(term.getOriginName())){
            sb.append(" and t.origin_name='"+term.getOriginName()+"' ");
        }
        if(StringUtils.isNotBlank(term.getConceptId())){
            sb.append(" and t.concept_id='"+term.getConceptId()+"' ");
        }
        sb.append("order by t.id");
        return sb.toString();
    }

    public String queryTermJoinConcept(Map map){
        Term term=(Term)map.get("term");
        String checked=map.get("checked").toString();
        StringBuilder sb=new StringBuilder();
        sb.append("select t.id,t.term_id,pterm_id,term_code,term_type,term_name," +
                "origin_name,has_children,label,state,t.concept_id,c.standard_name ");
        sb.append("from term t left join concept c on t.concept_id=c.concept_id ");
        sb.append("where state='ENABLE' ");
        if(StringUtils.isNotBlank(term.getTermId())){
            sb.append("and term_id ='"+term.getTermId()+"' ");
        }
        if(StringUtils.isNotBlank(term.getTermName())){
            sb.append("and term_name like concat('%','"+term.getTermName()+"','%') ");
        }
        if(StringUtils.isNotBlank(term.getTermType())){
            sb.append("and term_type like concat('%','"+term.getTermType()+"','%') ");
        }
        if(StringUtils.isNotBlank(term.getLabel())){
            sb.append("and label like concat('%','"+term.getLabel()+"','%') ");
        }
        if(StringUtils.isNotBlank(term.getOriginName())){
            sb.append("and origin_name like concat('%','"+term.getOriginName()+"','%') ");
        }
        if(checked.equals("true")){
            sb.append("and t.concept_id !=''");
        }else {
            sb.append("and t.concept_id =''");
        }
        sb.append("or state is null ");
        if(StringUtils.isNotBlank(term.getTermId())){
            sb.append("and term_id ='"+term.getTermId()+"' ");
        }
        if(StringUtils.isNotBlank(term.getTermName())){
            sb.append("and term_name like concat('%','"+term.getTermName()+"','%') ");
        }
        if(StringUtils.isNotBlank(term.getTermType())){
            sb.append("and term_type like concat('%','"+term.getTermType()+"','%') ");
        }
        if(StringUtils.isNotBlank(term.getLabel())){
            sb.append("and label like concat('%','"+term.getLabel()+"','%') ");
        }
        if(StringUtils.isNotBlank(term.getOriginName())){
            sb.append("and origin_name like concat('%','"+term.getOriginName()+"','%') ");
        }
        if(checked.equals("true")){
            sb.append("and t.concept_id !=''");
        }else {
            sb.append("and t.concept_id =''");
        }
        return sb.toString();
    }

    public String updateTermSelective(final Term term){
        return new SQL(){
            {
                UPDATE("term");
                if(StringUtils.isNotBlank(term.getConceptId()))
                    SET("concept_id=#{conceptId}");
                if(StringUtils.isNotBlank(term.getOriginName()))
                    SET("origin_name=#{originName}");
                if(StringUtils.isNotBlank(term.getTermId()))
                    SET("term_id=#{termId}");
                if(StringUtils.isNotBlank(term.getpTermId()))
                    SET("pterm_id=#{pTermId}");
                if(StringUtils.isNotBlank(term.getState()))
                    SET("state=#{state}");
                if(term.getHasChildren()>=0)
                    SET("has_children=#{hasChildren}");
                if(StringUtils.isNotBlank(term.getLabel()))
                    SET("label=#{label}");
                if(StringUtils.isNotBlank(term.getTermCode()))
                    SET("term_code=#{termCode}");
                if(StringUtils.isNotBlank(term.getTermName()))
                    SET("term_name=#{termName}");
                if(StringUtils.isNotBlank(term.getTermType()))
                    SET("term_type=#{termType}");
                WHERE("id=#{id}");
            }
        }.toString();
    }

    public String insertTermSelective(final Term term){
        return new SQL(){
            {
                INSERT_INTO("term");
                if(StringUtils.isNotBlank(term.getTermId()))
                    VALUES("term_id","#{termId}");
                if(StringUtils.isNotBlank(term.getOriginName()))
                    VALUES("origin_name","#{originName}");
                if(StringUtils.isNotBlank(term.getLabel()))
                    VALUES("label","#{label}");
                if(StringUtils.isNotBlank(term.getTermType()))
                    VALUES("term_type","{termType}");
                if(StringUtils.isNotBlank(term.getTermName()))
                    VALUES("term_name","#{termName}");
                if(StringUtils.isNotBlank(term.getTermCode()))
                    VALUES("term_code","#{termCode}");
                if(StringUtils.isNotBlank(term.getConceptId()))
                    VALUES("concept_id","#{conceptId}");
                if(term.getHasChildren()>0)
                    VALUES("has_children","#{hasChildren}");
                if(StringUtils.isNotBlank(term.getState()))
                    VALUES("state","#{state}");
                if(StringUtils.isNotBlank(term.getpTermId()))
                    VALUES("pterm_id","#{pTermId}");
            }
        }.toString();
    }

    public String batchUpdateTermLabel(Map map){
        List<TermLabel> termLabelList=(List<TermLabel>)map.get("termLabelArr");
        String idStr=termLabelList.stream()
                .map(x->x.getId()).map(x->x.toString())
                .collect(Collectors.joining(","));
        StringBuilder sb=new StringBuilder();
        sb.append("update term set label ");
        sb.append("case id ");
        for(int i=0;i<termLabelList.size();i++){
            sb.append(" when "+termLabelList.get(i).getId());
            sb.append(" then "+termLabelList.get(i).getLabel());
        }
        sb.append("end ");
        sb.append("where id in ("+idStr+") ");
        return sb.toString();
    }

    public String batchUpdateTermConceptId(Map map){
        List<Integer> idArr=(List<Integer>)map.get("idArr");
        String conceptId=map.get("conceptId").toString();
        String idStr=idArr.stream().map(x->x.toString())
                .collect(Collectors.joining(","));
        StringBuilder sb=new StringBuilder();
        sb.append("update term set concept_id ");
        sb.append("case id ");
        for(int i=0;i<idArr.size();i++){
            sb.append(" when "+idArr.get(i));
            sb.append(" then "+conceptId);
        }
        sb.append(" end ");
        sb.append("where id in ("+idStr+")");
        return sb.toString();
    }

    public String batchCoverTermLabel(Map map){
        List<Integer> idArr=(List<Integer>)map.get("idArr");
        String label=map.get("label").toString();
        String idStr=idArr.stream().map(x->x.toString())
                .collect(Collectors.joining(","));
        StringBuilder sb=new StringBuilder();
        sb.append("update term set label ");
        sb.append("case id ");
        for(int i=0;i<idArr.size();i++){
            sb.append(" when "+idArr.get(i));
            sb.append(" then "+label);
        }
        sb.append(" end ");
        sb.append("where id in ("+idStr+")");
        return sb.toString();
    }


}
