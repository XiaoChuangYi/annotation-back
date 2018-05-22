package com.microservice.service.account;

import com.microservice.dataAccessLayer.entity.UserAccount;
import com.microservice.dataAccessLayer.mapper.UserAccountMapper;
import com.microservice.service.exercise.AnnotationWordPosExerciseService;
import com.microservice.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by cjl on 2018/4/16.
 */
@Service
public class UserAccountService {

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private AnnotationWordPosExerciseService annotationWordPosExerciseService;

    public List<UserAccount> listUserAccount(){
        return userAccountMapper.listUserAccount();
    }

    @Transactional
    public void addUserAccount(String accountName ,String password,String role){
        UserAccount userAccount=new UserAccount();
        userAccount.setAccountName(accountName);
        //对密码进行MD5加密
        userAccount.setPassword(MD5Util.getMD5String(password));
        userAccount.setState("enable");
        userAccount.setRole(role);
        Date currentDate=new Date();
        userAccount.setGmtCreated(currentDate);
        userAccount.setGmtModified(currentDate);
        userAccountMapper.insertUserAccountSelective(userAccount);
        if("练习人员".equals(role))
            annotationWordPosExerciseService.initUserExercises(userAccount.getId());
    }

    public void resetUserAccountPassword(String newPassword,int id){
        userAccountMapper.updateUserAccountPassword(MD5Util.getMD5String(newPassword),id);
    }

    public void setUserAccountState(String state,int id){
        userAccountMapper.updateUserAccountState(state,id);
    }

    public UserAccount getUserAccountById(int id){
        return userAccountMapper.getUserAccountById(id);
    }

    public  UserAccount getUserAccountByAccountName(String accountName){
        return userAccountMapper.getUserAccountByAccountName(accountName);
    }

}
