package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.UserExerciseRepository;
import cn.malgo.annotation.entity.UserExercise;
import cn.malgo.annotation.request.brat.AddRelationRequest;
import cn.malgo.annotation.request.brat.DeleteRelationRequest;
import cn.malgo.annotation.request.brat.UpdateRelationRequest;
import cn.malgo.annotation.service.RelationOperateService;
import cn.malgo.annotation.utils.AnnotationConvert;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Created by cjl on 2018/5/31. */
@Service("exercise-relation")
public class UserRelationOperateServiceExerciseImpl implements RelationOperateService {

  private final UserExerciseRepository userExerciseRepository;

  @Autowired
  public UserRelationOperateServiceExerciseImpl(UserExerciseRepository userExerciseRepository) {
    this.userExerciseRepository = userExerciseRepository;
  }

  @Override
  public String addRelation(AddRelationRequest addRelationRequest) {
    Optional<UserExercise> optional = userExerciseRepository.findById(addRelationRequest.getId());
    if (optional.isPresent()) {
      UserExercise userExercise = optional.get();
      String annotation =
          AnnotationConvert.addRelationsAnnotation(
              userExercise.getUserAnnotation(),
              addRelationRequest.getSourceTag(),
              addRelationRequest.getTargetTag(),
              addRelationRequest.getRelation());
      return annotation;
    }
    return "";
  }

  @Override
  public String updateRelation(UpdateRelationRequest updateRelationRequest) {
    Optional<UserExercise> optional =
        userExerciseRepository.findById(updateRelationRequest.getId());
    if (optional.isPresent()) {
      UserExercise userExercise = optional.get();
      String annotation =
          AnnotationConvert.updateRelationAnnotation(
              userExercise.getUserAnnotation(),
              updateRelationRequest.getReTag(),
              updateRelationRequest.getRelation());
      return annotation;
    }
    return "";
  }

  @Override
  public String deleteRelation(DeleteRelationRequest deleteRelationRequest) {
    Optional<UserExercise> optional =
        userExerciseRepository.findById(deleteRelationRequest.getId());
    if (optional.isPresent()) {
      UserExercise userExercise = optional.get();
      String annotation =
          AnnotationConvert.deleteRelationsAnnotation(
              userExercise.getUserAnnotation(), deleteRelationRequest.getReTag());
      return annotation;
    }
    return "";
  }
}
