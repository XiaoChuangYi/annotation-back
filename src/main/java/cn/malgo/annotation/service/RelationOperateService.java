package cn.malgo.annotation.service;

import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.request.brat.AddRelationRequest;
import cn.malgo.annotation.request.brat.DeleteRelationRequest;
import cn.malgo.annotation.request.brat.UpdateRelationRequest;

public interface RelationOperateService {
  String addRelation(AnnotationCombine annotation, AddRelationRequest addRelationRequest);

  String updateRelation(AnnotationCombine annotation, UpdateRelationRequest updateRelationRequest);

  String deleteRelation(AnnotationCombine annotation, DeleteRelationRequest deleteRelationRequest);
}
