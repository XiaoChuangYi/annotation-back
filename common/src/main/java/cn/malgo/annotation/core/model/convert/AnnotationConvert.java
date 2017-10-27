package cn.malgo.annotation.core.model.convert;

import java.text.MessageFormat;

import com.sun.org.apache.bcel.internal.generic.NEW;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.malgo.annotation.common.dal.model.AnTermAnnotation;
import cn.malgo.annotation.common.service.integration.apiserver.vo.TermTypeVO;
import cn.malgo.core.definition.Document;
import cn.malgo.core.definition.utils.DocumentManipulator;

/**
 * Created by 张钟 on 2017/10/20.
 */
public class AnnotationConvert {

    /**
     * 标注格式,样例如下
     * T3	Pharmaceutical-unconfirmed 8 10	卡铂
     */
    public static String AN_LINE_FORMAT = "{0}\t{1} {2} {3}\t{4}\n";

    public static JSONObject convertToBratFormat(AnTermAnnotation anTermAnnotation) {
        Document document = new Document(anTermAnnotation.getTerm(), null);
        DocumentManipulator.parseBratAnnotations(anTermAnnotation.getFinalAnnotation(), document);
        JSONObject result = DocumentManipulator.toBratAjaxFormat(document);
        return result;
    }

    /**
     * 根据标注内容,生成信的标注标签
     * @param annotation
     * @return
     */
    public static String getNewTag(String annotation) {

        if (StringUtils.isBlank(annotation)) {
            return "T" + 1;
        }
        int tagIndexMax = 1;
        String[] lines = annotation.split("\n");
        for (String an : lines) {
            Integer tagIndex = Integer.valueOf(an.split("\t")[0].replace("T", ""));
            if (tagIndex > tagIndexMax) {
                tagIndexMax = tagIndex;
            }
        }
        return "T" + (tagIndexMax + 1);
    }

    /**
     * 构建新的标注
     * @param oldManualAnnotation
     * @param newType
     * @param newStart
     * @param newEnd
     * @param newText
     * @return
     */
    public static String addNewTag(String oldManualAnnotation, String newType, String newStart,
                                   String newEnd, String newText) {
        String newTag = getNewTag(oldManualAnnotation);

        //检查待添加的手工标注是否已经存在
        String[] lines = oldManualAnnotation.split("\n");
        for (String line : lines) {
            if (line.contains(newText) && line.contains(newType)) {
                return oldManualAnnotation;
            }
        }

        String newLine = MessageFormat.format(AN_LINE_FORMAT, newTag, newType, newStart, newEnd,
            newText);
        return oldManualAnnotation + newLine;
    }

    /**
     * 删除标注中的指定标签,并且对手工标注标签进行重新排序
     * @param oldAnnotation
     * @param tag
     * @return
     */
    public static String deleteTag(String oldAnnotation, String tag) {
        if (StringUtils.isBlank(oldAnnotation)) {
            return "";
        }
        String newAnnotation = "";
        String[] lines = oldAnnotation.split("\n");
        int tagIndex = 1;
        for (String line : lines) {
            if (!line.contains(tag)) {
                String oldTag = line.split("\t")[0];
                line = line.replace(oldTag, "T" + tagIndex);
                newAnnotation = newAnnotation + line + "\n";
                tagIndex++;
            }
        }
        return newAnnotation;
    }

    /**
     * 在原有新词列表中,增加新词
     * @param oldTerms
     * @param newTerm
     * @param newTermType
     * @return
     */
    public static String addNewTerm(String oldTerms, String newTerm, String newTermType) {
        JSONArray termArray;
        if (StringUtils.isBlank(oldTerms)) {
            termArray = new JSONArray();
        }else{
            termArray = JSONArray.parseArray(oldTerms);
        }

        //检查是否已经存在要添加的新词,如果已经存在,直接返回
        for (Object object : termArray) {
            String oldTermType = JSONObject.parseObject(object.toString()).getString(newTerm);
            if (newTermType.equals(oldTermType)) {
                return oldTerms;
            }
        }

        //将新词添加到原有新词列表中
        JSONObject newTermObject = new JSONObject();
        newTermObject.put(newTerm, newTermType);
        termArray.add(newTermObject);
        return JSONArray.toJSONString(termArray);
    }

    /**
     * 从原有的新词列表中删除新词
     * @param oldTerms
     * @param newTerm
     * @param newTermType
     * @return
     */
    public static String deleteNewTerm(String oldTerms, String newTerm, String newTermType) {
        if (StringUtils.isBlank(oldTerms)) {
            return "";
        }

        JSONArray termArray = JSONArray.parseArray(oldTerms);

        JSONArray newTermArray = new JSONArray();

        //检查是否存在待删除的新词,如果存在,不再构建到新的新词列表中
        for (Object object : termArray) {
            String oldTermType = JSONObject.parseObject(object.toString()).getString(newTerm);
            if (!newTermType.equals(oldTermType)) {
                newTermArray.add(object);
            }
        }

        return JSONArray.toJSONString(newTermArray);
    }

    /**
     * 根据tag在手工标注中查找标注内容和标注类型
     * @param annotation
     * @param tag
     * @return
     */
    public static TermTypeVO getTermTypeVOByTag(String annotation, String tag) {

        String[] lines = annotation.split("\n");
        for (String line : lines) {
            if (line.contains(tag)) {
                String[] lineElements = line.split("\t");
                String term = lineElements[2];
                String termType = lineElements[1].split(" ")[0];
                TermTypeVO termTypeVO = new TermTypeVO();
                termTypeVO.setType(termType);
                termTypeVO.setTerm(term);
                return termTypeVO;
            }
        }
        return null;
    }

}
