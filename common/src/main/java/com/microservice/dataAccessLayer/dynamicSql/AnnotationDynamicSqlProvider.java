package com.microservice.dataAccessLayer.dynamicSql;

import com.microservice.dataAccessLayer.entity.Annotation;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by cjl on 2018/3/30.
 */
public class AnnotationDynamicSqlProvider {

    public String queryAnnotationByCondition(Map map){
        Annotation annotation=(Annotation) map.get("annotation");
        String sort=map.get("sort").toString();
        return new SQL(){
            {
                SELECT("*");
                        FROM("annotation");
                        WHERE(" 1=1 ");
                        if(StringUtils.isNotBlank(annotation.getModifier())){
                            WHERE("modifier='"+annotation.getModifier()+"'");
                        }
                        if(StringUtils.isNotBlank(annotation.getState())){
                            WHERE("state='"+annotation.getState()+"'");
                        }
                        if(StringUtils.isNotBlank(annotation.getTermId())){
                            WHERE("term_id='"+annotation.getTermId()+"'");
                        }
                        if(StringUtils.isNotBlank(annotation.getTerm())){
                            WHERE("term like '%"+annotation.getTerm()+"%'");
                        }
                        ORDER_BY(sort);
            }
        }.toString();
    }
    public String queryAnnotationByStateList(Map map){
        List<String> stateList=(List<String>)map.get("list");
        String sort=map.get("sort").toString();
        String stateTemp=stateList.stream().map(x->x.toUpperCase()).collect(Collectors.joining(","));
        return new SQL(){
            {
                SELECT("*");
                FROM("annotation");
                WHERE("1=1");
                if(StringUtils.isNotBlank(stateTemp)){
                    WHERE("state in ("+stateTemp+")");
                }
                ORDER_BY(sort);
            }
        }.toString();
    }

    public String queryAnnotationByIdArr(Map map){
        List<String> idArr=(List<String>)map.get("idArr");
        String idStr=idArr.stream().map(x->x.toString()).collect(Collectors.joining(","));
        return new SQL(){
            {
                SELECT("*");
                FROM("annotation");
                WHERE("1=1");
                if(idArr.size()>0)
                    WHERE("id in ("+idStr+")");
            }
        }.toString();
    }

    public String countAnnotationSizeByState(Map map){
        String state=map.get("state").toString();
        return new SQL(){
            {
                SELECT("count(id)");
                FROM("annotation");
                WHERE("1=1");
                if(StringUtils.isNotBlank(state))
                    WHERE("state='"+state+"'");
            }
        }.toString();
    }

    public String batchUpdateAnnotation(Map map){
        List<Annotation> annotationList=(List<Annotation>)map.get("list");
        StringBuilder sb=new StringBuilder();
        sb.append("update annotation set final_annotation case id ");
        for(Annotation annotation:annotationList){
            sb.append("when " +annotation.getId()+" then "+annotation.getFinalAnnotation());
        }
        sb.append(" end,manual_annotation");
        for(Annotation annotation:annotationList){
            sb.append("when "+annotation.getId()+" then "+annotation.getManualAnnotation());
        }
        String idTemp=annotationList.stream().map(x->x.getId()).collect(Collectors.joining(","));
        sb.append(" end where id in ("+idTemp+")");
        return sb.toString();
    }
    public String batchUpdateAnnotationModifier(Map map){
        List<Integer> integerList=(List<Integer>)map.get("list");
        String modifier=map.get("modifier").toString();
        StringBuilder sb=new StringBuilder();
        sb.append("update annotation set modifier="+modifier);
        String idTemp=integerList.stream().map(x->x.toString()).collect(Collectors.joining(","));
        sb.append("where id in ("+idTemp+")");
        return sb.toString();
    }

    public String updateAnnotationSelective(final Annotation annotation){
        return new SQL(){
            {
                UPDATE("annotation");
                if(StringUtils.isNotBlank(annotation.getTermId())){
                    SET("term_id=#{termId}");
                }
                if(StringUtils.isNotBlank(annotation.getTerm())){
                    SET("term=#{term}");
                }
                if(StringUtils.isNotBlank(annotation.getState())){
                    SET("state=#{state}");
                }
                if(StringUtils.isNotBlank(annotation.getAutoAnnotation())){
                    SET("auto_annotation=#{autoAnnotation}");
                }
                if(StringUtils.isNotBlank(annotation.getFinalAnnotation())){
                    SET("final_annotation=#{finalAnnotation}");
                }
                if(StringUtils.isNotBlank(annotation.getManualAnnotation())){
                    SET("manual_annotation=#{manualAnnotation}");
                }
                if(StringUtils.isNotBlank(annotation.getMemo())){
                    SET("memo=#{memo}");
                }
                if(StringUtils.isNotBlank(annotation.getModifier())){
                    SET("modifier=#{modifier}");
                }
                if(StringUtils.isNotBlank(annotation.getNewTerms())){
                    SET("new_terms=#{newTerms}");
                }
                if(annotation.getGmtCreated()!=null){
                    SET("gmt_created=#{gmtCreated}");
                }
                if(annotation.getGmtModified()!=null){
                    SET("gmt_modified=#{gmtModified}");
                }
                WHERE("id=#{id}");
            }
        }.toString();
    }
}
