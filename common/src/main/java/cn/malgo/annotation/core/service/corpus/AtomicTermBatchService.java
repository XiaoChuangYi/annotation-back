package cn.malgo.annotation.core.service.corpus;

import java.util.*;
import java.util.stream.Collectors;

import cn.malgo.annotation.common.dal.model.Annotation;
import cn.malgo.annotation.common.dal.model.CombineAtomicTerm;
import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.core.model.annotation.AtomicTermAnnotation;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;

import cn.malgo.annotation.common.dal.mapper.AnAtomicTermMapper;
import cn.malgo.annotation.common.dal.model.AnAtomicTerm;
import cn.malgo.annotation.core.model.annotation.TermAnnotationModel;
import cn.malgo.annotation.core.model.convert.AnnotationConvert;
import cn.malgo.annotation.core.model.enums.CommonStatusEnum;
import cn.malgo.annotation.core.model.enums.annotation.AnnotationStateEnum;
import cn.malgo.annotation.core.service.annotation.AnnotationService;

import cn.malgo.common.LogUtil;
import cn.malgo.common.security.SecurityUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 张钟
 * @date 2017/11/2
 */

@Service
public class AtomicTermBatchService {

    private Logger             logger = Logger.getLogger(AtomicTermBatchService.class);

    @Autowired
    private AnnotationService  annotationService;

    @Autowired
    private AnAtomicTermMapper anAtomicTermMapper;

    /**
     * 批量加密
     */
    public void batchCrypt() {

        List<AnAtomicTerm> anAtomicTermList = anAtomicTermMapper
            .selectByState(AnnotationStateEnum.UN_ENCRYPTED.name());
        int count = anAtomicTermList.size();

        for (AnAtomicTerm anAtomicTerm : anAtomicTermList) {
            LogUtil.info(logger, "批量更新原子术语,剩余:" + count);

            String securityTerm = SecurityUtil.cryptAESBase64(anAtomicTerm.getTerm());
            anAtomicTerm.setTerm(securityTerm);
            anAtomicTerm.setState(CommonStatusEnum.ENABLE.name());
            anAtomicTermMapper.updateByPrimaryKeySelective(anAtomicTerm);
            count--;
        }

    }

