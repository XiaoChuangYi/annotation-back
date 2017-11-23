package cn.malgo.annotation.core.service.type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

/**
 * Created by cjl on 2017/11/23.
 */
@Service
public class AsyncTypeBatchService {

    @Autowired
    private TypeAnnotationBatchService typeAnnotationBatchService;

    /**
     * 异步自动更新标注表中的type类型
     *@param typeOld
     *@param typeNew
     */
    @Async("taskAsyncPool")
    public Future<Boolean> asyncAutoBatchType(String typeOld,String typeNew) {
        boolean result = true;
        try {
            typeAnnotationBatchService.batchReplaceAnnotationTerm(typeOld,typeNew);
        } catch (Exception e) {
            result = false;
        }
        return new AsyncResult<>(result);
    }
}
