package cn.malgo.annotation.service;

import cn.malgo.annotation.request.brat.AddRelationRequest;
import cn.malgo.annotation.request.brat.DeleteRelationRequest;
import cn.malgo.annotation.request.brat.UpdateRelationRequest;

/** Created by cjl on 2018/5/31. */
public interface RelationOperateService {

  String addRelation(AddRelationRequest addRelationRequest, int roleId);

  String updateRelation(UpdateRelationRequest updateRelationRequest, int roleId);

  String deleteRelation(DeleteRelationRequest deleteRelationRequest, int roleId);
}
