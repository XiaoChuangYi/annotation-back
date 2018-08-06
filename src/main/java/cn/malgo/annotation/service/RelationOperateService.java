package cn.malgo.annotation.service;

import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.request.brat.AddRelationRequest;
import cn.malgo.annotation.request.brat.DeleteRelationRequest;
import cn.malgo.annotation.request.brat.UpdateRelationRequest;

public interface RelationOperateService {

  String addRelation(AnnotationNew annotation, AddRelationRequest addRelationRequest);

  String updateRelation(AnnotationNew annotation, UpdateRelationRequest updateRelationRequest);

  String deleteRelation(AnnotationNew annotation, DeleteRelationRequest deleteRelationRequest);
}
