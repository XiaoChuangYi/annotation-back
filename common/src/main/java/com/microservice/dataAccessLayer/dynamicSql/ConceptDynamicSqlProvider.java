package com.microservice.dataAccessLayer.dynamicSql;

import com.microservice.dataAccessLayer.entity.Concept;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

/**
 * Created by cjl on 2018/4/11.
 */
public class ConceptDynamicSqlProvider {

    public String queryConceptByCondition(final Concept concept){
        return new SQL(){
            {
                SELECT("*");
                FROM("concept");
                WHERE("1=1");
                if(StringUtils.isNotBlank(concept.getStandardName())){
                    WHERE("standard_name like concat('%',#{standardName},'%')");
                }
                if(StringUtils.isNotBlank(concept.getConceptId())){
                    WHERE("concept_id=#{conceptId}");
                }
            }
        }.toString();
    }

    public String updateConceptSelective(final Concept concept){
        return new SQL(){
            {
                UPDATE("concept");
                if(StringUtils.isNotBlank(concept.getStandardName())){
                    SET("standard_name=#{standardName}");
                }
                if(StringUtils.isNotBlank(concept.getConceptId())){
                    SET("concept_id=#{conceptId}");
                }
                WHERE("id=#{id}");
            }
        }.toString();
    }

    public String insertConceptSelective(final Concept concept){
        return new SQL(){
            {
                INSERT_INTO("concept");
                if(StringUtils.isNotBlank(concept.getConceptId())){
                    VALUES("concept_id","#{conceptId}");
                }
                if(StringUtils.isNotBlank(concept.getStandardName())){
                    VALUES("standard_name","#{standardName}");
                }
            }
        }.toString();
    }
}
