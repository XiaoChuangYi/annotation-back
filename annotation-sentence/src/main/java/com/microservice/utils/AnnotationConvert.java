package com.microservice.utils;

import cn.malgo.core.definition.BratConst;
import cn.malgo.core.definition.Document;
import cn.malgo.core.definition.Entity;
import cn.malgo.core.definition.utils.DocumentManipulator;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.microservice.dataAccessLayer.entity.AnnotationSentence;
import com.microservice.dataAccessLayer.entity.AnnotationSentenceExercise;
import com.microservice.dataAccessLayer.entity.UserExercises;
import com.microservice.vo.AnnotationSentExerciseBratVO;
import com.microservice.vo.AnnotationSentenceBratVO;
import com.microservice.vo.UserExercisesBratVO;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by cjl on 2018/4/16.
 */
public class AnnotationConvert {


    public static Logger logger = Logger.getLogger(AnnotationConvert.class);


    /**
     * 文本内容转化为brat格式的且数据返回格式是jsonObject
     * @param originTerm
     * @param annotationData
     */
    public static JSONObject convertToBratFormat(String originTerm,String annotationData){
        Document document=new Document(originTerm,null);
        DocumentManipulator.parseBratAnnotations(annotationData==null?"":annotationData,document);
        JSONObject jsonObject=DocumentManipulator.toBratAjaxFormat(document);
        List<Integer> endPositionList=document.getEntities().stream()
            .filter(x -> !x.getType().endsWith("-deleted"))
            .map(x->x.getEnd()).sorted().collect(Collectors.toList());

        List<Integer> startPositionList = endPositionList.stream().collect(Collectors.toList());
        if (endPositionList.size() == 0 || endPositionList.get(endPositionList.size()-1) < document.getText().length()){
            endPositionList.add(document.getText().length());
        }else{
            startPositionList.remove(startPositionList.size()-1);
        }
        if (startPositionList.size() == 0 || startPositionList.get(0) != 0){

            startPositionList.add(0,0);
        }

        jsonObject.put(BratConst.SENTENCE_OFFSET, IntStream.range(0, endPositionList.size())
                .mapToObj(i -> Arrays.asList(startPositionList.get(i), endPositionList.get(i)))
                .collect(Collectors.toList()));

        jsonObject.put(BratConst.TOKEN_OFFSET, IntStream.range(0, document.getText().length())
                .mapToObj(i -> Arrays.asList(i, i+1))
                .collect(Collectors.toList()));
        return jsonObject;

    }

    /**
     *  将标注数据部分字段转换成前端可以渲染的数据格式
     */
    public static List<AnnotationSentExerciseBratVO> convert2AnnotationSentBratVOList(List<AnnotationSentenceExercise> annotationSentenceExerciseList){
        List<AnnotationSentExerciseBratVO> annotationSentExerciseBratVOList=new LinkedList<>();

        for(AnnotationSentenceExercise current:annotationSentenceExerciseList){
            JSONObject autoBratJson=convertToBratFormat(current.getOriginText(),current.getAutoAnnotation());
            JSONObject standardBratJson=convertToBratFormat(current.getOriginText(),current.getStandardAnnotation());
            AnnotationSentExerciseBratVO annotationSentExerciseBratVO=new AnnotationSentExerciseBratVO();
            BeanUtils.copyProperties(current,annotationSentExerciseBratVO);
            annotationSentExerciseBratVO.setAutoBratData(autoBratJson);
            annotationSentExerciseBratVO.setStandardBratData(standardBratJson);
            annotationSentExerciseBratVOList.add(annotationSentExerciseBratVO);
        }
        return annotationSentExerciseBratVOList;
    }

    /**
     * 将标注数据部分字段转换成前端可以渲染的数据格式
     */
    public static AnnotationSentExerciseBratVO convert2AnnotationSentBratVO(AnnotationSentenceExercise annotation){
        JSONObject autoBratJson=convertToBratFormat(annotation.getOriginText(),annotation.getAutoAnnotation());
        JSONObject standardBratJson=convertToBratFormat(annotation.getOriginText(),annotation.getStandardAnnotation());
        AnnotationSentExerciseBratVO annotationSentExerciseBratVO=new AnnotationSentExerciseBratVO();
        BeanUtils.copyProperties(annotation,annotationSentExerciseBratVO);
        annotationSentExerciseBratVO.setAutoBratData(autoBratJson);
        annotationSentExerciseBratVO.setStandardBratData(standardBratJson);
        return annotationSentExerciseBratVO;
    }

