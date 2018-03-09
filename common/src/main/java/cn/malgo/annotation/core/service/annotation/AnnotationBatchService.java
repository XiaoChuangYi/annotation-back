package cn.malgo.annotation.core.service.annotation;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import cn.malgo.annotation.common.dal.model.Annotation;
import cn.malgo.annotation.core.business.annotation.AnnotationPagination;
import cn.malgo.annotation.core.business.antomicTerm.CombineAtomicTerm;
import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.core.business.annotation.AtomicTermAnnotation;
import cn.malgo.core.definition.utils.DocumentManipulator;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.sun.tools.hat.internal.util.Comparer;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;

import cn.malgo.annotation.common.dal.mapper.AnAtomicTermMapper;
import cn.malgo.annotation.common.dal.model.AnAtomicTerm;
import cn.malgo.annotation.common.util.log.LogUtil;
import cn.malgo.annotation.core.business.annotation.TermAnnotationModel;
import cn.malgo.annotation.core.tool.check.AnnotationChecker;
import cn.malgo.annotation.core.tool.convert.AnnotationConvert;
import cn.malgo.annotation.core.tool.enums.annotation.AnnotationStateEnum;
import cn.malgo.common.security.SecurityUtil;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.Document;

/**
 * @author 张钟
 * @date 2017/11/2
 */
@Service
public class AnnotationBatchService extends AnnotationService {

    private Logger logger = Logger.getLogger(AnnotationBatchService.class);

    @Autowired
    private AnAtomicTermMapper anAtomicTermMapper;

    /**
     * 构建原子术语的map
     * @return
     */
    public Map<String, AnAtomicTerm> buildAtomicTermMap() {
        List<AnAtomicTerm> anAtomicTermList = anAtomicTermMapper.selectAll();
        LogUtil.info(logger, "待检查原子词条总数:" + anAtomicTermList.size());

        //构造成map,提高处理性能
        Map<String, AnAtomicTerm> anAtomicTermMap = new HashMap<>(anAtomicTermList.size());
        for (AnAtomicTerm anAtomicTerm : anAtomicTermList) {
            String key = SecurityUtil.decryptAESBase64(anAtomicTerm.getTerm()) + ":"
                    + anAtomicTerm.getType();
            anAtomicTermMap.put(key, anAtomicTerm);
        }
        return anAtomicTermMap;
    }
    /**
     * 批量,全量检查标注的二义性
     */
    public void batchCheckAmbiguityAndAtomicTerm(List<String> stateList) {

        LogUtil.info(logger, "开全量检查标注的二义性,以及标注中的原子术语是否存在于原子术语表");

        Map<String, AnAtomicTerm> anAtomicTermMap = buildAtomicTermMap();

        int pageNum = 1;
        int pageSize = 10;
        Page<Annotation> pageInfo = null;
        do {

            pageInfo = listAnnotationByStateList(stateList, pageNum, pageSize);
            LogUtil.info(logger,
                "开始处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次");

            for (Annotation annotation : pageInfo.getResult()) {
                LogUtil.info(logger, "开始处理的标注ID:" + annotation.getId() + ";标注内容:"
                                     + annotation.getTerm());
                checkAmbiguityAndAtomicTermExist(annotation, anAtomicTermMap);
            }

            LogUtil.info(logger,
                "结束处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次");
            pageNum++;

        } while (pageInfo.getPages() >= pageNum);

    }

    /**
     *批量解密annotation
     */
    public  void batchDecryptAnnotation(){
       List<Annotation> annotationList=annotationMapper.selectAll();
       decryptAES(annotationList);
       for(Annotation currentAnnotation:annotationList) {
           LogUtil.info(logger, "开始解密第" + currentAnnotation.getId() + "标注");
           try {
               annotationMapper.updateByPrimaryKeySelective(currentAnnotation);
           } catch (Exception e) {
             LogUtil.info(logger,"解密失败anId"+currentAnnotation.getId());
           }
       }
    }

