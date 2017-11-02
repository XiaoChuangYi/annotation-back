package cn.malgo.annotation.core.model.check;

import java.util.List;

import cn.malgo.annotation.core.model.annotation.TermAnnotationModel;
import cn.malgo.annotation.core.model.convert.AnnotationConvert;

/**
 * @author 张钟
 * @date 2017/10/31
 */
public class AnnotationChecker {

    /**
     * 检查标注中的
     * @param annotation
     * @return
     */
    public static boolean hasAmbiguity(String annotation) {
        List<TermAnnotationModel> termAnnotationModelList = AnnotationConvert
            .convertAnnotationModelList(annotation);
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
        boolean isSameType = source.getType().equals(target.getType());
        if (isSameType) {
            return false;
        }

        //上述条件均不满足,有歧义
        return true;
    }
}
