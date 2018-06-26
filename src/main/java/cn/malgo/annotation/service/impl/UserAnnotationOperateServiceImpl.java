package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.UserExerciseRepository;
import cn.malgo.annotation.entity.UserExercise;
import cn.malgo.annotation.request.brat.AddAnnotationRequest;
import cn.malgo.annotation.request.brat.DeleteAnnotationRequest;
import cn.malgo.annotation.request.brat.UpdateAnnotationRequest;
import cn.malgo.annotation.service.AnnotationOperateService;
import cn.malgo.annotation.utils.AnnotationConvert;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Created by cjl on 2018/5/31. */
@Service("exercise-entity")
public class UserAnnotationOperateServiceImpl implements AnnotationOperateService {

  private final UserExerciseRepository userExerciseRepository;

  @Autowired
  public UserAnnotationOperateServiceImpl(UserExerciseRepository userExerciseRepository) {
    this.userExerciseRepository = userExerciseRepository;
  }

  @Override
  public String addAnnotation(AddAnnotationRequest addAnnotationRequest, int roleId) {
    Optional<UserExercise> optional = userExerciseRepository.findById(addAnnotationRequest.getId());
    if (optional.isPresent()) {
      UserExercise userExercise = optional.get();
      String newAnnotation =
          AnnotationConvert.addEntitiesAnnotation(
              userExercise.getUserAnnotation(),
              addAnnotationRequest.getType(),
              addAnnotationRequest.getStartPosition(),
              addAnnotationRequest.getEndPosition(),
              addAnnotationRequest.getTerm());
      return newAnnotation;
    }
    return "";
  }

  @Override
  public String deleteAnnotation(DeleteAnnotationRequest deleteAnnotationRequest) {
    Optional<UserExercise> optional =
        userExerciseRepository.findById(deleteAnnotationRequest.getId());
    if (optional.isPresent()) {
      UserExercise userExercise = optional.get();
      String newAnnotation =
          AnnotationConvert.deleteEntitiesAnnotation(
              userExercise.getUserAnnotation(), deleteAnnotationRequest.getTag());
      return newAnnotation;
    }
    return "";
  }

  @Override
  public String updateAnnotation(UpdateAnnotationRequest updateAnnotationRequest) {
    Optional<UserExercise> optional =
        userExerciseRepository.findById(updateAnnotationRequest.getId());
    if (optional.isPresent()) {
      UserExercise userExercise = optional.get();
      String newAnnotation =
          AnnotationConvert.updateEntitiesAnnotation(
              userExercise.getUserAnnotation(),
              updateAnnotationRequest.getTag(),
              updateAnnotationRequest.getNewType());
      return newAnnotation;
    }
    return "";
  }
}
