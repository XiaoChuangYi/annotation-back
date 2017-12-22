package cn.malgo.annotation.test;

import cn.malgo.annotation.common.dal.model.AnType;
import cn.malgo.annotation.core.service.annotation.AnnotationService;
import cn.malgo.annotation.core.service.type.AsyncTypeBatchService;
import cn.malgo.annotation.core.service.type.TypeAnnotationBatchService;
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

        @Autowired
        private AnnotationService annotationService;

        @Autowired
        private TypeAnnotationBatchService typeAnnotationBatchService;

        @Autowired
        private AsyncTypeBatchService asyncTypeBatchService;

//        @Test
//        public  void  addType(){
//                typeService.insertType("身体结构","Body-structure");
////        }
//        @Test
//        public void selectType(){
//                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>打印<<<<<<<<<<<<<<<<<<<<<");
//                List<AnType> anTypeList=typeService.selectPaginationTypesAndShowParent(1,50);
//                System.out.println(JSON.parseArray(JSON.toJSONString(anTypeList)));
//        }
//        @Test
//        public void updateType(){
//                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>打印<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
//                Future<Boolean> future = asyncTypeBatchService
//                        .asyncAutoBatchType("Body-struct","Body-structure");
//                try {
//                        Boolean result = future.get();
//                        if(result){
//                                System.out.println("更新标注表成功");
//                        }
//                } catch (InterruptedException e) {
//                        e.printStackTrace();
//                } catch (ExecutionException e) {
//                        e.printStackTrace();
//                }
//                typeService.updateTypeCodeById("1","Body-structure");
//                typeService.updateBatchTypeOnAtomicTerm("Error","Janine");
//                typeService.updateBatchTypeOnTerm("physical force","Janine");
//                typeAnnotationBatchService.batchReplaceAnnotationTerm("Body-structure","Body-struct");
//        }



//        @Test
//        public  void deleteType(){
//                typeService.deleteType("1");
//        }
//        @Test
//        public  void selectType(){
//                List<Annotation> list=typeService.queryAnnotationByType("Body-structure",);
//                System.out.println(JSON.parseArray(JSON.toJSONString(list)));
//        }

}
