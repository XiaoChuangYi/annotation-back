package cn.malgo.annotation.test;


import cn.malgo.annotation.core.business.visual.TypeHierarchyNode;
import cn.malgo.annotation.core.service.config.VisualService;
import com.alibaba.fastjson.JSONArray;
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

//    @Test
//    public void test() {
//        List<TypeHierarchyNode> typeHierarchyNodeList =visualService.getEntitySection();
//        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<<<<<<<<");
//        System.out.println(JSON.parseArray(JSON.toJSONString(typeHierarchyNodeList)));
//    }
    @Test
    public void test() {
        List<TypeHierarchyNode> typeHierarchyNodeList =visualService.getDrawingSection();
        JSONArray array=visualService.fillTypeConfiguration(typeHierarchyNodeList);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println(array);
    }

}
