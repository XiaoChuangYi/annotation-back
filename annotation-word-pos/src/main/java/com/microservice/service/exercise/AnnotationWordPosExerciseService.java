package com.microservice.service.exercise;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.microservice.dataAccessLayer.entity.AnnotationSentenceExercise;
import com.microservice.dataAccessLayer.entity.AnnotationWordPosExercise;
import com.microservice.dataAccessLayer.entity.UserExercises;
import com.microservice.dataAccessLayer.entity.UserWordExercise;
import com.microservice.dataAccessLayer.mapper.AnnotationWordPosExerciseMapper;
import com.microservice.dataAccessLayer.mapper.UserWordExerciseMapper;
import com.microservice.enums.AnnotationWordPosExerciseStateEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by cjl on 2018/5/17.
 */
@Service
public class AnnotationWordPosExerciseService {

    @Autowired
    private AnnotationWordPosExerciseMapper annotationWordPosExerciseMapper;

    @Autowired
    private UserWordExerciseMapper userWordExerciseMapper;


    public AnnotationWordPosExercise getAnnotationWordPosExerciseById(int id) {
        return annotationWordPosExerciseMapper.getAnnotationWordPosExerciseById(id);
    }

    public void updateAnnotationWordPosExerciseSelective(AnnotationWordPosExercise annotationWordPosExercise) {
        annotationWordPosExerciseMapper.updateAnnotationWordExercise(annotationWordPosExercise);
    }

    /**
     * 初始化user_exercises表
     */
    public void initUserExercises(int userModifier) {
        int sum = userWordExerciseMapper.countUserWordExercise(userModifier);
        if (sum == 0) {
            //新增
            Date currentDate = new Date();
            List<AnnotationWordPosExercise> annotationSentenceExerciseList = annotationWordPosExerciseMapper.listAnnotationWordExerciseAll();
            List<UserWordExercise> userExercisesList = IntStream.range(0, annotationSentenceExerciseList.size())
                    .mapToObj(i -> new UserWordExercise(annotationSentenceExerciseList.get(i).getOriginText(),
                            annotationSentenceExerciseList.get(i).getAutoAnnotation(),
                            AnnotationWordPosExerciseStateEnum.INIT.name(),
                            userModifier,
                            currentDate,
                            currentDate,
                            annotationSentenceExerciseList.get(i).getId()
                    ))
                    .collect(Collectors.toList());
            if (userExercisesList.size() > 0)
                userWordExerciseMapper.batchAddUserWordExercise(userExercisesList);
        }
    }

    /**
     * 查询标准练习数据
     */
    public Map<String, Object> listAnnotationWordExercise(int pageIndex, int pageSize, int userModifier, String state) {
        List<AnnotationWordPosExercise> annotationSentenceExerciseList = annotationWordPosExerciseMapper.listAnnotationWordExerciseAll();

        UserWordExercise paramUserExercises = new UserWordExercise();
        paramUserExercises.setState(state);
        paramUserExercises.setUserModifier(userModifier);
        List<UserWordExercise> userExercisesList = userWordExerciseMapper.queryUserWordExerciseAnnotation(paramUserExercises);

        List<AnnotationWordPosExercise> finalAnnotationList;
        for (UserWordExercise current : userExercisesList) {
            annotationSentenceExerciseList.stream().filter(x -> x.getId() == current.getAnId())
                    .forEach(k -> {
                        k.setMemo("匹配");
                        k.setState(current.getState());
                    });
//            finalAnnotationList.addAll(annotationSentenceExerciseList.stream().filter(x -> x.getId() == current.getAnId()).collect(Collectors.toList()));
        }
        if(StringUtils.isNotBlank(state)) {
            finalAnnotationList = annotationSentenceExerciseList.stream().filter(x -> state.equals(x.getState())).collect(Collectors.toList());
        }else {
            finalAnnotationList=annotationSentenceExerciseList;
        }
        int fromIndex = (pageIndex - 1) * pageSize;
        int toIndex = pageIndex * pageSize;
        Map finalMap = new HashMap<>();
        if (finalAnnotationList.size() >= fromIndex && finalAnnotationList.size() < toIndex)
            finalMap.put("dataList", finalAnnotationList.subList(fromIndex, finalAnnotationList.size()));
        if (annotationSentenceExerciseList.size() < fromIndex)
            finalMap.put("dataList", finalAnnotationList);
        if (annotationSentenceExerciseList.size() > toIndex)
            finalMap.put("dataList", finalAnnotationList.subList(fromIndex, toIndex));

        finalMap.put("total", finalAnnotationList.size());
        return finalMap;
    }

