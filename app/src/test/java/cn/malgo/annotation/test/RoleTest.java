package cn.malgo.annotation.test;

import cn.malgo.annotation.common.dal.model.CrmRole;
import cn.malgo.annotation.core.service.account.RoleService;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by cjl on 2017/11/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RoleTest {

    @Autowired
    private RoleService roleService;

    @Test
    public void testRoleList(){
        List<CrmRole> roleList=roleService.selectAllRole();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>打印<<<<<<<<<<<<<<<<<<<<<");
        System.out.println(JSON.parseArray(JSON.toJSONString(roleList)));
    }

}
