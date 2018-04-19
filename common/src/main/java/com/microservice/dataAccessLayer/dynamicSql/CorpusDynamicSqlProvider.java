package com.microservice.dataAccessLayer.dynamicSql;

import com.microservice.dataAccessLayer.entity.Corpus;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by cjl on 2018/4/11.
 */
public class CorpusDynamicSqlProvider {

    public String queryCorpusByCondition(final Corpus corpus){
        return new SQL(){
            {
                SELECT("*");
                FROM("corpus");
                WHERE("1=1");
                if(StringUtils.isNotBlank(corpus.getState())){
                    WHERE("state=#{state}");
                }
                if(StringUtils.isNotBlank(corpus.getType())){
                    WHERE("type=#{type}");
                }
            }
        }.toString();
    }

    public String batchUpdateCorpusByIdArr(Map map){
        List<String> idArr=(List<String>)map.get("idArr");
        String type=map.get("type").toString();
        String idStr=idArr.stream().map(x->x.toString()).collect(Collectors.joining(","));
        return new SQL(){
            {
                UPDATE("corpus");
                SET("type='"+type+"'");
                WHERE("id in ("+idStr+")");
            }
        }.toString();
    }

}
