package cn.malgo.annotation.common.service.integration.apiserver.result;

import org.apache.commons.lang.StringUtils;

/**
 * Created by 张钟 on 2017/10/18.
 */
public class AnnotationResult {

    private String id;

    private String annotation;

    public static boolean  chekc(AnnotationResult annotationResult){
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