    /**
     * 指派标准标注数据，新增前端指定标准答案集合到指定用户的user_exercises表，防止重复新增，过滤掉已经新增的标准答案集
     */
    public void designateAnnotationWordExercise(List<Integer> anIdArr, int userModifier, String state) {
        String idTemp = anIdArr.stream().map(x -> x.toString()).collect(Collectors.joining(","));

        List<UserWordExercise> userExercisesList = userWordExerciseMapper.listUserWordExerciseByUserModifier(userModifier);
        List<AnnotationWordPosExercise> annotationSentenceExerciseList = annotationWordPosExerciseMapper.listAnnotationWordExerciseByIdArr(idTemp);

        Iterator<AnnotationWordPosExercise> iterator = annotationSentenceExerciseList.iterator();
        while (iterator.hasNext()) {
            AnnotationWordPosExercise annotationSentenceExercise = iterator.next();
            for (int k = 0; k < userExercisesList.size(); k++) {
                if (userExercisesList.get(k).getAnId() == annotationSentenceExercise.getId())
                    iterator.remove();
            }
        }
        Date currentDate = new Date();
        List<UserWordExercise> finalUserExercisesList = IntStream.range(0, annotationSentenceExerciseList.size())
                .mapToObj(i -> new UserWordExercise(annotationSentenceExerciseList.get(i).getOriginText(),
                        annotationSentenceExerciseList.get(i).getAutoAnnotation(),
                        state,
                        userModifier,
                        currentDate,
                        currentDate,
                        annotationSentenceExerciseList.get(i).getId()
                ))
                .collect(Collectors.toList());
        if (finalUserExercisesList.size() > 0)
            userWordExerciseMapper.batchAddUserWordExercise(finalUserExercisesList);
    }

    public void resetUserExercisesByUserModifier(int userModifier) {
        List<UserWordExercise> userExercisesList = userWordExerciseMapper.listUserWordExerciseByUserModifier(userModifier);
        if (userExercisesList.size() > 0) {
            String idTemp = userExercisesList.stream().map(x -> "'" + x.getAnId() + "'").collect(Collectors.joining(","));
            List<AnnotationWordPosExercise> annotationSentenceExerciseList = annotationWordPosExerciseMapper.listAnnotationWordExerciseByIdArr(idTemp);
            Date currentDate = new Date();
            userExercisesList = IntStream.range(0, annotationSentenceExerciseList.size())
                    .mapToObj(i -> new UserWordExercise(annotationSentenceExerciseList.get(i).getOriginText(),
                            annotationSentenceExerciseList.get(i).getAutoAnnotation(),
                            AnnotationWordPosExerciseStateEnum.INIT.name(),
                            userModifier,
                            currentDate,
                            currentDate,
                            annotationSentenceExerciseList.get(i).getId()
                    ))
                    .collect(Collectors.toList());
            userWordExerciseMapper.batchUpdateUserWordExerciseUser(userExercisesList);
        }
    }

    private List<UserWordExercise> convert2UserExercises(List<AnnotationWordPosExercise> annotationSentenceExerciseList, String state, int userModifier) {
        List<UserWordExercise> userExercisesList = new LinkedList<>();
        if (annotationSentenceExerciseList.size() > 0) {
            for (AnnotationWordPosExercise current : annotationSentenceExerciseList) {
                UserWordExercise userExercises = new UserWordExercise();
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

    /**
     * 查询标准习题集
     */
    public Page<AnnotationWordPosExercise> listAnnotationSentExerciseByPaging(int pageIndex, int pageSize, String originText) {
        Page<AnnotationWordPosExercise> pageInfo = PageHelper.startPage(pageIndex, pageSize);
        AnnotationWordPosExercise annotationWordPosExercise = new AnnotationWordPosExercise();
        annotationWordPosExercise.setOriginText(originText);
        annotationWordPosExerciseMapper.listAnnotationWordExerciseByCondition(annotationWordPosExercise);
        return pageInfo;
    }
}
