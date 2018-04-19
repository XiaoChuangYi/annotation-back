package com.microservice.dataAccessLayer.dynamicSql;

import com.microservice.dataAccessLayer.entity.AnnotationNegation;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by cjl on 2018/4/16.
 */
public class AnnotationNegationDynamicSqlProvider {

    public String queryAnnotationNegationSelective(Map map){
        AnnotationNegation annotationNegation=(AnnotationNegation)map.get("annotationNegation");
        List<String> stateList=(List<String>)map.get("stateList");
        String stateStr=stateList.stream().map(x->"'"+x.toString()+"'").collect(Collectors.joining(","));
        return new SQL(){
            {
                SELECT("*");
                FROM("annotation_negation");
                WHERE("1=1");
                //如果当前的用户是admin,仅仅做state过滤
                if(!annotationNegation.getUserModifier().equals("1")) {
                    if (StringUtils.isNotBlank(annotationNegation.getUserModifier())) {
                        WHERE("user_modifier='" + annotationNegation.getUserModifier() + "'");
                    }
                }
                if(StringUtils.isNotBlank(stateStr)&&!"''".equals(stateStr)){
                    WHERE("state in ("+stateStr+")");
                }
            }
        }.toString();
    }

    public String updateAnnotationNegationSelective(final AnnotationNegation annotationNegation){
        return new SQL(){
            {
                UPDATE("annotation_negation");
                if(StringUtils.isNotBlank(annotationNegation.getState())){
                    SET("state=#{state}");
                }
                if(StringUtils.isNotBlank(annotationNegation.getUserModifier())){
                    SET("user_modifier=#{userModifier}");
                }
                if(annotationNegation.getAnnotationText()!=null){
                    SET("annotation_text=#{annotationText}");
                }
                if(StringUtils.isNotBlank(annotationNegation.getMemo())){
                    SET("memo=#{memo}");
                }
                if(annotationNegation.getGmtCreated()!=null){
                    SET("gmt_created=#{gmtCreated}");
                }
                if(annotationNegation.getGmtModified()!=null){
                    SET("gmt_modified=#{gmtModified}");
                }
                WHERE("id=#{id}");
            }
        }.toString();
    }

    public String updateAnnotationNegationUserModifierByIdArr(Map map){
        List<Integer> idArr=(List<Integer>)map.get("idArr");
        int userModifier=Integer.valueOf(map.get("userModifier").toString());
        String state=map.get("state").toString();
        String idStr=idArr.stream().map(x->x.toString())
                .collect(Collectors.joining(","));
        StringBuilder sb=new StringBuilder();
        sb.append("update annotation_negation set user_modifier="+userModifier);
        sb.append(" ,state='"+state+"' ");
        sb.append(" where id in ("+idStr+")");
        return sb.toString();
    }

    public String batchUpdateAnnotationNegationStateByIdArr(Map map){
        List<Integer> idArr=(List<Integer>)map.get("idArr");
        String state=map.get("state").toString();
        String idStr=idArr.stream().map(x->x.toString())
                .collect(Collectors.joining(","));
        StringBuilder sb=new StringBuilder();
        sb.append("update annotation_negation set state='"+state+"'");
        sb.append(" where id in ("+idStr+")");
        return sb.toString();
    }
}