    /**
     *  将标注数据部分字段转换成前端可以渲染的数据格式
     */
    public static List<UserExercisesBratVO> convert2UserExercisesBratVOList(List<UserExercises> userExercisesList){
        List<UserExercisesBratVO> annotationSentExerciseBratVOList=new LinkedList<>();

        for(UserExercises current:userExercisesList){
            JSONObject practiceBratJson=convertToBratFormat(current.getOriginText(),current.getPracticeAnnotation());
            JSONObject standardBratJson=convertToBratFormat(current.getOriginText(),current.getStandardAnnotation());
            UserExercisesBratVO annotationSentExerciseBratVO=new UserExercisesBratVO();
            BeanUtils.copyProperties(current,annotationSentExerciseBratVO);
            annotationSentExerciseBratVO.setPracticeBratData(practiceBratJson);
            annotationSentExerciseBratVO.setStandardBratData(standardBratJson);
            boolean correct=compareAnnotation(current.getPracticeAnnotation(),current.getStandardAnnotation());
            if(correct)
                annotationSentExerciseBratVO.setMemo("正确");
            else
                annotationSentExerciseBratVO.setMemo("有问题");

            annotationSentExerciseBratVOList.add(annotationSentExerciseBratVO);
        }
        return annotationSentExerciseBratVOList;
    }


    /**
     * 将标注数据部分字段转换成前端可以渲染的数据格式
     */
    public static UserExercisesBratVO convert2UserExercisesBratVO(UserExercises annotation){
        JSONObject practiceBratJson=convertToBratFormat(annotation.getOriginText(),annotation.getPracticeAnnotation());
        JSONObject standardBratJson=convertToBratFormat(annotation.getOriginText(),annotation.getStandardAnnotation());
        UserExercisesBratVO annotationSentExerciseBratVO=new UserExercisesBratVO();
        BeanUtils.copyProperties(annotation,annotationSentExerciseBratVO);
        annotationSentExerciseBratVO.setPracticeBratData(practiceBratJson);
        annotationSentExerciseBratVO.setStandardBratData(standardBratJson);
        return annotationSentExerciseBratVO;
    }


    /**
     * 将标注数据部分字段转换成前端可以渲染的数据格式
     */
    public static List<AnnotationSentenceBratVO> convert2AnnotationBratVOList(List<AnnotationSentence> annotationList){
        List<AnnotationSentenceBratVO> annotationBratVOList=new LinkedList<>();
        for(AnnotationSentence annotation:annotationList){
            JSONObject bratJson=convertToBratFormat(annotation.getOriginText(),annotation.getAnnotationText());
            JSONObject finalBratJson=convertToBratFormat(annotation.getOriginText(),annotation.getFinalAnnotationText());
            AnnotationSentenceBratVO annotationSentenceBratVO=new AnnotationSentenceBratVO();
            BeanUtils.copyProperties(annotation,annotationSentenceBratVO);
            annotationSentenceBratVO.setBratData(bratJson);
            annotationSentenceBratVO.setFinalBratData(finalBratJson);
            annotationBratVOList.add(annotationSentenceBratVO);
        }
        return annotationBratVOList;
    }

    /**
     * 将标注数据部分字段转换成前端可以渲染的数据格式
     */
    public static AnnotationSentenceBratVO convert2AnnotationBratVO(AnnotationSentence annotation){
        JSONObject bratJson=convertToBratFormat(annotation.getOriginText(),annotation.getAnnotationText());
        JSONObject finalBratJson=convertToBratFormat(annotation.getOriginText(),annotation.getFinalAnnotationText());
        AnnotationSentenceBratVO annotationSentenceBratVO=new AnnotationSentenceBratVO();
        BeanUtils.copyProperties(annotation,annotationSentenceBratVO);
        annotationSentenceBratVO.setBratData(bratJson);
        annotationSentenceBratVO.setFinalBratData(finalBratJson);
        return annotationSentenceBratVO;
    }

