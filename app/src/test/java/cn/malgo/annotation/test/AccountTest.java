package cn.malgo.annotation.test;

import cn.malgo.annotation.common.dal.model.CrmAccount;
import cn.malgo.annotation.core.service.account.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by 张钟 on 2017/10/23.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountTest {

    @Autowired
    private AccountService accountService;

    @Test
    public void testSave(){
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>打印-----------------------------------------------");
        accountService.saveAccount("Janine","123456","100");
    }

//    @Test
//    public void testCheckPwd(){
//        CrmAccount crmAccount = accountService.checkPwd("111@111.com","123456");
//        System.out.println("打印-----------------------------------------------");
//        System.out.println(crmAccount);
//    }
}
