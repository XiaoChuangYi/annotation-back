package cn.malgo.annotation.core.model.convert;

import com.alibaba.fastjson.JSONObject;

import cn.malgo.annotation.common.dal.model.AnTermAnnotation;
import cn.malgo.core.definition.Document;
import cn.malgo.core.definition.utils.DocumentManipulator;

/**
 * Created by 张钟 on 2017/10/20.
 */
public class AnnotationConvert {

    public static JSONObject convertToBratFormat(AnTermAnnotation anTermAnnotation) {
        Document document = new Document(anTermAnnotation.getTerm(), null);
        DocumentManipulator.parseBratAnnotations(anTermAnnotation.getFinalAnnotation(), document);
        JSONObject result = DocumentManipulator.toBratAjaxFormat(document);
        return result;
    }
}