    /**
     * 更新标注
     */
    public void updateUNEncryptedAnnotation(String userId) {

        int batchCount = 1;
        Page<Annotation> page = null;
        do {
            LogUtil.info(logger, "开始处理第" + batchCount + "批次");
            page = listAnnotationUNEncrypted(AnnotationStateEnum.UN_ENCRYPTED.name(), userId, 1, 10);
            Annotation annotation_temp = null;
            try {
                for (Annotation annotation : page.getResult()) {
                    annotation_temp = annotation;
                    annotation.setState(AnnotationStateEnum.FINISH.name());
                    cryptAnnotationAndUpdate(annotation);
                }
            } catch (Exception e) {
                LogUtil.info(logger, "加密失败anId" + annotation_temp.getId());
            }
            LogUtil.info(logger, "结束处理第" + batchCount + "批次,剩余:" + (page.getTotal() - 10));
            batchCount++;
        } while (page.getTotal() > 10);
    }

    /**
     * 检查标注是否存在歧义,以及是否存在对应的原子术语
     * @param annotation
     * @param anAtomicTermMap
     */
    public void checkAmbiguityAndAtomicTermExist(Annotation annotation,
                                                 Map<String, AnAtomicTerm> anAtomicTermMap) {
        try {

            Annotation annotationNew = new Annotation();
            BeanUtils.copyProperties(annotation, annotationNew);

            //检查标注中的原子术语是否存在于原子术语表中
            List<TermAnnotationModel> termAnnotationModelList = AnnotationConvert
                .convertAnnotationModelList(annotation.getFinalAnnotation());

            //词库中存在对应的词条,检查词条是否存在来源,如果不存在,将当前标注作为该原子词条的来源
            //用来记录当前词条的所有标注是否有对应的原子术语,默认是有对应
            boolean isUnRecognize = false;
            for (TermAnnotationModel termAnnotationModel : termAnnotationModelList) {

                //只针对既存在于原子术语表中,同时也无二义性的标注节点做确认,同时将替换最终标注中的unconfirmed
                //检查标注的词条是否存在于原子术语表中
                String key = termAnnotationModel.getTerm() + ":" + termAnnotationModel.getType()
                    .replace(AnnotationChecker.UN_CONFIRMED, "");
                boolean existAtomicTerm = anAtomicTermMap.get(key) != null;

                //检查是否存在二义性
                boolean hasAmbiguity = AnnotationChecker.hasAmbiguity(termAnnotationModel,
                    termAnnotationModelList);

                //存在原子术语表的对应,同时不存在歧义,进行确认,否则,不进行确认,同时整条标注标记为无法识别
                if (existAtomicTerm && !hasAmbiguity) {
                    termAnnotationModel.setType(
                        termAnnotationModel.getType().replace(AnnotationChecker.UN_CONFIRMED, ""));

                    //添加经过确认的标注到手工标注中
                    String manualAnnotation = AnnotationConvert.addNewTag(
                        annotationNew.getManualAnnotation(), termAnnotationModel.getType(),
                        String.valueOf(termAnnotationModel.getStartPosition()),
                        String.valueOf(termAnnotationModel.getEndPosition()),
                        termAnnotationModel.getTerm());
                    annotationNew.setManualAnnotation(manualAnnotation);

                } else {
                    isUnRecognize = true;
                }

            }

            //存在二义性,或者存在未出现在原子术语表中的标注
            if (isUnRecognize) {
                annotationNew.setState(AnnotationStateEnum.INCONSISTENT.name());
            }

            LogUtil.info(logger, "手工标注内容:" + annotationNew.getManualAnnotation());

            //设置经过确认的最终标注
            annotationNew
                .setFinalAnnotation(AnnotationConvert.convertToText(termAnnotationModelList));

            //更新标注
            cryptAnnotationAndUpdate(annotationNew);

        } catch (Exception e) {
            LogUtil.info(logger, "检查标注二义性和原子术语对应关系异常,标注ID:" + annotation.getId());
        }
    }
    public void batchReplaceUnitAnnotationType(String typeOld, String typeNew) {
        LogUtil.info(logger, "开始批量替换标注表单位标注中的type类型");
        int pageNum = 1;
        int pageSize = 1000;
        Page<Annotation> pageInfo = null;
        List<Annotation> finalAnnotation=new LinkedList<>();
        int total=countAnnotationSizeByState(null);
        int endPageIndex=total/pageSize;
        if(total%pageSize>0){
            endPageIndex++;
        }
        for(;pageNum<=endPageIndex;pageNum++){
            pageInfo=listAnnotationByStateList(null,pageNum,pageSize);
            LogUtil.info(logger,
                    "开始处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次，剩余共"+(total-pageSize*pageNum)+"条记录");
            for (Annotation annotation : pageInfo.getResult()){
                try {
                    //手动标注
                    List<TermAnnotationModel> manualModelList = AnnotationConvert
                            .convertAnnotationModelList(annotation.getManualAnnotation());
                    //最终标注
                    List<TermAnnotationModel> finalModelList = AnnotationConvert
                            .convertAnnotationModelList(annotation.getFinalAnnotation());
                    boolean isAdd=false;
                    for (TermAnnotationModel currentModel : finalModelList) {
                        //如果标注中存在待替换的type类型,进行替换
                        if (currentModel.getType().equals(typeOld)) {
                            currentModel.setType(typeNew);
                            isAdd=true;
                        }
                    }
                    for (TermAnnotationModel currentModel : manualModelList) {
                        //如果标注中存在待替换的type类型,进行替换
                        if (currentModel.getType().equals(typeOld)) {
                            currentModel.setType(typeNew);
                            isAdd=true;
                        }
                    }
                    if(isAdd) {
                        annotation.setGmtModified(new Date());
                        annotation.setManualAnnotation(AnnotationConvert.convertToText(manualModelList));
                        annotation.setFinalAnnotation(AnnotationConvert.convertToText(finalModelList));
//                    annotation.setManualAnnotation(SecurityUtil.cryptAESBase64(AnnotationConvert.convertToText(manualModelList)));
//                    annotation.setFinalAnnotation(SecurityUtil.cryptAESBase64(AnnotationConvert.convertToText(finalModelList)));
                        finalAnnotation.add(annotation);
                    }
                }catch (Exception ex){
                    LogUtil.info(logger,
                            "结束处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次");
                }
            }
        }
        if(finalAnnotation.size()>0){
            LogUtil.info(logger, "存在带替换的标注术语,共:" + finalAnnotation.size()+"条需要处理");
            updateBatchAnnotation(finalAnnotation);
        }
    }
    /**
     * 批量合并包含指定单位标注(term/type)组标注数据
     */
    public void batchCombineUnitAnnotation(List<String> idList, List<CombineAtomicTerm> combineAtomicTermList, String newTerm, String newType){
        LogUtil.info(logger, "开始批量替换标注中的原子术语");
        //通过前台筛选的ids查询所有的符合标注的数据
        List<Annotation> annotationList = listAnnotationByAnIds(idList);
        for (Annotation annotation : annotationList){
            try {
                //部分数字文本带有小数点，因此变换逻辑
                long count=combineAtomicTermList.stream()
                        .filter(x->annotation.getTerm().contains(x.getTerm()))
                        .count();
                if(combineAtomicTermList.size()==count){
                    //为了生成新的标注，并删掉原来的标注，关键需要知道替换的startPosition和endPosition
                    //有些文本显示是前后顺序的，Tag标签不一定也是同样的前后顺序
//                    List<TermAnnotationModel> termAnnotationModelList = AnnotationConvert
//                            .convertAnnotationModelList(annotation.getFinalAnnotation());
//                    List<Integer> startPositionList=new ArrayList<>();
//                    List<Integer> endPositionList=new ArrayList<>();
//                    for(TermAnnotationModel termAnnotationModel:termAnnotationModelList){
//                        for(CombineAtomicTerm combineAtomicTerm:combineAtomicTermList) {
//
//                            if (combineAtomicTerm.getTerm().toUpperCase().equals(termAnnotationModel.getTerm().toUpperCase())&&
//                                    combineAtomicTerm.getType().equals(termAnnotationModel.getType())){
//                                System.out.println("combineAtomicTerm"+combineAtomicTerm.getTerm().toUpperCase()+";termAnnotationModel"+termAnnotationModel.getTerm().toUpperCase());
//                                startPositionList.add(termAnnotationModel.getStartPosition());
//                                endPositionList.add(termAnnotationModel.getEndPosition());
//                            }
//                        }
//                    }
//                    Collections.sort(startPositionList);
//                    Collections.sort(endPositionList);
//                    //对两个集合取交集，然后各个集合删除交集的部分，即为最终需要的数据
//                    List<Integer> commonList=new ArrayList<>();
//                    commonList.addAll(startPositionList);
//                    commonList.retainAll(endPositionList);
//                    startPositionList.removeAll(commonList);
//                    endPositionList.removeAll(commonList);
//                    System.out.println("去交集后"+ JSONArray.parse(JSON.toJSONString(startPositionList)));
//                    System.out.println("去交集后"+JSONArray.parse(JSON.toJSONString(endPositionList)));
//                    String finalAnnotationOld=AnnotationConvert.convertToText(termAnnotationModelList);
//                    if(startPositionList.size()>0){
//                        for(int k=0;k<startPositionList.size();k++){
//                            finalAnnotationOld=AnnotationConvert.addNewTag(finalAnnotationOld,newType,startPositionList.get(k).toString(),
//                                    endPositionList.get(k).toString(),newTerm);
//                        }
//                    }
//                    System.out.println("finalAnnotationOld："+finalAnnotationOld);
//                    List<TermAnnotationModel> termAnnotationModelListNew = AnnotationConvert
//                            .convertAnnotationModelList(finalAnnotationOld);
//                    Iterator<TermAnnotationModel> iterator=termAnnotationModelListNew.iterator();
//                    //合并术语时，删除原先的术语
//                    while (iterator.hasNext()){
//                        TermAnnotationModel termAnnotationModel=iterator.next();
//                        for(CombineAtomicTerm combineAtomicTerm:combineAtomicTermList){
//                            if(combineAtomicTerm.getTerm().toUpperCase().equals(termAnnotationModel.getTerm().toUpperCase())
//                                    &&combineAtomicTerm.getType().equals(termAnnotationModel.getType())){
//                                System.out.println("combineAtomicTerm："+combineAtomicTerm.getTerm().toUpperCase()+" ; termAnnotationModel："+termAnnotationModel.getTerm().toUpperCase());
//                                iterator.remove();
//                            }
//                        }
//                    }
//                    String finalAnnotationNew=AnnotationConvert.convertToText(termAnnotationModelListNew);
                    String finalAnnotationNew=AnnotationConvert.getAnnotationAfterCombineAnnotationByLambda(
                            combineAtomicTermList,annotation,newTerm,newType);
                    System.out.println("finalAnnotationNew："+finalAnnotationNew);
                    LogUtil.info(logger, "存在带替换的标注记录:" + annotation.getId());
                    updateFinalAnnotation(annotation.getId(), finalAnnotationNew);
                }
            }
            catch(Exception ex) {
                LogUtil.info(logger, "替换标注中的原子术语失败,标注ID:" + annotation.getId());
            }
        }
    }
    /**
     * 继续拆分标注数据中含有指定单位标注组的单位标注
     */
    public void batchSubdivideUnitAnnotation(List<AtomicTermAnnotation> atomicTermAnnotationList) {
        LogUtil.info(logger, "开始批量替换标准中的原子术语");
        int pageNum = 1;
        int pageSize = 2000;
        Page<Annotation> pageInfo = null;
        List<String> stateList = new ArrayList<>();
        stateList.add(AnnotationStateEnum.FINISH.name());
        String originText=atomicTermAnnotationList.stream()
                .sorted(Comparator.comparing(x->x.getStartPosition()))
                .map(
                (x)-> x.getText()).collect(Collectors.joining(""));
        System.out.println("originText:"+originText);
        do {
            //根据finish状态到术语标注表中查询
            pageInfo = listAnnotationByStateList(stateList, pageNum, pageSize);
            LogUtil.info(logger,
                    "开始处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次");
            for (Annotation annotation : pageInfo.getResult()) {
                try {
                    if(AnnotationConvert.getUnitAnnotationList(annotation.getFinalAnnotation()).stream()
                            .filter(x->x.getTerm().equals(originText)).count()>0){
                        LogUtil.info(logger, "存在带替换的标注记录:" + annotation.getId());
                        String newAnnotation=AnnotationConvert.getAnnotationAfterDivideUnitAnnotation(atomicTermAnnotationList,annotation.getFinalAnnotation(),originText);
                        updateFinalAnnotation(annotation.getId(),newAnnotation);
                        LogUtil.info(logger, "标注数据:" + newAnnotation);
                    }

//                    List<TermAnnotationModel> termAnnotationModelList = AnnotationConvert
//                            .convertAnnotationModelList(annotation.getFinalAnnotation());
//                    //是否发生替换的标志
//                    boolean isChange = false;
//                    //新拆分的原子术语集合跟标注中的原子术语做匹配，如果符合，说明原先标注中已经有该原子术语，说明拆分不合理
//                    //只要有一个满足，则不进行拆分，不更新标注
//                    Iterator<TermAnnotationModel> iterator=termAnnotationModelList.iterator();
//                    while (iterator.hasNext()){
//                        TermAnnotationModel termAnnotationModel=iterator.next();
//                        //如果标注中存在待替换的原子术语,设置为false,不用进行替换
//                        //前台拆分的原子术语当中,如果当前标注中有一个匹配到是符合的,则没有拆分的必要
//                        if (termAnnotationModel.getTerm().equals(originText)) {
//                            System.out.println("currentTerm:"+termAnnotationModel.getTerm());
//                            isChange = true;
//                        }
//                    }
//                    if (isChange) {
//                        String finalAnnotationNew=annotation.getFinalAnnotation();
//                        for(AtomicTermAnnotation atomicTermAnnotationModel: atomicTermAnnotationList){
//                            finalAnnotationNew = AnnotationConvert.addNewTagForAtomicTerm(
//                                    finalAnnotationNew,atomicTermAnnotationModel.getAnnotationType(),
//                                    atomicTermAnnotationModel.getStartPosition(),atomicTermAnnotationModel.getEndPosition(),atomicTermAnnotationModel.getText());
//                        }
//                        //添加完新的标签后，删除老的标签
//                        List<TermAnnotationModel> newTermAnnotationModelList = AnnotationConvert
//                                .convertAnnotationModelList(finalAnnotationNew);
//                        Iterator<TermAnnotationModel> iterator1=newTermAnnotationModelList.iterator();
//                        String newAtomicStr=atomicTermAnnotationList.get(0).getText();
//                        while (iterator1.hasNext()){
//                            TermAnnotationModel termAnnotationModel=iterator1.next();
//                            if(!termAnnotationModel.getTerm().equals(newAtomicStr)&&termAnnotationModel.getTerm().contains(newAtomicStr)){
//                                iterator1.remove();
//                            }
//                        }
//                        LogUtil.info(logger, "存在带替换的标注记录:" + annotation.getId());
//                        updateFinalAnnotation(annotation.getId(), AnnotationConvert.convertToText(newTermAnnotationModelList));
//                    }

                } catch (Exception e) {
                    LogUtil.info(logger, "替换标注中的原子术语失败,标注ID:" + annotation.getId());
                }
            }
            LogUtil.info(logger,
                    "结束处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次");
            pageNum++;

        } while (pageInfo.getPages() >= pageNum);
    }

