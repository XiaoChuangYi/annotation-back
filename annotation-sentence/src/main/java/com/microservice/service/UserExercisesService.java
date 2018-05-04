package com.microservice.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.microservice.dataAccessLayer.entity.AnnotationSentenceExercise;
import com.microservice.dataAccessLayer.entity.UserExercises;
import com.microservice.dataAccessLayer.mapper.AnnotationSentenceExerciseMapper;
import com.microservice.dataAccessLayer.mapper.UserExercisesMapper;
import com.microservice.enums.AnnotationSentExercisesStateEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by cjl on 2018/5/3.
 */
@Service
public class UserExercisesService {

    @Autowired
    private UserExercisesMapper userExercisesMapper;

    @Autowired
    private  AnnotationSentenceExerciseMapper annotationSentenceExerciseMapper;

    public Page<UserExercises> listUserExercisesPaging(int pageIndex,int pageSize,int userModifier,String state){
        Page<UserExercises> pageInfo= PageHelper.startPage(pageIndex,pageSize);
        UserExercises paramUserExercises=new UserExercises();
        paramUserExercises.setState(state);
        paramUserExercises.setUserModifier(userModifier);
        userExercisesMapper.queryUserExercisesAnnotation(paramUserExercises);
        return pageInfo;
    }

    public void commitAnnotationSentExercises(int id,int anId){
        AnnotationSentenceExercise annotationSentenceExercise=annotationSentenceExerciseMapper.getAnnotationSentExerciseById(anId);
        UserExercises userExercises=new UserExercises();
        userExercises.setStandardAnnotation(annotationSentenceExercise.getStandardAnnotation());
        userExercises.setGmtModified(new Date());
        userExercises.setId(id);
        userExercises.setState(AnnotationSentExercisesStateEnum.FINISH.name());
        userExercisesMapper.updateUserExercisesSelective(userExercises);
    }

    public UserExercises getUserExercisesById(int id){
        return userExercisesMapper.getUserExercisesById(id);
    }

    public void updateUserExercisesSelective(UserExercises userExercises){
        userExercisesMapper.updateUserExercisesSelective(userExercises);
    }
}
