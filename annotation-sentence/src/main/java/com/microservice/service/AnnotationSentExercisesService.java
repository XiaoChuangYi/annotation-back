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
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by cjl on 2018/5/3.
 */
@Service
public class AnnotationSentExercisesService {

    @Autowired
    private AnnotationSentenceExerciseMapper  annotationSentenceExerciseMapper;


    @Autowired
    private UserExercisesMapper userExercisesMapper;

    /**
     * 查询标准练习数据
     */
    public Page<AnnotationSentenceExercise> listAnnotationSentExercise(int pageIndex,int pageSize){
        Page<AnnotationSentenceExercise> pageInfo= PageHelper.startPage(pageIndex,pageSize);
        annotationSentenceExerciseMapper.listAnnotationSentExerciseByCondition(new AnnotationSentenceExercise());
        return pageInfo;
    }

    /**
     * 根据id查询annotation
     */

    public  AnnotationSentenceExercise getAnnotationSentExerciseById(int id){
        return annotationSentenceExerciseMapper.getAnnotationSentExerciseById(id);
    }

    /**
     * 初始化user_exercises表
     */
    public void initUserExercises(int userModifier){
        int sum=userExercisesMapper.countUserExercises(userModifier);
        if(sum==0){
            //新增
            List<AnnotationSentenceExercise> annotationSentenceExerciseList=annotationSentenceExerciseMapper.listAnnotationSentExerciseAll();
                List<UserExercises> userExercisesList=convert2UserExercises(annotationSentenceExerciseList,AnnotationSentExercisesStateEnum.INIT.name(),userModifier);
                userExercisesMapper.batchAddUserExercises(userExercisesList);
        }
    }


    public void resetUserExercisesByUserModifier(int userModifier){
        List<UserExercises> userExercisesList=userExercisesMapper.listUserExercisesByUserModifier(userModifier);
//        List<Integer> anIdList=userExercisesList.stream().map(x->x.getAnId()).collect(Collectors.toList());
        String idTemp=userExercisesList.stream().map(x->"'"+x.getAnId()+"'").collect(Collectors.joining(","));
        List<AnnotationSentenceExercise> annotationSentenceExerciseList =annotationSentenceExerciseMapper.listAnnotationSentExerciseByIdArr(idTemp);
        userExercisesList=convert2UserExercises(annotationSentenceExerciseList,AnnotationSentExercisesStateEnum.INIT.name(),userModifier);
        //该方法可能有坑
        userExercisesMapper.batchUpdateUserExerciseUser(userExercisesList);

    }

    /**
     * 指派标准标注数据，如果根据当前用户查询不到其所指派的数据，则新增所有标注数据到user_exercises表,否则批量更新对应的
     */
    public void designateAnnotationSentExercises(List<Integer> anIdArr,int userModifier,String state){
            String idTemp=anIdArr.stream().map(x->x.toString()).collect(Collectors.joining(","));
            List<AnnotationSentenceExercise> annotationSentenceExerciseList =annotationSentenceExerciseMapper.listAnnotationSentExerciseByIdArr(idTemp);
            List<UserExercises> userExercisesList=convert2UserExercises(annotationSentenceExerciseList,state,userModifier);
            userExercisesMapper.batchUpdateUserExerciseUser(userExercisesList);
    }



    private List<UserExercises> convert2UserExercises(List<AnnotationSentenceExercise> annotationSentenceExerciseList,String state,int userModifier){
        List<UserExercises> userExercisesList=new LinkedList<>();
        if(annotationSentenceExerciseList.size()>0) {
            for (AnnotationSentenceExercise current : annotationSentenceExerciseList) {
                UserExercises userExercises = new UserExercises();
                Date currentDate = new Date();
                userExercises.setOriginText(current.getOriginText());
                userExercises.setState(state);
                userExercises.setPracticeAnnotation(current.getAutoAnnotation());
                userExercises.setUserModifier(userModifier);
                userExercises.setAnId(current.getId());
                userExercises.setGmtCreated(currentDate);
                userExercises.setGmtModified(currentDate);
                userExercisesList.add(userExercises);
            }
        }
        return userExercisesList;
    }

}
