package com.microservice.dataAccessLayer.dynamicSql;

import com.microservice.dataAccessLayer.entity.Type;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;
import sun.awt.SunHints;

/**
 * Created by cjl on 2018/4/11.
 */
public class TypeDynamicSqlProvider {

    public String queryTypeByCondition(final Type type){
        return new SQL(){
            {
                SELECT("*");
                FROM("an_type");
                WHERE("1=1");
                if(type.getTaskId()>=0)
                    WHERE("task_id=#{taskId}");

                if(StringUtils.isNotBlank(type.getId())){
                    WHERE("id=#{id}");
                }
                if(StringUtils.isNotBlank(type.getState())){
                    WHERE("state=#{state}");
                }
                if(StringUtils.isNotBlank(type.getTypeCode())){
                    WHERE("type_code=#{typeCode}");
                }
            }
        }.toString();
    }

    public String queryTypeAndShowParent(final Type type){
        return new SQL(){
            {
                SELECT("a.*,IFNULL(b.type_code,'') as parent_type ");
                FROM("an_type a ");
                LEFT_OUTER_JOIN("an_type b on a.parent_id=b.id ");
                WHERE("a.state='ENABLE' ");
                if(StringUtils.isNotBlank(type.getTypeName())){
                    WHERE("a.type_name like concat('%',#{typeName},'%')");
                }
                if(StringUtils.isNotBlank(type.getTypeCode())){
                    WHERE("a.type_code like concat('%',#{typeCode},'%')");
                }
                if(type.getTaskId()>=0)
                    WHERE("a.task_id=#{taskId}");

                ORDER_BY("a.id");
            }
        }.toString();
    }
    public String updateTypeSelectiveById(final Type type){
        return new SQL(){
            {
                UPDATE("an_type");
                if(StringUtils.isNotBlank(type.getParentId())){
                    SET("parent_id=#{parentId}");
                }
                if(StringUtils.isNotBlank(type.getTypeName())){
                    SET("type_name=#{typeName}");
                }
                if(StringUtils.isNotBlank(type.getState())){
                    SET("state=#{state}");
                }
                if(type.getGmtCreated()!=null){
                    SET("gmt_created=#{gmtCreated}");
                }
                if(type.getGmtModified()!=null){
                    SET("gmt_modified=#{gmtModified}");
                }
                if(StringUtils.isNotBlank(type.getTypeCode())){
                    SET("type_code=#{typeCode}");
                }
                if(type.getTaskId()>=0)
                    SET("task_id=#{taskId}");

                WHERE("id=#{id}");
            }
        }.toString();
    }
    public String insertTypeSelective(final Type type){
        return new SQL(){
            {
                INSERT_INTO("an_type");
                if(StringUtils.isNotBlank(type.getId()))
                    VALUES("id","#{id}");
                if(StringUtils.isNotBlank(type.getState()))
                    VALUES("state","#{state}");
                if(StringUtils.isNotBlank(type.getTypeName()))
                    VALUES("type_name","#{typeName}");
                if(StringUtils.isNotBlank(type.getParentId()))
                    VALUES("parent_id","#{parentId}");
                if(StringUtils.isNotBlank(type.getTypeCode()))
                    VALUES("type_code","#{typeCode}");
                if(type.getTaskId()>=0)
                    VALUES("task_id","#{taskId}");
                if(type.getHasChildren()>=0)
                    VALUES("has_children","#{hasChildren}");
                if(type.getGmtCreated()!=null)
                    VALUES("gmt_created","#{gmtCreated}");
                if(type.getGmtModified()!=null)
                    VALUES("gmt_modified","#{gmtModified}");
            }
        }.toString();
    }
}
