package com.microservice.service.annotation;

import com.microservice.dataAccessLayer.entity.Corpus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by cjl on 2018/4/25.
 */
@Service
public class AsyAnnotationService {

    @Autowired
    private AnnotationBatchService annotationBatchService;

    /**
     * 异步自动标注
     * @param corpusList
     * @return
     */
    @Async("taskAsyncPool")
    public Future<Boolean> asyncAutoAnnotation(List<Corpus> corpusList) {
        boolean result = true;
        try {
            annotationBatchService.autoAnnotationByTermList(corpusList);
        } catch (Exception e) {
            result = false;
        }
        return new AsyncResult<>(result);
    }
}
