package cn.malgo.annotation.core.service.account;

import cn.malgo.annotation.common.dal.mapper.CrmRoleMapper;
import cn.malgo.annotation.common.dal.model.CrmRole;
import cn.malgo.annotation.common.dal.sequence.CodeGenerateTypeEnum;
import cn.malgo.annotation.common.dal.sequence.SequenceGenerator;
import cn.malgo.annotation.common.util.AssertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by cjl on 2017/11/17.
 */
@Service
public class RoleService {

    @Autowired
    private CrmRoleMapper crmRoleMapper;

    /**
     *查询角色列表
     * */
    public List<CrmRole> listRole(){
        List<CrmRole> crmRoleList=crmRoleMapper.selectAll();
        return crmRoleList;
    }
}
