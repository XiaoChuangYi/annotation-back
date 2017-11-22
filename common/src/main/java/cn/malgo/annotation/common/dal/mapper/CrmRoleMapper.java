package cn.malgo.annotation.common.dal.mapper;

import cn.malgo.annotation.common.dal.model.CrmRole;
import cn.malgo.annotation.common.dal.util.CommonMapper;
import org.apache.ibatis.annotations.Param;
public interface CrmRoleMapper extends CommonMapper<CrmRole> {

    CrmRole selectCrmRoleByRoleId(@Param("roleId") String roleId);
    CrmRole selectCrmRoleByRoleName(@Param("roleName") String roleName);
    int selectCrmRoleCount();

}