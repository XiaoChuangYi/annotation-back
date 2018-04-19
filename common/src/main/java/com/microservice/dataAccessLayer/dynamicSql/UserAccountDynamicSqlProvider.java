package com.microservice.dataAccessLayer.dynamicSql;

import com.microservice.dataAccessLayer.entity.UserAccount;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

/**
 * Created by cjl on 2018/4/16.
 */
public class UserAccountDynamicSqlProvider {

    public String insertUserAccountSelective(final UserAccount userAccount){
        return new SQL(){
            {
                INSERT_INTO("user_account");
                if(StringUtils.isNotBlank(userAccount.getAccountName())){
                    VALUES("account_name","#{accountName}");
                }
                if(StringUtils.isNotBlank(userAccount.getPassword())){
                    VALUES("password","#{password}");
                }
                if(StringUtils.isNotBlank(userAccount.getRole())){
                    VALUES("role","#{role}");
                }
                if(StringUtils.isNotBlank(userAccount.getState())){
                    VALUES("state","#{state}");
                }
                if(userAccount.getGmtCreated()!=null){
                    VALUES("gmt_created","#{gmtCreated}");
                }
                if(userAccount.getGmtModified()!=null){
                    VALUES("gmt_modified","#{gmtModified}");
                }
            }
        }.toString();
    }
}
