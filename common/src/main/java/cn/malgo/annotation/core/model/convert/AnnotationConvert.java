package cn.malgo.annotation.core.model.convert;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import cn.malgo.annotation.common.util.log.LogUtil;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.malgo.annotation.common.dal.model.Annotation;
import cn.malgo.annotation.common.service.integration.apiserver.vo.TermTypeVO;
import cn.malgo.annotation.core.model.annotation.TermAnnotationModel;
import cn.malgo.core.definition.Document;
import cn.malgo.core.definition.utils.DocumentManipulator;
import org.apache.log4j.Logger;

/**
 * Created by 张钟 on 2017/10/20.
 */
public class AnnotationConvert {

    public static Logger logger = Logger.getLogger(AnnotationConvert.class);


    /**
     * 标注格式,样例如下
     * T3	Pharmaceutical-unconfirmed 8 10	卡铂
     */
    public static String AN_LINE_FORMAT = "{0}\t{1} {2} {3}\t{4}\n";

    public static JSONObject convertToBratFormat(Annotation annotation) {
        Document document = new Document(annotation.getTerm(), null);
        DocumentManipulator.parseBratAnnotations(annotation.getFinalAnnotation(), document);
        JSONObject result = DocumentManipulator.toBratAjaxFormat(document);
        return result;
    }
    public static JSONObject convertToBratFormat(String term,String finalAnnotation) {
        Document document = new Document(term, null);
        DocumentManipulator.parseBratAnnotations(finalAnnotation, document);
        JSONObject result = DocumentManipulator.toBratAjaxFormat(document);
        return result;
    }
    /**
     * 根据标注内容,生成新的标注标签
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
     *构建新的标准
     * @param oldAnnotation
     * @param newType
     * @param newStart
     * @param newEnd
     * @param newText
     * @return
     */
    public static  String addNewTagForAtomicTerm(String oldAnnotation, String newType, String newStart,
                                                 String newEnd, String newText){
        String newTag = getNewTag(oldAnnotation);

        List<TermAnnotationModel> termAnnotationModelList = AnnotationConvert
                .convertAnnotationModelList(oldAnnotation);
        //检查相同的手工标注是否已经存在
        Integer pNewStart=0,pNewEnd=0;
        for (TermAnnotationModel termAnnotationModel : termAnnotationModelList) {
            boolean isSameType = termAnnotationModel.getType().equals(newType);
            boolean isSameTerm = termAnnotationModel.getTerm().equals(newText);
            boolean isSameStart = termAnnotationModel.getStartPosition() == Integer
                    .valueOf(newStart);
            if(termAnnotationModel.getTerm().contains(newText)){
                pNewStart=termAnnotationModel.getStartPosition()+Integer.valueOf(newStart);
            }
            boolean isSameEnd = termAnnotationModel.getEndPosition() == Integer.valueOf(newEnd);
            if(termAnnotationModel.getTerm().contains(newText)){
                pNewEnd=termAnnotationModel.getStartPosition()+Integer.valueOf(newEnd);
            }
            if (isSameType && isSameTerm && isSameStart && isSameEnd) {
                return oldAnnotation;
            }
        }
        String newLine = MessageFormat.format(AN_LINE_FORMAT, newTag, newType, pNewStart.toString(), pNewEnd.toString(),
                newText);
        return oldAnnotation + newLine;
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

        List<TermAnnotationModel> termAnnotationModelList = AnnotationConvert
            .convertAnnotationModelList(oldManualAnnotation);

        //检查相同的手工标注是否已经存在
        for (TermAnnotationModel termAnnotationModel : termAnnotationModelList) {
            boolean isSameType = termAnnotationModel.getType().equals(newType);
            boolean isSameTerm = termAnnotationModel.getTerm().equals(newText);
            boolean isSameStart = termAnnotationModel.getStartPosition() == Integer
                .valueOf(newStart);
            boolean isSameEnd = termAnnotationModel.getEndPosition() == Integer.valueOf(newEnd);
            if (isSameType && isSameTerm && isSameStart && isSameEnd) {
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
        } else {
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

    /**
     * 参数格式如下(样例)
     * T1	Body-structure-unconfirmed 2 5	支气管
     * T2	Body-structure-unconfirmed 5 7	动脉
     * T3	Treatment-unconfirmed 9 11	化疗
     * T4	Treatment-unconfirmed 7 9	灌注
     * T5	Space-unconfirmed 0 1	左
     * T6	Procedure-unconfirmed 11 12	术
     * T7	Body-structure-unconfirmed 1 2	肺
     * @param text
     */
    public static List<TermAnnotationModel> convertAnnotationModelList(String text) {

        LogUtil.debug(logger,"待转换文本:"+text);

        List<TermAnnotationModel> termAnnotationModelList = new ArrayList<>();
        if (StringUtils.isBlank(text)) {
            return termAnnotationModelList;
        }

        String[] lines = text.split("\n");
        for (String line : lines) {
            TermAnnotationModel termAnnotationModel = new TermAnnotationModel();

            String[] elements = line.split("\t");
            termAnnotationModel.setTag(elements[0]);
            termAnnotationModel.setTerm(elements[2]);

            String[] structElement = elements[1].split(" ");
            termAnnotationModel.setType(structElement[0]);
            termAnnotationModel.setStartPosition(Integer.valueOf(structElement[1]));
            termAnnotationModel.setEndPosition(Integer.valueOf(structElement[2]));

            termAnnotationModelList.add(termAnnotationModel);
        }

        LogUtil.debug(logger,"转换结果:"+JSONObject.toJSONString(termAnnotationModelList));


        return termAnnotationModelList;
    }

    /**
     * 标注模型转换成文本
     * @param termAnnotationModelList
     * @return
     */
    public static String convertToText(List<TermAnnotationModel> termAnnotationModelList) {
        StringBuilder sb = new StringBuilder();
        for (TermAnnotationModel termAnnotationModel : termAnnotationModelList) {
            sb.append(convertToText(termAnnotationModel));
        }
        return sb.toString();
    }

    /**
     * 标注模型转换成文本
     * @param termAnnotationModel
     * @return
     */
    public static String convertToText(TermAnnotationModel termAnnotationModel) {
        String result = MessageFormat.format(AN_LINE_FORMAT, termAnnotationModel.getTag(),
            termAnnotationModel.getType(), termAnnotationModel.getStartPosition(),
            termAnnotationModel.getEndPosition(), termAnnotationModel.getTerm());
        return result;
    }

}