    /**
     *批量删除符合当前原子术语的annotation表中的最终标注字段，并且删除原子术语表中对应的记录
     * @param id
     * @param term
     * @param type
     */
    @Transactional
    public void deleteAtomicTermAndUnitAnnotation(String id,String term,String type){
        int deleteResult=anAtomicTermMapper.deleteByPrimaryKey(id);
        AssertUtil.state(deleteResult > 0, "删除术语失败");
        LogUtil.info(logger, "开始批量删除annotation表中符合当前原子术语的标注");
        Page<Annotation> pageInfo=null;
        int pageNum=1;
        int pageSize=2000;
        do{
            pageInfo=listAnnotationByStateList(null,pageNum,pageSize);
            LogUtil.info(logger,
                    "开始处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次");

            for (Annotation annotation : pageInfo.getResult()) {
                try {
                    List<TermAnnotationModel> termAnnotationModelList = AnnotationConvert
                            .convertAnnotationModelList(annotation.getFinalAnnotation());
                    String newFinalAnnotation="";
                    boolean isChange = false;
                    //用来记录当前词条的所有标注是否有对应的原子术语,默认是有对应
                    for (TermAnnotationModel termAnnotationModel : termAnnotationModelList) {
                        //如果标注中纯在待替换的原子术语,进行替换
                        if (termAnnotationModel.getTerm().equals(term)
                                && termAnnotationModel.getType().equals(type)) {
                            isChange=true;
//                            newFinalAnnotation= AnnotationConvert.deleteTag(annotation.getFinalAnnotation(),termAnnotationModel.getTag());
                            newFinalAnnotation=AnnotationConvert.deleteUnitAnnotationByLambda(annotation.getFinalAnnotation(),
                                    termAnnotationModel.getTag());
                        }
                    }
                    if(isChange) {
                        LogUtil.info(logger, "存在带替换的标注记录:" + annotation.getId());
                          updateFinalAnnotation(annotation.getId(), newFinalAnnotation);
                    }
                }catch (Exception e) {
                    LogUtil.info(logger, "删除标注中的原子术语失败,标注ID:" + annotation.getId());
                }
            }
            LogUtil.info(logger,
                    "结束处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次");
            pageNum++;
        }while (pageInfo.getPages() >= pageNum);
    }
    /**
     * 批量替换标注字段中的单位标注
     * @param anAtomicTermOld
     * @param anAtomicTermNew
     */
    public void batchReplaceUnitAnnotation(AnAtomicTerm anAtomicTermOld, AnAtomicTerm anAtomicTermNew) {

        LogUtil.info(logger, "开始批量替换标准中的原子术语");
        int pageNum = 1;
        int pageSize = 2000;
        Page<Annotation> pageInfo = null;
        List<String> stateList = new ArrayList<>();
        stateList.add(AnnotationStateEnum.FINISH.name());
        do {
            //根据finish状态到术语标注表中查询
            pageInfo = listAnnotationByStateList(stateList, pageNum, pageSize);

            LogUtil.info(logger,
                    "开始处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次");

            for (Annotation annotation : pageInfo.getResult()) {
                try {
                    List<TermAnnotationModel> termAnnotationModelList = AnnotationConvert
                            .convertAnnotationModelList(annotation.getFinalAnnotation());

                    //是否发生替换的标志
                    boolean isChange = false;
                    //用来记录当前词条的所有标注是否有对应的原子术语,默认是有对应
                    for (TermAnnotationModel termAnnotationModel : termAnnotationModelList) {

                        //如果标注中纯在待替换的原子术语,进行替换
                        if (termAnnotationModel.getTerm().equals(anAtomicTermOld.getTerm())
                                && termAnnotationModel.getType().equals(anAtomicTermOld.getType())) {
                            termAnnotationModel.setTerm(anAtomicTermNew.getTerm());
                            termAnnotationModel.setType(anAtomicTermNew.getType());
                            isChange = true;
                        }
                    }

                    if (isChange) {

                        LogUtil.info(logger, "存在带替换的原子术语:" + annotation.getId());
                        String newText = AnnotationConvert.convertToText(termAnnotationModelList);
                        updateFinalAnnotation(annotation.getId(), newText);
                    }

                } catch (Exception e) {
                    LogUtil.info(logger, "替换标注中的原子术语失败,标注ID:" + annotation.getId());
                }
            }
            LogUtil.info(logger,
                    "结束处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次");
            pageNum++;

        } while (pageInfo.getPages() >= pageNum);
    }
    /**
     * 根据type/term集合查询标术语注表中的标注
     * @param combineAtomicTermList
     */
    public List<Annotation> listAnnotationByUnitAnnotationArr(List<CombineAtomicTerm> combineAtomicTermList) {
        int pageNum = 1;
        int pageSize = 2000;
        List<Annotation> finalAnnotation = new ArrayList<>();
        Page<Annotation> pageInfo = null;
        Set<String> setAtomicTerm=new HashSet<>();
        do {
            pageInfo = listAnnotationByStateList(null, pageNum, pageSize);
            LogUtil.info(logger,
                    "开始处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次");
            for (Annotation annotation : pageInfo.getResult()) {
                try {
                    List<TermAnnotationModel> finalModelList = AnnotationConvert
                            .convertAnnotationModelList(annotation.getFinalAnnotation());
                    setAtomicTerm.clear();
                    for (TermAnnotationModel currentModel : finalModelList) {
                        //如果参数传入的原子术语集合都能匹配到当前标注中的原子术语,则加入返回的集合
                        for(CombineAtomicTerm combineAtomicTerm:combineAtomicTermList) {
                            if (currentModel.getType().equals(combineAtomicTerm.getType()) && currentModel.getTerm().equals(combineAtomicTerm.getTerm())) {
                                setAtomicTerm.add(currentModel.getTerm());
                            }
                        }
                    }
                    if (setAtomicTerm.size()==combineAtomicTermList.size()) {
                        finalAnnotation.add(annotation);
                        LogUtil.info(logger, "存在带替换的标注术语为:" + annotation.getId());
                    }
                } catch (Exception ex) {
                    LogUtil.info(logger,
                            "异常处理第" + pageNum + "批次,原因:"+ ex.getMessage());
                }
            }
            LogUtil.info(logger,
                    "结束处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次");
            pageNum++;
        }while (pageInfo.getPages() >= pageNum) ;
        return finalAnnotation;
    }

