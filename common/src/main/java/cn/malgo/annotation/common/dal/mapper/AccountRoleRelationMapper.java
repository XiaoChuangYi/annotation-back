package cn.malgo.annotation.common.dal.mapper;

import cn.malgo.annotation.common.dal.model.AccountRoleRelation;
import cn.malgo.annotation.common.dal.util.CommonMapper;

public interface AccountRoleRelationMapper extends CommonMapper<AccountRoleRelation> {
    int selectCountRelation();
}