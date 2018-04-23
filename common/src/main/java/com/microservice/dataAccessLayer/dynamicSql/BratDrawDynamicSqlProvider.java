package com.microservice.dataAccessLayer.dynamicSql;

import com.microservice.dataAccessLayer.entity.BratDraw;
import com.microservice.dataAccessLayer.entity.Draw;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

/**
 * Created by cjl on 2018/4/2.
 */
public class BratDrawDynamicSqlProvider {

    public String selectDrawByCondition(final BratDraw draw){
        return new SQL(){
            {
                SELECT("d.id,type_code,IFNULL(draw_name,''),type_label");
                FROM("brat_draw d");
                INNER_JOIN("an_type a on d.type_id=a.id ");
                WHERE("1=1");
                if(StringUtils.isNotBlank(draw.getTypeCode())){
                    WHERE("type_code=#{typeCode}");
                }
                if(StringUtils.isNotBlank(draw.getDrawName())){
                    WHERE("draw_name concat('%',#{drawName},'%')");
                }
                if(draw.getTaskId()>=0)
                    WHERE("d.task_id=#{taskId}");

            }
        }.toString();
    }

    public String updateDrawBySelective(final BratDraw draw){
        return new SQL(){
            {
                UPDATE("brat_draw");
                if(StringUtils.isNotBlank(draw.getTypeLabel())){
                    SET("type_label=#{typeLabel}");
                }
                if(StringUtils.isNotBlank(draw.getDrawName())){
                    SET("draw_name=#{drawName}");
                }
                WHERE("id=#{id}");
            }
        }.toString();
    }

    public String insertDrawSelective(final BratDraw draw){
        return new SQL(){
            {
                INSERT_INTO("brat_draw");
                if(StringUtils.isNotBlank(draw.getDrawName())){
                    VALUES("draw_name","#{drawName}");
                }
                if(StringUtils.isNotBlank(draw.getTypeLabel())){
                    VALUES("type_label","#{typeLabel}");
                }
                if(draw.getTypeId()>=0)
                    VALUES("type_id","#{typeId}");
                if(draw.getTaskId()>=0)
                    VALUES("task_id","#{taskId}");
            }
        }.toString();
    }
}
