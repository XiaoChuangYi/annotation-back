package cn.malgo.annotation.core.model.check;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;

import cn.malgo.annotation.common.util.log.LogUtil;
import cn.malgo.annotation.core.model.annotation.TermAnnotationModel;
import cn.malgo.annotation.core.model.convert.AnnotationConvert;

/**
 * @author 张钟
 * @date 2017/10/31
 */
public class AnnotationChecker {

    public static Logger             logger       = Logger.getLogger(AnnotationChecker.class);

    public static final String UN_CONFIRMED = "-unconfirmed";

    /**
     * 检查标注是否是经过确认的
     * @param termAnnotationModel
     * @return
     */
    public static boolean isConfirmed(TermAnnotationModel termAnnotationModel) {
        if (termAnnotationModel.getType().contains(UN_CONFIRMED)) {
            return false;
        }
        return true;
    }

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

    /**
     * 检查标注中的节点是否与整条标注中的其他节点是否存在二义性(排除自身)
     * @param target
     * @param termAnnotationModelList
     * @return
     */
    public static boolean hasAmbiguity(TermAnnotationModel target,
                                       List<TermAnnotationModel> termAnnotationModelList) {
        int count = 0;
        for (TermAnnotationModel termAnnotationModel : termAnnotationModelList) {
            if (target.getStartPosition() == termAnnotationModel.getStartPosition()
                && target.getEndPosition() == termAnnotationModel.getEndPosition()) {
                count++;
            }
        }
        if (count <= 1) {
            return false;
        } else {
            LogUtil.info(logger,"存在二义性,文本:"+ JSONObject.toJSONString(termAnnotationModelList));
            LogUtil.info(logger,"检查  "+target.getTerm()+":"+target.getType()+"的二义性");
            return true;
        }
    }
}
