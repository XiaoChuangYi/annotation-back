package com.microservice.dataAccessLayer.mapper;

import com.microservice.dataAccessLayer.dynamicSql.UserExercisesDynamicSqlProvider;
import com.microservice.dataAccessLayer.entity.UserExercises;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by cjl on 2018/5/3.
 */
@Component
public interface UserExercisesMapper {


    @SelectProvider(type = UserExercisesDynamicSqlProvider.class, method = "queryUserExercisesAnnotation")
    @Results({
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "origin_text", property = "originText"),
            @Result(column = "standard_annotation", property = "standardAnnotation"),
            @Result(column = "practice_annotation", property = "practiceAnnotation"),
            @Result(column = "state", property = "state"),
            @Result(column = "account_name", property = "accountName"),
            @Result(column = "user_modifier", property = "userModifier"),
            @Result(column = "gmt_created", property = "gmtCreated"),
            @Result(column = "gmt_modified", property = "gmtModified"),
            @Result(column = "memo", property = "memo"),
            @Result(column = "anId", property = "anId")
    })
    List<UserExercises> queryUserExercisesAnnotation(UserExercises userExercises);


    @SelectProvider(type = UserExercisesDynamicSqlProvider.class, method = "queryUserExercisesAssociateAnnotation")
    @Results({
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "origin_text", property = "originText"),
            @Result(column = "standard_annotation", property = "standardAnnotation"),
            @Result(column = "practice_annotation", property = "practiceAnnotation"),
            @Result(column = "state", property = "state"),
            @Result(column = "account_name", property = "accountName"),
            @Result(column = "user_modifier", property = "userModifier"),
            @Result(column = "gmt_created", property = "gmtCreated"),
            @Result(column = "gmt_modified", property = "gmtModified"),
            @Result(column = "memo", property = "memo"),
            @Result(column = "anId", property = "anId")
    })
    List<UserExercises> queryUserExercisesAssociateAnnotation(UserExercises userExercises);


    @Select("select * from user_exercises where user_modifier=#{userModifier}")
    @Results({
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "origin_text", property = "originText"),
            @Result(column = "standard_annotation", property = "standardAnnotation"),
            @Result(column = "practice_annotation", property = "practiceAnnotation"),
            @Result(column = "state", property = "state"),
            @Result(column = "user_modifier", property = "userModifier"),
            @Result(column = "gmt_created", property = "gmtCreated"),
            @Result(column = "gmt_modified", property = "gmtModified"),
            @Result(column = "memo", property = "memo"),
            @Result(column = "anId", property = "anId")
    })
    List<UserExercises> listUserExercisesByUserModifier(@Param("userModifier") int userModifier);


    @Select("select * from user_exercises where id=#{id}")
    @Results({
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "origin_text", property = "originText"),
            @Result(column = "standard_annotation", property = "standardAnnotation"),
            @Result(column = "practice_annotation", property = "practiceAnnotation"),
            @Result(column = "state", property = "state"),
            @Result(column = "user_modifier", property = "userModifier"),
            @Result(column = "gmt_created", property = "gmtCreated"),
            @Result(column = "gmt_modified", property = "gmtModified"),
            @Result(column = "memo", property = "memo"),
            @Result(column = "anId", property = "anId")
    })
    UserExercises getUserExercisesById(@Param("id") int id);

    @UpdateProvider(type = UserExercisesDynamicSqlProvider.class, method = "batchUpdateUserExerciseUser")
    void batchUpdateUserExerciseUser(@Param("userExercisesList") List<UserExercises> userExercisesList);


    @Select("select count(id) from user_exercises where user_modifier=#{userModifier}")
    @ResultType(value = Integer.class)
    int countUserExercises(@Param("userModifier") int userModifier);

    @InsertProvider(type = UserExercisesDynamicSqlProvider.class, method = "batchAddUserExercises")
    void batchAddUserExercises(@Param("list") List<UserExercises> userExercisesList);

    @UpdateProvider(type = UserExercisesDynamicSqlProvider.class, method = "updateUserExercisesSelective")
    void updateUserExercisesSelective(UserExercises userExercises);
}
