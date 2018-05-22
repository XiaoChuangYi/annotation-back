package com.microservice.dataAccessLayer.mapper;

import com.microservice.dataAccessLayer.dynamicSql.UserWordExerciseDynamicSqlProvider;
import com.microservice.dataAccessLayer.entity.UserWordExercise;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by cjl on 2018/5/17.
 */
@Component
public interface UserWordExerciseMapper {

    @SelectProvider(type=UserWordExerciseDynamicSqlProvider.class,method = "queryUserWordExerciseAnnotation")
    @Results({
            @Result(id=true,column ="id" ,property = "id"),
            @Result(column ="origin_text" ,property = "originText"),
            @Result(column = "standard_annotation",property = "standardAnnotation"),
            @Result(column = "practice_annotation",property = "practiceAnnotation"),
            @Result(column = "state",property = "state"),
            @Result(column = "account_name",property = "accountName"),
            @Result(column = "user_modifier",property = "userModifier"),
            @Result(column = "gmt_created",property = "gmtCreated"),
            @Result(column = "gmt_modified",property = "gmtModified"),
            @Result(column = "memo",property = "memo"),
            @Result(column = "anId",property = "anId")
    })
    List<UserWordExercise> queryUserWordExerciseAnnotation(UserWordExercise userWordExercise);


    @SelectProvider(type=UserWordExerciseDynamicSqlProvider.class,method = "queryUserExerciseAssociateAnnotation")
    @Results({
            @Result(id=true,column ="id" ,property = "id"),
            @Result(column ="origin_text" ,property = "originText"),
            @Result(column = "standard_annotation",property = "standardAnnotation"),
            @Result(column = "practice_annotation",property = "practiceAnnotation"),
            @Result(column = "state",property = "state"),
            @Result(column = "account_name",property = "accountName"),
            @Result(column = "user_modifier",property = "userModifier"),
            @Result(column = "gmt_created",property = "gmtCreated"),
            @Result(column = "gmt_modified",property = "gmtModified"),
            @Result(column = "memo",property = "memo"),
            @Result(column = "anId",property = "anId")
    })
    List<UserWordExercise> queryUserExerciseAssociateAnnotation(UserWordExercise userWordExercise);

    @Select("select * from user_word_exercise where user_modifier=#{userModifier}")
    @Results({
            @Result(id=true,column ="id" ,property = "id"),
            @Result(column ="origin_text" ,property = "originText"),
            @Result(column = "standard_annotation",property = "standardAnnotation"),
            @Result(column = "practice_annotation",property = "practiceAnnotation"),
            @Result(column = "state",property = "state"),
            @Result(column = "user_modifier",property = "userModifier"),
            @Result(column = "gmt_created",property = "gmtCreated"),
            @Result(column = "gmt_modified",property = "gmtModified"),
            @Result(column = "memo",property = "memo"),
            @Result(column = "anId",property = "anId")
    })
    List<UserWordExercise> listUserWordExerciseByUserModifier(@Param("userModifier") int userModifier);


    @Select("select * from user_word_exercise where id=#{id}")
    @Results({
            @Result(id=true,column ="id" ,property = "id"),
            @Result(column ="origin_text" ,property = "originText"),
            @Result(column = "standard_annotation",property = "standardAnnotation"),
            @Result(column = "practice_annotation",property = "practiceAnnotation"),
            @Result(column = "state",property = "state"),
            @Result(column = "user_modifier",property = "userModifier"),
            @Result(column = "gmt_created",property = "gmtCreated"),
            @Result(column = "gmt_modified",property = "gmtModified"),
            @Result(column = "memo",property = "memo"),
            @Result(column = "anId",property = "anId")
    })
    UserWordExercise getUserWordExerciseById(@Param("id") int id);


    @UpdateProvider(type=UserWordExerciseDynamicSqlProvider.class,method = "batchUpdateUserWordExerciseUser")
    void batchUpdateUserWordExerciseUser(@Param("userWordExerciseList") List<UserWordExercise> userExercisesList);


    @Select("select count(id) from user_word_exercise where user_modifier=#{userModifier}")
    @ResultType(value = Integer.class)
    int countUserWordExercise(@Param("userModifier") int userModifier);

    @InsertProvider(type=UserWordExerciseDynamicSqlProvider.class,method = "batchAddUserWordExercise")
    void batchAddUserWordExercise(@Param("list") List<UserWordExercise> userExercisesList);

    @UpdateProvider(type = UserWordExerciseDynamicSqlProvider.class,method = "updateUserWordExerciseSelective")
    void  updateUserWordExerciseSelective(UserWordExercise userWordExercise);

}
