package com.malgo.service;

import com.malgo.request.brat.AddRelationRequest;
import com.malgo.request.brat.DeleteRelationRequest;
import com.malgo.request.brat.UpdateRelationRequest;

/**
 * Created by cjl on 2018/5/31.
 */
public interface RelationOperateService {

  String addRelation(AddRelationRequest addRelationRequest);

  String updateRelation(UpdateRelationRequest updateRelationRequest);

  String deleteRelation(DeleteRelationRequest deleteRelationRequest);

}
