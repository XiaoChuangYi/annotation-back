package com.malgo.service.impl;

import com.malgo.dao.UserExerciseRepository;
import com.malgo.entity.UserExercise;
import com.malgo.request.brat.AddRelationRequest;
import com.malgo.request.brat.DeleteRelationRequest;
import com.malgo.request.brat.UpdateRelationRequest;
import com.malgo.service.RelationOperateService;
import com.malgo.utils.AnnotationConvert;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by cjl on 2018/5/31.
 */
@Service("exercise-relation")
public class UserRelationOperateServiceExerciseImpl implements RelationOperateService {

  private final UserExerciseRepository userExerciseRepository;

  @Autowired
  public UserRelationOperateServiceExerciseImpl(UserExerciseRepository userExerciseRepository){
    this.userExerciseRepository=userExerciseRepository;
  }

  @Override
  public String addRelation(AddRelationRequest addRelationRequest) {
    Optional<UserExercise> optional =userExerciseRepository.findById(addRelationRequest.getId());
    if(optional.isPresent()){
      UserExercise userExercise=optional.get();
      String annotation= AnnotationConvert.addRelationsAnnotation(userExercise.getUserAnnotation(),
          addRelationRequest.getSourceTag(),addRelationRequest.getTargetTag(),addRelationRequest.getRelation());
      return annotation;
    }
    return "";
  }

  @Override
  public String updateRelation(UpdateRelationRequest updateRelationRequest) {
    Optional<UserExercise> optional =userExerciseRepository.findById(updateRelationRequest.getId());
    if(optional.isPresent()){
      UserExercise userExercise=optional.get();
      String annotation=AnnotationConvert.updateRelationAnnotation(userExercise.getUserAnnotation(),updateRelationRequest.getRTag(),
          updateRelationRequest.getRelation());
      return annotation;
    }
    return "";
  }

  @Override
  public String deleteRelation(DeleteRelationRequest deleteRelationRequest) {
    Optional<UserExercise> optional =userExerciseRepository.findById(deleteRelationRequest.getId());
    if(optional.isPresent()) {
      UserExercise userExercise = optional.get();
      String annotation=AnnotationConvert.deleteRelationsAnnotation(userExercise.getUserAnnotation(),deleteRelationRequest.getRTag());
      return annotation;
    }
    return "";
  }
}
