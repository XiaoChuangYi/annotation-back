package com.microservice.utils;

import com.microservice.vo.TermAnnotationModel;

import java.util.List;

/**
 * Created by cjl on 2018/4/25.
 */
public class AnnotationChecker {

    public static final String UN_CONFIRMED = "-unconfirmed";
    /**
     * 检查标注中的
     * @param annotation
     * @return
     */
    public static boolean hasAmbiguity(String annotation) {
        List<TermAnnotationModel> termAnnotationModelList = AnnotationConvert
                .convertAnnotationModelList(annotation);
        //标注中只有一个原子术语,必定无歧义
        if (termAnnotationModelList == null || termAnnotationModelList.size() <= 1) {
            return false;
        }
        for (int first = 0; first < termAnnotationModelList.size(); first++) {
            for (int second = 0; second < termAnnotationModelList.size(); second++) {
                if (first == second) {
                    continue;
                }
                if (hasAmbiguity(termAnnotationModelList.get(first),
                        termAnnotationModelList.get(second))) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * 检查两个行标注是否具有歧义
     * @param source
     * @param target
     * @return
     */
    public static boolean hasAmbiguity(TermAnnotationModel source, TermAnnotationModel target) {

        //起始位置不同,不具有歧义
        boolean isSameStart = source.getStartPosition() == target.getStartPosition();
        if (!isSameStart) {
            return false;
        }
        //结束位置不同,不具有歧义
        boolean isSameEnd = source.getEndPosition() == target.getEndPosition();
        if (!isSameEnd) {
            return false;
        }
        //标注文本不同,不具有歧义
        boolean isSameTerm = source.getTerm().equals(target.getTerm());
        if (!isSameTerm) {
            return false;
        }
        //标注类型相同,不具有歧义
        boolean isSameType = source.getType().replace(UN_CONFIRMED, "")
                .equals(target.getType().replace(UN_CONFIRMED, ""));
        if (isSameType) {
            return false;
        }

        //上述条件均不满足,有歧义
        return true;
    }

}
