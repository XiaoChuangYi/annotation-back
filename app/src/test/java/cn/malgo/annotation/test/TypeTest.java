package cn.malgo.annotation.test;

import cn.malgo.annotation.common.dal.model.AnType;
import cn.malgo.annotation.core.service.type.TypeService;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by cjl on 2017/11/20.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TypeTest {
        @Autowired
        private TypeService typeService;

//        @Test
//        public  void  addType(){
//                typeService.insertType("身体结构","Body-structure");
//        }
        @Test
        public void selectType(){
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>打印<<<<<<<<<<<<<<<<<<<<<");
                List<AnType> anTypeList=typeService.selectAllTypes();
                System.out.println(JSON.parseArray(JSON.toJSONString(anTypeList)));
        }
//        @Test
//        public void updateType(){
//                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>打印<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
//                typeService.updateTypeName("1","身体构造");
//        }
        @Test
        public  void deleteType(){
                typeService.deleteType("1");
        }

}
