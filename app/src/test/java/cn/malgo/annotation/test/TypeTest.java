package cn.malgo.annotation.test;

import cn.malgo.annotation.common.dal.model.AnAtomicTerm;
import cn.malgo.annotation.common.dal.model.AnTermAnnotation;
import cn.malgo.annotation.common.dal.model.AnType;
import cn.malgo.annotation.core.model.enums.annotation.AnnotationStateEnum;
import cn.malgo.annotation.core.service.annotation.AnnotationService;
import cn.malgo.annotation.core.service.type.AsyncTypeBatchService;
import cn.malgo.annotation.core.service.type.TypeAnnotationBatchService;
import cn.malgo.annotation.core.service.type.TypeService;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
//                List<String> stateList = new ArrayList<>();
//                stateList.add(AnnotationStateEnum.PROCESSING.name());
//                Page<AnTermAnnotation> page=annotationService.queryByStateList(stateList,1,10);
////                List<AnType> anTypeList=typeService.selectAllTypes();
//                System.out.println(JSON.parseArray(JSON.toJSONString(page)));
//        }
        @Test
        public void updateType(){
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
                typeService.updateTypeCodeById("1","Body-structure");
//                typeService.updateBatchTypeOnAtomicTerm("Error","Janine");
//                typeService.updateBatchTypeOnTerm("physical force","Janine");
//                typeAnnotationBatchService.batchReplaceAnnotationTerm("Body-structure","Body-struct");
        }



//        @Test
//        public  void deleteType(){
//                typeService.deleteType("1");
//        }

}
