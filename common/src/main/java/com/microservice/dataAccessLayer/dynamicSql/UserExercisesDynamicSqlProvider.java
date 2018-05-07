package com.microservice.dataAccessLayer.dynamicSql;

import com.microservice.dataAccessLayer.entity.UserExercises;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by cjl on 2018/5/3.
 */
public class UserExercisesDynamicSqlProvider {

    public String queryUserExercisesAssociateAnnotation(final UserExercises userExercises){
        return new SQL(){
            {
                SELECT("ue.id,ue.origin_text,ue.practice_annotation,ue.state,ue.user_modifier,ue.gmt_created,ue.gmt_modified,ue.memo,ue.anId,ua.account_name,asent.standard_annotation");
                FROM("user_exercises ue ");
                INNER_JOIN("user_account ua on ue.user_modifier=ua.id ");
                INNER_JOIN("annotation_sentence_exercises asent on ue.anId=asent.id ");
                WHERE("1=1");
                if(StringUtils.isNotBlank(userExercises.getState()))
                    WHERE("ue.state=#{state}");
                if(userExercises.getUserModifier()>0)
                    WHERE("ue.user_modifier=#{userModifier}");
            }
        }.toString();
    }

    public String queryUserExercisesAnnotation(final UserExercises userExercises){
        return new SQL(){
            {
                SELECT("ue.*,ua.account_name");
                FROM("user_exercises ue");
                INNER_JOIN("user_account ua on ue.user_modifier=ua.id ");
                WHERE("1=1");
                if(StringUtils.isNotBlank(userExercises.getState()))
                    WHERE("ue.state=#{state}");
                if(userExercises.getUserModifier()>0)
                    WHERE("ue.user_modifier=#{userModifier}");
            }
        }.toString();
    }

    //根据唯一索引批量更新
    public String batchUpdateUserExerciseUser(Map map){
        List<UserExercises> userExercisesList=(List<UserExercises>)map.get("userExercisesList");
        StringBuilder sb=new StringBuilder();
        sb.append("update user_exercises set practice_annotation = case");
        for(UserExercises userExercises:userExercisesList){
            sb.append(" when (anId=" +userExercises.getAnId()+" and user_modifier= "+userExercises.getUserModifier()+") then '"+userExercises.getPracticeAnnotation()+"' ");
        }
        sb.append(" end,state = case ");
        for(UserExercises userExercises:userExercisesList){
            sb.append(" when (anId="+userExercises.getAnId()+" and user_modifier= "+userExercises.getUserModifier()+") then '"+userExercises.getState()+"' ");
        }
        String idStr=userExercisesList.stream().map(x->"(anId="+x.getAnId()+" and user_modifier="+x.getUserModifier()+")")
                .collect(Collectors.joining("or"));
        sb.append(" end where "+idStr);
        return sb.toString();
    }


    public String batchAddUserExercises(Map map){
        List<UserExercises> userExercisesList=(List<UserExercises>)map.get("list");
        StringBuilder sb=new StringBuilder();
        sb.append("insert into user_exercises ");
        sb.append("(origin_text,practice_annotation,state,user_modifier,gmt_created,gmt_modified,anId)");
        sb.append(" values ");
        MessageFormat mf=new MessageFormat("(#'{'list[{0}].originText'}',#'{'list[{0}].practiceAnnotation'}',#'{'list[{0}].state'}',#'{'list[{0}].userModifier'}',#'{'list[{0}].gmtCreated'}',#'{'list[{0}].gmtModified'}',#'{'list[{0}].anId'}')");
        for(int i=0;i<userExercisesList.size();i++){
            sb.append(mf.format(new Object[]{i}));
            if(i<userExercisesList.size()-1){
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public String updateUserExercisesSelective(final UserExercises userExercises){
        return new SQL(){
            {
                UPDATE("user_exercises");
                if(StringUtils.isNotBlank(userExercises.getMemo()))
                    SET("memo=#{memo}");
                if(StringUtils.isNotBlank(userExercises.getOriginText()))
                    SET("origin_text=#{originText}");
                if(userExercises.getPracticeAnnotation()!=null)
                    SET("practice_annotation=#{practiceAnnotation}");
                if(userExercises.getStandardAnnotation()!=null)
                    SET("standard_annotation=#{standardAnnotation}");
                if(StringUtils.isNotBlank(userExercises.getState()))
                    SET("state=#{state}");
                if(userExercises.getUserModifier()>0)
                    SET("user_modifier=#{userModifier}");
                if(userExercises.getGmtCreated()!=null)
                    SET("gmt_created=#{gmtCreated}");
                if(userExercises.getGmtModified()!=null)
                    SET("gmt_modified=#{gmtModified}");
                WHERE("id=#{id}");
            }
        }.toString();
    }
}