    /**
     * 通过lambda添加新的单位标注从而构建新标注
     * @param oldAnnotation
     * @param newType
     * @param newStart
     * @param newEnd
     * @param newTerm
     * @return
     */
    public static String addUnitAnnotation(String oldAnnotation,String newType,int newStart,
                                                   int newEnd,String newTerm){
        Document document=new Document("",new LinkedList<>());
        DocumentManipulator.parseBratAnnotations(oldAnnotation==null?"":oldAnnotation,document);
        List<Entity> entityList=document.getEntities();
        int num=entityList.size()>0 ? entityList.stream()
            .map(x->x.getTag().substring(1,x.getTag().length()))
            .map(s -> Integer.valueOf(s))
            .max(Comparator.comparing(Function.identity())).get().intValue()+1:1;
        String newTag= "T"+num;

        if(document.getEntities().stream().filter(x->x.getTerm().equals(newTerm)&&x.getType().equals(newType)
                &&x.getStart()==newStart&&x.getEnd()==newEnd)
                .count()>0){
            return oldAnnotation; // 已存在同样的标注就不添加 TODO 有隐患.
        }else{
            document.getEntities().add(new Entity(newTag,newStart,newEnd,newType,newTerm));
            return DocumentManipulator.toBratAnnotations(document);
        }
    }

    /**
     *根据tag删除标注中指定的单位标注
     * @param tag
     * @param oldAnnotation
     */
    public static String deleteUnitAnnotation(String oldAnnotation,String tag){
        Document document=new Document("",new LinkedList<>());
        DocumentManipulator.parseBratAnnotations(oldAnnotation,document);
        document.setEntities(document.getEntities().stream()
                .filter(x->!x.getTag().equals(tag))
                .collect(Collectors.toList()));
        logger.info("删除后的标注："+ JSONArray.parseArray(JSON.toJSONString(document.getEntities())));
        return DocumentManipulator.toBratAnnotations(document);
    }

    /**
     * 通过lambda实现，更新原先标注中指定的单位标注类型
     * @param oldAnnotation
     * @param  oldType
     * @param newType
     * @param tag
     */
    public static String updateUnitAnnotationTypeByLambda(String oldAnnotation,String oldType,String newType,String tag) {
        Document document = new Document("", new LinkedList<>());
        DocumentManipulator.parseBratAnnotations(oldAnnotation == null ? "" : oldAnnotation, document);
        if (document.getEntities().size() > 0)
            document.getEntities().stream().filter(x -> x.getTag().equals(tag) && x.getType().equals(oldType))
                    .forEach(e -> e.setType(newType));
        logger.info("更新后的标注：" + JSONArray.parseArray(JSON.toJSONString(document.getEntities())));
        return DocumentManipulator.toBratAnnotations(document);
    }

    /**
     * 修改tag对应的标注类型
     * @param oldAnnotation
     * @param tag
     * @return
     */
    public static String changeUnitAnnotation(String oldAnnotation,String tag, String type){
        Document document=new Document("",new LinkedList<>());
        DocumentManipulator.parseBratAnnotations(oldAnnotation,document);
        document.getEntities().stream().filter(x -> x.getTag().equals(tag)).forEach(x -> x.setType(type));
        return DocumentManipulator.toBratAnnotations(document);
    }

    /**
     * @param sourceAnnotation practiceAnnotation
     * @param targetAnnotation standardAnnotation
     *
     **/
    private static boolean compareAnnotation(String sourceAnnotation,String targetAnnotation){
        Document documentSource=new Document("",new LinkedList<>());
        DocumentManipulator.parseBratAnnotations(sourceAnnotation==null?"":sourceAnnotation,documentSource);
        Document documentTarget=new Document("",new LinkedList<>());
        DocumentManipulator.parseBratAnnotations(targetAnnotation==null?"":targetAnnotation,documentTarget);
        if(documentTarget.getEntities().size()!=documentSource.getEntities().size())
            return false;
        if(documentTarget.getEntities().size()>0) {
            for (Entity entity : documentTarget.getEntities()) {
                //练习人员未标注，也直接返回false
                if(documentSource.getEntities().size()==0)
                    return false;
                //练习人员的标注与标准答案作比较，没有匹配到的，则有不一样的标注
                long num = documentSource.getEntities().stream().filter(x -> x.getType().equals(entity.getType()) && x.getStart() == entity.getStart() && x.getEnd() == entity.getEnd()&&x.getTerm().equals(entity.getTerm())).count();
                if (num == 0)
                    return false;
            }
        }else{
            return false;
        }
        return true;
    }
}
