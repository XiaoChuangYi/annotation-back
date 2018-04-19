package com.microservice.dataAccessLayer.dynamicSql;

import com.microservice.dataAccessLayer.entity.Draw;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

/**
 * Created by cjl on 2018/4/2.
 */
public class DrawDynamicSqlProvider {

    public String selectDrawByCondition(final Draw draw){
        return new SQL(){
            {
                SELECT("d.id,type_code,IFNULL(draw_name,''),type_label");
                FROM("draw d");
                INNER_JOIN("an_type a on d.id=a.id ");
                WHERE("1=1");
                        if(StringUtils.isNotBlank(draw.getTypeCode())){
                            WHERE("type_code=#{typeCode}");
                        }
                        if(StringUtils.isNotBlank(draw.getDrawName())){
                            WHERE("draw_name concat('%',#{drawName},'%')");
                        }

            }
        }.toString();
    }

    public String updateDrawBySelective(final Draw draw){
        return new SQL(){
            {
                UPDATE("draw");
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

    public String insertDrawSelective(final Draw draw){
        return new SQL(){
            {
                INSERT_INTO("draw");
                if(StringUtils.isNotBlank(draw.getDrawName())){
                    VALUES("draw_name","#{drawName}");
                }
                if(StringUtils.isNotBlank(draw.getTypeLabel())){
                    VALUES("type_label","#{typeLabel}");
                }
            }
        }.toString();
    }
}
