package com.microservice.dataAccessLayer.mapper;

import com.microservice.dataAccessLayer.entity.Account;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.type.JdbcType;

/**
 * Created by cjl on 2018/4/11.
 */
public interface AccountMapper {

     @Select("select * from crm_account where account_no=#{accountNo}")
     @Results({
             @Result(id=true,column = "id",property = "id",jdbcType = JdbcType.VARCHAR),
             @Result(column = "account_no",property = "accountNo"),
             @Result(column = "login_pwd",property = "loginPwd"),
             @Result(column = "state",property = "state"),
             @Result(column = "gmt_created",property = "gmtCreated"),
             @Result(column = "gmt_modified",property = "gmtModified")
     })
     Account selectByAccountNo(@Param("accountNo") String accountNo);
}
