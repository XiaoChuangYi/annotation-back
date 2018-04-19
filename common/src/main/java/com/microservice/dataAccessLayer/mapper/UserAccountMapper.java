package com.microservice.dataAccessLayer.mapper;

import com.microservice.dataAccessLayer.dynamicSql.UserAccountDynamicSqlProvider;
import com.microservice.dataAccessLayer.entity.UserAccount;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by cjl on 2018/4/16.
 */
public interface UserAccountMapper {

    @Select("select * from user_account")
    @Results({
            @Result(id=true,column = "id",property ="id"),
            @Result(property = "accountName",column = "account_name"),
            @Result(property = "password",column = "password"),
            @Result(property = "role",column = "role"),
            @Result(property = "state",column = "state"),
            @Result(property = "gmtCreated",column = "gmt_created"),
            @Result(property = "gmtModified",column = "gmt_modified")
    })
    List<UserAccount> listUserAccount();

    @Select("select * from user_account where id=#{id}")
    @Results({
            @Result(id=true,column = "id",property ="id"),
            @Result(property = "accountName",column = "account_name"),
            @Result(property = "password",column = "password"),
            @Result(property = "role",column = "role"),
            @Result(property = "state",column = "state"),
            @Result(property = "gmtCreated",column = "gmt_created"),
            @Result(property = "gmtModified",column = "gmt_modified")
    })
    UserAccount getUserAccountById(@Param("id") int id);

    @Select("select * from user_account where account_name=#{accountName}")
    @Results({
            @Result(id=true,column = "id",property ="id"),
            @Result(property = "accountName",column = "account_name"),
            @Result(property = "password",column = "password"),
            @Result(property = "role",column = "role"),
            @Result(property = "state",column = "state"),
            @Result(property = "gmtCreated",column = "gmt_created"),
            @Result(property = "gmtModified",column = "gmt_modified")
    })
    UserAccount getUserAccountByAccountName(@Param("accountName") String accountName);

    @Update("update user_account set password=#{password} where id=#{id}")
    void updateUserAccountPassword(@Param("password") String password,@Param("id") int id);

    @Update("update user_account set state=#{state} where id=#{id}")
    void updateUserAccountState(@Param("state") String state,@Param("id") int id);

    @InsertProvider(type = UserAccountDynamicSqlProvider.class,method="insertUserAccountSelective")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    void insertUserAccountSelective(UserAccount userAccount);
}
