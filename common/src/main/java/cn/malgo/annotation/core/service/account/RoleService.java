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
    private SequenceGenerator sequenceGenerator;

    @Autowired
    private CrmRoleMapper crmRoleMapper;

    /**
     * 保存角色
     * @param roleName
     *
     * */
    public CrmRole saveRole(String roleName,String roleId){
        CrmRole crmRoleOld=crmRoleMapper.selectCrmRoleByRoleName(roleName);
        AssertUtil.state(crmRoleOld==null,"角色已经存在");
        String id=sequenceGenerator.nextCodeByType(CodeGenerateTypeEnum.DEFAULT);
        int count=crmRoleMapper.selectCrmRoleCount();
        CrmRole crmRoleNew=new CrmRole();
        count++;
        crmRoleNew.setId(count+"");
        crmRoleNew.setRoleId(roleId);//定义个枚举类，作为角色的编码
        crmRoleNew.setRoleName(roleName);
        crmRoleNew.setGmtCreated(new Date());
        int insertResult=crmRoleMapper.insert(crmRoleNew);
        AssertUtil.state(insertResult>0,"保存角色失败");
        return crmRoleMapper.selectCrmRoleByRoleId(id);
    }
    /**
     *查询角色列表
     * */
    public List<CrmRole> selectAllRole(){
        List<CrmRole> crmRoleList=crmRoleMapper.selectAll();
        return crmRoleList;
    }
    /**
     *更新角色
     *@param roleName
     * */
    public CrmRole updateRole(String roleName,String roleId){
        return  null;
    }
}
