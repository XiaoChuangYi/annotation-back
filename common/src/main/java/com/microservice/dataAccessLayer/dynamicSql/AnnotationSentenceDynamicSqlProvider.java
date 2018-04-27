package com.microservice.dataAccessLayer.dynamicSql;

import com.microservice.dataAccessLayer.entity.AnnotationSentence;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by cjl on 2018/4/16.
 */
public class AnnotationSentenceDynamicSqlProvider {

    public String queryAutoDistributionAnnotation(Map map){
//        AnnotationSentence annotationSentence=(AnnotationSentence)map.get("annotationSentence");
        List<String> stateList=(List<String>)map.get("stateList");
        String stateStr=stateList.stream().map(x->"'"+x.toString()+"'").collect(Collectors.joining(","));
        return new SQL(){
            {
                SELECT("*");
                FROM("annotation_sentence");
                WHERE("1=1");
                //如果当前的用户是admin,仅仅做state过滤
//                if (StringUtils.isNotBlank(annotationSentence.getUserModifier())) {
//                        WHERE("user_modifier !='" + annotationSentence.getUserModifier() + "'");
//                }
                if(StringUtils.isNotBlank(stateStr)&&!"''".equals(stateStr)){
                    WHERE("state in ("+stateStr+")");
                }
            }
        }.toString();
    }


    public String queryAnnotationSentenceSelective(Map map){
        AnnotationSentence annotationSentence=(AnnotationSentence)map.get("annotationSentence");
        List<String> stateList=(List<String>)map.get("stateList");
        String stateStr=stateList.stream().map(x->"'"+x.toString()+"'").collect(Collectors.joining(","));
        return new SQL(){
            {
                SELECT("*");
                FROM("annotation_sentence");
                WHERE("1=1");
                //如果当前的用户是admin,仅仅做state过滤
                if(!annotationSentence.getUserModifier().equals("1")) {
                    if (StringUtils.isNotBlank(annotationSentence.getUserModifier())) {
                        WHERE("user_modifier='" + annotationSentence.getUserModifier() + "'");
                    }
                }
                if(StringUtils.isNotBlank(stateStr)&&!"''".equals(stateStr)){
                    WHERE("state in ("+stateStr+")");
                }
            }
        }.toString();
    }

    public String updateAnnotationSentenceSelective(final AnnotationSentence annotationSentence){
        return new SQL(){
            {
                UPDATE("annotation_sentence");
                if(StringUtils.isNotBlank(annotationSentence.getState())){
                    SET("state=#{state}");
                }
                if(StringUtils.isNotBlank(annotationSentence.getUserModifier())){
                    SET("user_modifier=#{userModifier}");
                }
                if(annotationSentence.getAnnotationText()!=null){
                    SET("annotation_text=#{annotationText}");
                }
                if(annotationSentence.getFinalAnnotationText()!=null)
                    SET("final_annotation_text=#{finalAnnotationText}");

                if(StringUtils.isNotBlank(annotationSentence.getMemo())){
                    SET("memo=#{memo}");
                }
                if(annotationSentence.getGmtCreated()!=null){
                    SET("gmt_created=#{gmtCreated}");
                }
                if(annotationSentence.getGmtModified()!=null){
                    SET("gmt_modified=#{gmtModified}");
                }
                WHERE("id=#{id}");
            }
        }.toString();
    }

    public String updateAnnotationSentenceUserModifierByIdArr(Map map){
        List<Integer> idArr=(List<Integer>)map.get("idArr");
        int userModifier=Integer.valueOf(map.get("userModifier").toString());
        String state=map.get("state").toString();
        String idStr=idArr.stream().map(x->x.toString())
                .collect(Collectors.joining(","));
        StringBuilder sb=new StringBuilder();
        sb.append("update annotation_sentence set user_modifier="+userModifier);
        sb.append(" ,state='"+state+"' ");
        sb.append(" where id in ("+idStr+")");
        return sb.toString();
    }

    public String batchUpdateAnnotationSentenceStateByIdArr(Map map){
        List<Integer> idArr=(List<Integer>)map.get("idArr");
        String state=map.get("state").toString();
        String idStr=idArr.stream().map(x->x.toString())
                .collect(Collectors.joining(","));
        StringBuilder sb=new StringBuilder();
        sb.append("update annotation_sentence set state='"+state+"'");
        sb.append(" where id in ("+idStr+")");
        return sb.toString();
    }
}
