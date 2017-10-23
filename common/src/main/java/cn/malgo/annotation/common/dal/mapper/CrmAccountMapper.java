package cn.malgo.annotation.common.dal.mapper;

import cn.malgo.annotation.common.dal.model.CrmAccount;
import cn.malgo.annotation.common.dal.util.CommonMapper;
import org.apache.ibatis.annotations.Param;

public interface CrmAccountMapper extends CommonMapper<CrmAccount> {

    CrmAccount selectByAccountNo(@Param("accountNo") String accountNo);
}