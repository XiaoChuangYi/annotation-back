package com.microservice.service.exercise;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.microservice.dataAccessLayer.entity.AnnotationWordPosExercise;
import com.microservice.dataAccessLayer.entity.UserWordExercise;
import com.microservice.dataAccessLayer.mapper.AnnotationWordPosExerciseMapper;
import com.microservice.dataAccessLayer.mapper.UserWordExerciseMapper;
import com.microservice.enums.AnnotationWordPosExerciseStateEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by cjl on 2018/5/17.
 */
@Service
public class UserWordExerciseService {

    @Autowired
    private UserWordExerciseMapper userWordExerciseMapper;

    @Autowired
    private AnnotationWordPosExerciseMapper annotationWordPosExerciseMapper;

    //关联习题集标准答案
    public Page<UserWordExercise> listUserWordExerciseAssociatePaging(int pageIndex, int pageSize, int userModifier, String state){
        Page<UserWordExercise> pageInfo= PageHelper.startPage(pageIndex,pageSize);
        UserWordExercise paramUserExercises=new UserWordExercise();
        paramUserExercises.setState(state);
        paramUserExercises.setUserModifier(userModifier);
        userWordExerciseMapper.queryUserExerciseAssociateAnnotation(paramUserExercises);
        return pageInfo;
    }

    public void commitUserWordExercise(int id,int anId){
        AnnotationWordPosExercise annotationSentenceExercise=annotationWordPosExerciseMapper.getAnnotationWordPosExerciseById(anId);
        UserWordExercise userExercises=new UserWordExercise();
        userExercises.setStandardAnnotation(annotationSentenceExercise.getStandardAnnotation());
        userExercises.setGmtModified(new Date());
        userExercises.setId(id);
        userExercises.setState(AnnotationWordPosExerciseStateEnum.FINISH.name());
        userWordExerciseMapper.updateUserWordExerciseSelective(userExercises);
    }

    public UserWordExercise getUserWordExerciseById(int id){
        return userWordExerciseMapper.getUserWordExerciseById(id);
    }

    public void updateUserWordExerciseSelective(UserWordExercise userWordExercise){
        userWordExerciseMapper.updateUserWordExerciseSelective(userWordExercise);
    }

}
