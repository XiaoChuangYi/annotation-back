package com.microservice.dataAccessLayer.dynamicSql;

import org.apache.ibatis.jdbc.SQL;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * Created by cjl on 2018/4/11.
 */
public class TagDynamicSqlProvider {

    public String batchInsertTag(Map map){
        List<String> tagList=(List<String>)map.get("listTag");
        StringBuilder sb=new StringBuilder();
        sb.append("insert into tag ");
        sb.append("(tag_name)");
        sb.append("values ");
        MessageFormat mf=new MessageFormat("(#'{'list[{0}].tagName'}')");
        for(int i=0;i<tagList.size();i++){
            sb.append(mf.format(new Object[]{i}));
            if(i<tagList.size()-1){
                sb.append(",");
            }
        }
        return sb.toString();
    }
}