    /**
     * 批量检查原子术语是否存在于术语表中
     */
    public void batchCheckRelation() {
        List<AnAtomicTerm> anAtomicTermList = anAtomicTermMapper.selectAll();
        LogUtil.info(logger, "待检查原子词条总数:" + anAtomicTermList.size());

        //构造成map,提高处理性能
        Map<String, AnAtomicTerm> anAtomicTermMap = new HashMap<>(anAtomicTermList.size());
        for (AnAtomicTerm anAtomicTerm : anAtomicTermList) {
            String key = SecurityUtil.decryptAESBase64(anAtomicTerm.getTerm()) + ":"
                         + anAtomicTerm.getType();
            anAtomicTermMap.put(key, anAtomicTerm);
        }

        LogUtil.info(logger, "开始全量匹配原子词库和术语的对应关系");
        int pageNum = 1;
        int pageSize = 10;
        Page<Annotation> pageInfo = null;
        List<String> stateList = new ArrayList<>();
        stateList.add(AnnotationStateEnum.FINISH.name());
        do {

            pageInfo = annotationService.queryByStateList(stateList, pageNum, pageSize);
            LogUtil.info(logger,
                "开始处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次");

            for (Annotation annotation : pageInfo.getResult()) {
                try {

                    List<TermAnnotationModel> termAnnotationModelList = AnnotationConvert
                        .convertAnnotationModelList(annotation.getFinalAnnotation());

                    //用来记录当前词条的所有标注是否有对应的原子术语,默认是有对应
                    boolean isValidate = true;
                    for (TermAnnotationModel termAnnotationModel : termAnnotationModelList) {
                        String key = termAnnotationModel.getTerm() + ":"
                                     + termAnnotationModel.getType();
                        key = key.replace("-unconfirmed", "");

                        AnAtomicTerm anAtomicTerm = anAtomicTermMap.get(key);

                        //词库中存在对应的词条,检查词条是否存在来源,如果不存在,将当前标注作为该原子词条的来源
                        if (anAtomicTerm != null) {

                            if (StringUtils.isBlank(anAtomicTerm.getFromAnId())) {
                                AnAtomicTerm anAtomicTermForUpdate = new AnAtomicTerm();
                                anAtomicTermForUpdate.setId(anAtomicTerm.getId());
                                anAtomicTermForUpdate.setFromAnId(annotation.getId());
                                anAtomicTermMapper
                                    .updateByPrimaryKeySelective(anAtomicTermForUpdate);
                            }

                        } else {
                            isValidate = false;
                        }
                    }
                    if (!isValidate) {
                        annotationService.updateAnnotationState(annotation.getId(),
                            AnnotationStateEnum.UN_RECOGNIZE);
                    }
                } catch (Exception e) {
                    LogUtil.info(logger, "排查原子词库中的词条是否存在于标注中异常,标注ID:" + annotation.getId());
                }
            }
            LogUtil.info(logger,
                "结束处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次");
            pageNum++;

        } while (pageInfo.getPages() >= pageNum);

    }
    public void batchCombineAtomicTerm(List<String> idList, List<CombineAtomicTerm> combineAtomicTermList,String newTerm, String newType){
        LogUtil.info(logger, "开始批量替换标注中的原子术语");
        //通过前台筛选的ids查询所有的符合标注的数据
        List<Annotation> annotationList = annotationService.queryByIdList(idList);
            for (Annotation annotation : annotationList){
                try {
                    //部分数字文本带有小数点，因此变换逻辑
                    long count=combineAtomicTermList.stream()
                            .filter(x->annotation.getTerm().contains(x.getTerm()))
                            .count();
                    if(combineAtomicTermList.size()==count){
                        //为了生成新的标注，并删掉原来的标注，关键需要知道替换的startPosition和endPosition
                        //有些文本显示是前后顺序的，标注不一定也是同样的前后顺序
                        List<TermAnnotationModel> termAnnotationModelList = AnnotationConvert
                                .convertAnnotationModelList(annotation.getFinalAnnotation());
                        List<Integer> startPositionList=new ArrayList<>();
                        List<Integer> endPositionList=new ArrayList<>();
                        for(TermAnnotationModel termAnnotationModel:termAnnotationModelList){
                            for(CombineAtomicTerm combineAtomicTerm:combineAtomicTermList) {

                                if (combineAtomicTerm.getTerm().toUpperCase().equals(termAnnotationModel.getTerm().toUpperCase())&&
                                        combineAtomicTerm.getType().equals(termAnnotationModel.getType())){
                                    System.out.println("combineAtomicTerm"+combineAtomicTerm.getTerm().toUpperCase()+";termAnnotationModel"+termAnnotationModel.getTerm().toUpperCase());
                                    startPositionList.add(termAnnotationModel.getStartPosition());
                                    endPositionList.add(termAnnotationModel.getEndPosition());
                                }
                            }
                        }
                        Collections.sort(startPositionList);
                        Collections.sort(endPositionList);
                        //对两个集合取交集，然后各个集合删除交集的部分，即为最终需要的数据
                        List<Integer> commonList=new ArrayList<>();
                        commonList.addAll(startPositionList);
                        commonList.retainAll(endPositionList);
                        startPositionList.removeAll(commonList);
                        endPositionList.removeAll(commonList);
                        System.out.println("去交集后"+JSONArray.parse(JSON.toJSONString(startPositionList)));
                        System.out.println("去交集后"+JSONArray.parse(JSON.toJSONString(endPositionList)));
                        String finalAnnotationOld=AnnotationConvert.convertToText(termAnnotationModelList);
                        if(startPositionList.size()>0){
                            for(int k=0;k<startPositionList.size();k++){
                                finalAnnotationOld=AnnotationConvert.addNewTag(finalAnnotationOld,newType,startPositionList.get(k).toString(),
                                        endPositionList.get(k).toString(),newTerm);
                            }
                        }
                        System.out.println("finalAnnotationOld："+finalAnnotationOld);
                        List<TermAnnotationModel> termAnnotationModelListNew = AnnotationConvert
                                .convertAnnotationModelList(finalAnnotationOld);
                        Iterator<TermAnnotationModel> iterator=termAnnotationModelListNew.iterator();
                        //合并术语时，删除原先的术语
                        while (iterator.hasNext()){
                            TermAnnotationModel termAnnotationModel=iterator.next();
                            for(CombineAtomicTerm combineAtomicTerm:combineAtomicTermList){
                                if(combineAtomicTerm.getTerm().toUpperCase().equals(termAnnotationModel.getTerm().toUpperCase())
                                        &&combineAtomicTerm.getType().equals(termAnnotationModel.getType())){
                                    System.out.println("combineAtomicTerm："+combineAtomicTerm.getTerm().toUpperCase()+" ; termAnnotationModel："+termAnnotationModel.getTerm().toUpperCase());
                                    iterator.remove();
                                }
                            }
                        }
                        String finalAnnotationNew=AnnotationConvert.convertToText(termAnnotationModelListNew);
                        System.out.println("finalAnnotationNew："+finalAnnotationNew);
                        LogUtil.info(logger, "存在带替换的标注记录:" + annotation.getId());
                        annotationService.updateFinalAnnotation(annotation.getId(), finalAnnotationNew);
                    }
                }
                catch(Exception ex) {
                    LogUtil.info(logger, "替换标注中的原子术语失败,标注ID:" + annotation.getId());
                }
            }
    }
    public void batchSubdivideAtomicTerm(List<AtomicTermAnnotation> atomicTermAnnotationList) {
        LogUtil.info(logger, "开始批量替换标准中的原子术语");
        int pageNum = 1;
        int pageSize = 2000;
        Page<Annotation> pageInfo = null;
        List<String> stateList = new ArrayList<>();
        stateList.add(AnnotationStateEnum.FINISH.name());
        do {
            //根据finish状态到术语标注表中查询
            pageInfo = annotationService.queryByStateList(stateList, pageNum, pageSize);
            LogUtil.info(logger,
                    "开始处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次");
            for (Annotation annotation : pageInfo.getResult()) {
                try {

                    List<TermAnnotationModel> termAnnotationModelList = AnnotationConvert
                            .convertAnnotationModelList(annotation.getFinalAnnotation());
                    //是否发生替换的标志
                    boolean isChange = true;
                    //新拆分的原子术语集合跟标注中的原子术语做匹配，如果符合，说明原先标注中已经有该原子术语，说明拆分不合理
                    //只要有一个满足，则不进行拆分，不更新标注
                    Iterator<TermAnnotationModel> iterator=termAnnotationModelList.iterator();
                    while (iterator.hasNext()){
                        TermAnnotationModel termAnnotationModel=iterator.next();
                        for(AtomicTermAnnotation atomicTermAnnotationModel: atomicTermAnnotationList){
                            //如果标注中存在待替换的原子术语,设置为false,不用进行替换
                            //前台拆分的原子术语当中,如果当前标注中有一个匹配到是符合的,则没有拆分的必要
                            if (termAnnotationModel.getTerm().equals(atomicTermAnnotationModel.getText())
                                    &&termAnnotationModel.getType().equals(atomicTermAnnotationModel.getAnnotationType())) {
                                isChange = false;
                            }
                        }
                    }
                    if (isChange&&annotation.getTerm().contains(atomicTermAnnotationList.get(0).getText())) {
                        String finalAnnotationNew=annotation.getFinalAnnotation();
                        for(AtomicTermAnnotation atomicTermAnnotationModel: atomicTermAnnotationList){
                             finalAnnotationNew = AnnotationConvert.addNewTagForAtomicTerm(
                                    finalAnnotationNew,atomicTermAnnotationModel.getAnnotationType(),
                                    atomicTermAnnotationModel.getStartPosition(),atomicTermAnnotationModel.getEndPosition(),atomicTermAnnotationModel.getText());
                        }
                        //添加完新的标签后，删除老的标签
                        List<TermAnnotationModel> newTermAnnotationModelList = AnnotationConvert
                                .convertAnnotationModelList(finalAnnotationNew);
                        Iterator<TermAnnotationModel> iterator1=newTermAnnotationModelList.iterator();
                        String newAtomicStr=atomicTermAnnotationList.get(0).getText();
                        while (iterator1.hasNext()){
                            TermAnnotationModel termAnnotationModel=iterator1.next();
                            if(!termAnnotationModel.getTerm().equals(newAtomicStr)&&termAnnotationModel.getTerm().contains(newAtomicStr)){
                                iterator1.remove();
                            }
                        }
                        LogUtil.info(logger, "存在带替换的标注记录:" + annotation.getId());
                        annotationService.updateFinalAnnotation(annotation.getId(), AnnotationConvert.convertToText(newTermAnnotationModelList));
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
     *批量删除符合当前原子术语的annotation表中的最终标注字段，并且删除原子术语表中对应的记录
     * @param id
     * @param term
     * @param type
     */
    @Transactional
    public void deleteAtomicTerm(String id,String term,String type){
        int deleteResult=anAtomicTermMapper.deleteByPrimaryKey(id);
        AssertUtil.state(deleteResult > 0, "删除术语失败");
        LogUtil.info(logger, "开始批量删除annotation表中符合当前原子术语的标注");
        Page<Annotation> pageInfo=null;
        int pageNum=1;
        int pageSize=2000;
//        List<String> stateList = new ArrayList<>();
        do{
            pageInfo=annotationService.queryByStateList(null,pageNum,pageSize);
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
                            newFinalAnnotation= AnnotationConvert.deleteTag(annotation.getFinalAnnotation(),termAnnotationModel.getTag());
                        }
                    }
                    if(isChange) {
                        LogUtil.info(logger, "存在带替换的标注记录:" + annotation.getId());
                        annotationService.updateFinalAnnotation(annotation.getId(), newFinalAnnotation);
//                        annotation.setFinalAnnotation(newFinalAnnotation);
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
     *批量替换标注中符合条件的最终标注
     * @param type
     * @param startPosition
     * @param endPosition
     * @param termText
     */
//    public void batchSubdivideAtomicTerm(String type,String startPosition,String endPosition,String termText) {
//        LogUtil.info(logger, "开始批量替换标准中的原子术语");
//        int pageNum = 1;
//        int pageSize = 2000;
//        Page<Annotation> pageInfo = null;
//        List<String> stateList = new ArrayList<>();
//        stateList.add(AnnotationStateEnum.FINISH.name());
//        do {
//            //根据finish状态到术语标注表中查询
//            pageInfo = annotationService.queryByStateList(stateList, pageNum, pageSize);
//            LogUtil.info(logger,
//                    "开始处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次");
//            for (Annotation annotation : pageInfo.getResult()) {
//                try {
//
//                    List<TermAnnotationModel> termAnnotationModelList = AnnotationConvert
//                            .convertAnnotationModelList(annotation.getFinalAnnotation());
//                    //是否发生替换的标志
//                    boolean isChange = true;
//                    for (TermAnnotationModel termAnnotationModel : termAnnotationModelList) {
//                        //如果标注中存在待替换的原子术语,设置为false,不用进行替换
//                            if (termAnnotationModel.getTerm().equals(termText)) {
//                                isChange = false;
//                            }
//                    }
//                    if (isChange&&annotation.getTerm().contains(termText)) {
//                        String finalAnnotationNew = AnnotationConvert.addNewTagForAtomicTerm(
//                                annotation.getFinalAnnotation(),type,startPosition,endPosition,termText);
//                        LogUtil.info(logger, "存在带替换的标注记录:" + annotation.getId());
//                        annotationService.updateFinalAnnotation(annotation.getId(), finalAnnotationNew);
//                    }
//                } catch (Exception e) {
//                    LogUtil.info(logger, "替换标注中的原子术语失败,标注ID:" + annotation.getId());
//                }
//            }
//            LogUtil.info(logger,
//                    "结束处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次");
//            pageNum++;
//
//        } while (pageInfo.getPages() >= pageNum);
//    }

    /**
     * 批量替换标注中的
     * @param anAtomicTermOld
     * @param anAtomicTermNew
     */
    public void batchReplaceAtomicTerm(AnAtomicTerm anAtomicTermOld, AnAtomicTerm anAtomicTermNew) {

        LogUtil.info(logger, "开始批量替换标准中的原子术语");
        int pageNum = 1;
        int pageSize = 2000;
        Page<Annotation> pageInfo = null;
        List<String> stateList = new ArrayList<>();
        stateList.add(AnnotationStateEnum.FINISH.name());
        do {
            //根据finish状态到术语标注表中查询
            pageInfo = annotationService.queryByStateList(stateList, pageNum, pageSize);

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
                        annotationService.updateFinalAnnotation(annotation.getId(), newText);
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

}
