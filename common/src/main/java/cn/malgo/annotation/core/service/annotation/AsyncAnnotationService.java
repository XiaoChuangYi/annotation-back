package cn.malgo.annotation.core.service.annotation;

import java.util.List;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import cn.malgo.annotation.common.dal.model.AnTerm;

/**
 *
 * @author 张钟
 * @date 2017/10/20
 */

@Service
public class AsyncAnnotationService {

    @Autowired
    private AnnotationService annotationService;

    /**
     * 异步自动标注
     * @param anTermList
     * @return
     */
    @Async("taskAsyncPool")
    public Future<Boolean> asyncAutoAnnotation(List<AnTerm> anTermList) {
        boolean result = true;
        try {
            annotationService.autoAnnotationByTermList(anTermList);
        } catch (Exception e) {
            result = false;
        }
        return new AsyncResult<>(result);
    }
}
