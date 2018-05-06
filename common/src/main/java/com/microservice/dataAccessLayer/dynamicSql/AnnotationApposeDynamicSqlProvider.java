package com.microservice.dataAccessLayer.dynamicSql;

import com.microservice.dataAccessLayer.entity.AnnotationAppose;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by cjl on 2018/4/16.
 */
public class AnnotationApposeDynamicSqlProvider {

    public String queryAnnotationParallelSelective(Map map){
        AnnotationAppose annotationParallel=(AnnotationAppose)map.get("annotationParallel");
        List<String> stateList=(List<String>)map.get("stateList");
        String stateStr=stateList.stream().map(x->"'"+x.toString()+"'").collect(Collectors.joining(","));
        return new SQL(){
            {
                SELECT("*");
                FROM("annotation_appose");
                WHERE("1=1");
                //如果当前的用户是admin,仅仅做state过滤
                if(!annotationParallel.getUserModifier().equals("1")) {
                    if (StringUtils.isNotBlank(annotationParallel.getUserModifier())) {
                        WHERE("user_modifier='" + annotationParallel.getUserModifier() + "'");
                    }
                }
                if(StringUtils.isNotBlank(stateStr)&&!"''".equals(stateStr)){
                    WHERE("state in ("+stateStr+")");
                }
            }
        }.toString();
    }

    public String updateAnnotationParallelSelective(final AnnotationAppose annotationParallel){
        return new SQL(){
            {
                UPDATE("annotation_appose");
                if(StringUtils.isNotBlank(annotationParallel.getState())){
                    SET("state=#{state}");
                }
                if(StringUtils.isNotBlank(annotationParallel.getUserModifier())){
                    SET("user_modifier=#{userModifier}");
                }
                if(annotationParallel.getAnnotationText()!=null){
                    SET("annotation_text=#{annotationText}");
                }
                if(StringUtils.isNotBlank(annotationParallel.getMemo())){
                    SET("memo=#{memo}");
                }
                if(annotationParallel.getGmtCreated()!=null){
                    SET("gmt_created=#{gmtCreated}");
                }
                if(annotationParallel.getGmtModified()!=null){
                    SET("gmt_modified=#{gmtModified}");
                }
                WHERE("id=#{id}");
            }
        }.toString();
    }

    public String updateAnnotationParallelUserModifierByIdArr(Map map){
        List<Integer> idArr=(List<Integer>)map.get("idArr");
        int userModifier=Integer.valueOf(map.get("userModifier").toString());
        String state=map.get("state").toString();
        String idStr=idArr.stream().map(x->x.toString())
                .collect(Collectors.joining(","));
        StringBuilder sb=new StringBuilder();
        sb.append("update annotation_appose set user_modifier="+userModifier);
        sb.append(" ,state='"+state+"' ");
        sb.append(" where id in ("+idStr+")");
        return sb.toString();
    }

    public String batchUpdateAnnotationParallelStateByIdArr(Map map){
        List<Integer> idArr=(List<Integer>)map.get("idArr");
        String state=map.get("state").toString();
        String idStr=idArr.stream().map(x->x.toString())
                .collect(Collectors.joining(","));
        StringBuilder sb=new StringBuilder();
        sb.append("update annotation_appose set state='"+state+"'");
        sb.append(" where id in ("+idStr+")");
        return sb.toString();
    }
}
