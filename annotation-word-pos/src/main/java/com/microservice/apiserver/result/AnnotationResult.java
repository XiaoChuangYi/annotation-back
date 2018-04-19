package com.microservice.apiserver.result;

import org.apache.commons.lang.StringUtils;

/**
 * Created by cjl on 2018/4/11.
 */
public class AnnotationResult {
    private String id;

    private String annotation;

    public static boolean  check(AnnotationResult annotationResult){
        if(annotationResult==null){
            return false;
        }
        if(StringUtils.isBlank(annotationResult.getId()) || StringUtils.isBlank(annotationResult.getAnnotation())){
            return false;
        }
        return true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }
}