    /**
     * 根据type/term查询标术语注表中的标注
     *@param type
     *@param term
     */
    public AnnotationPagination listAnnotationContainsUnitAnnotationByServerPaging(String type, String term, int cPageNum, int cPageSize){
        cn.malgo.common.LogUtil.info(logger, "开始批量查询标注术语，当前前端参数传入的pageIndex:"+cPageNum);
        int pageSize = 100;
        int currentIndex=cPageNum;//当前标注数据库中遍历的索引值
        int total=countAnnotationSizeByState(AnnotationStateEnum.FINISH.name());
        List<Annotation> finalAnnotationList = new LinkedList<>();
        outer:do {
            //cPageNum为前端传入的开始查询的startIndex
            List<Annotation> anAtomicTermList = listAnnotationByServerPaging(AnnotationStateEnum.FINISH.name(), currentIndex, pageSize);
            for (Annotation annotation : anAtomicTermList) {
                try {
                    //最终标注
                    List<TermAnnotationModel> finalModelList = AnnotationConvert
                            .convertAnnotationModelList(annotation.getFinalAnnotation());
                    boolean isHave = false;
                    for (TermAnnotationModel currentModel : finalModelList) {
                        if (currentModel.getType().equals(type) && currentModel.getTerm().equals(term)) {
                            isHave = true;
                            break;
                        }
                    }
                    //如果有符合的原子术语，则添加该条标注数据到最终集合中
                    //如果最终集合中的条数已经符合参数传递的pageSize的值，则暂停最外层循环直接返回
                    if (isHave) {
                        finalAnnotationList.add(annotation);
                        if(finalAnnotationList.size()==cPageSize)
                        {
                            currentIndex++;
//                            currentIndex=cPageNum;//当最终集合的大小符合cPageSize的时候，计算出当前在数据库中已经遍历到的索引值
                            break outer;
                        }
                        LogUtil.info(logger, "存在带替换的标注术语为:" + annotation.getId());
                    }
                } catch (Exception ex) {
                    LogUtil.info(logger,
                            "结束处理第" + ex.getMessage());
                }
                //count计数器，每遍历一条数据，+1
                currentIndex++;
            }
            System.out.println("当前的currentIndex:"+currentIndex);
        }while(currentIndex<total);
        System.out.println("当前遍历到数据库的索引值："+currentIndex+"<<<<<<<<<<<<<<<<>>>>>>>>>>数据库中总的标注数据："+total);
        AnnotationPagination annotationPagination=new AnnotationPagination<Annotation>();
        //传给前端两个参数，参数一：遍历Annotation表的的currentIndex,以及根据前端的pageSize大小的返回的集合
        annotationPagination.setLastIndex(currentIndex);
        annotationPagination.setList(finalAnnotationList);
        return annotationPagination;
    }
 }
