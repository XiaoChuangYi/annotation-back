package cn.malgo.annotation.test;


import cn.malgo.annotation.common.dal.model.result.TypeHierarchyNode;
import cn.malgo.annotation.core.service.config.VisualService;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by cjl on 2017/12/13.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DrawTest {

    @Autowired
    private VisualService visualService;

    @Test
    public void test() {
        List<TypeHierarchyNode> typeHierarchyNodeList =visualService.getEntitySection();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println(JSON.parseArray(JSON.toJSONString(typeHierarchyNodeList)));
    }

}
