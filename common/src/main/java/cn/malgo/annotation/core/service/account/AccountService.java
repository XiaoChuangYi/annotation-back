package cn.malgo.annotation.core.service.account;

import cn.malgo.annotation.common.dal.sequence.CodeGenerateTypeEnum;
import cn.malgo.annotation.common.dal.sequence.SequenceGenerator;
import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.common.util.security.MD5Util;
import cn.malgo.annotation.core.model.enums.CommonStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.malgo.annotation.common.dal.mapper.CrmAccountMapper;
import cn.malgo.annotation.common.dal.model.CrmAccount;

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
