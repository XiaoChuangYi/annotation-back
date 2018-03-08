package cn.malgo.annotation.core.service.account;

import cn.malgo.annotation.common.dal.mapper.AccountRoleRelationMapper;
import cn.malgo.annotation.common.dal.model.AccountRoleRelation;
import cn.malgo.annotation.common.dal.sequence.CodeGenerateTypeEnum;
import cn.malgo.annotation.common.dal.sequence.SequenceGenerator;
import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.common.util.security.MD5Util;
import cn.malgo.annotation.core.model.enums.CommonStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.malgo.annotation.common.dal.mapper.CrmAccountMapper;
import cn.malgo.annotation.common.dal.model.CrmAccount;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * @author 张钟
 * @date 2017/10/23
 */
@Service
public class AccountService {

    @Autowired
    private CrmAccountMapper crmAccountMapper;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    @Autowired
    private AccountRoleRelationMapper accountRoleRelationMapper;

    /**
     * 获取用户信息
     */
     public List<CrmAccount> listAccount(){
         List<CrmAccount> crmAccountList=crmAccountMapper.selectAll();
         return  crmAccountList;
     }
    /**
     * 重载保存账户方法，每新增一个用户，并且分配一个角色给该用户
     * 因为同时对两张表进行了操作，所以添加事务
     * @param accountNo
     * @param password
     * @param roleId
     * */
    @Transactional
    public String saveAccount(String accountNo,String password,String roleId){
        System.out.print("不支持重载？！");
        CrmAccount crmAccountOld =   crmAccountMapper.selectByAccountNo(accountNo);
        AssertUtil.state(crmAccountOld==null,"账号已经存在");
        //用户账号对象
        String id = sequenceGenerator.nextCodeByType(CodeGenerateTypeEnum.DEFAULT);
        CrmAccount crmAccountNew = new CrmAccount();
        crmAccountNew.setAccountNo(accountNo);
        crmAccountNew.setId(id);
        crmAccountNew.setLoginPwd(MD5Util.getMD5String(password));
        crmAccountNew.setState(CommonStatusEnum.ENABLE.name());
        //账号对象关系表
        String userRoleId=sequenceGenerator.nextCodeByType(CodeGenerateTypeEnum.DEFAULT);
        int count=accountRoleRelationMapper.selectCountRelation();
        AccountRoleRelation accountRoleRelation=new AccountRoleRelation();
        count++;
        accountRoleRelation.setId(count+"");
        accountRoleRelation.setAccountNo(accountNo);
        accountRoleRelation.setRoleId(roleId);
        accountRoleRelation.setUseroleId(userRoleId);
        int insertCrmRole = crmAccountMapper.insert(crmAccountNew);
        AssertUtil.state(insertCrmRole>0,"保存账号失败");
        int insertAccountRoleRelation=accountRoleRelationMapper.
                insert(accountRoleRelation);
        AssertUtil.state(insertAccountRoleRelation>0,"保存用户角色对照记录失败");
        return "保存成功";
    }

    /**
     * 保存用户账户
     * @param accountNo
     * @param pwd
     */
    public CrmAccount saveAccount(String accountNo, String pwd){
        CrmAccount crmAccountOld =   crmAccountMapper.selectByAccountNo(accountNo);
        AssertUtil.state(crmAccountOld==null,"账号已经存在");

        String id = sequenceGenerator.nextCodeByType(CodeGenerateTypeEnum.DEFAULT);
        CrmAccount crmAccountNew = new CrmAccount();
        crmAccountNew.setAccountNo(accountNo);
        crmAccountNew.setId(id);
        crmAccountNew.setLoginPwd(MD5Util.getMD5String(pwd));
        crmAccountNew.setState(CommonStatusEnum.ENABLE.name());

        int insertResult = crmAccountMapper.insert(crmAccountNew);
        AssertUtil.state(insertResult>0,"保存账号失败");

        return crmAccountMapper.selectByPrimaryKey(id);
    }

    /**
     * 检查登录密码是否正确
     * @param accountNo
     * @param pwd
     * @return
     */
    public CrmAccount checkPwd(String accountNo, String pwd){

        CrmAccount crmAccountOld =   crmAccountMapper.selectByAccountNo(accountNo);
        AssertUtil.notNull(crmAccountOld,"账号不存在");

        AssertUtil.state(MD5Util.checkPassword(pwd,crmAccountOld.getLoginPwd()),"密码错误");

        return crmAccountOld;
    }
}
