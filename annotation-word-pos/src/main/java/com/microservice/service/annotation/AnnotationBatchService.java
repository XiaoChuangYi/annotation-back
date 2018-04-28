package com.microservice.service.annotation;

import cn.malgo.common.LogUtil;
import cn.malgo.core.definition.Entity;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.microservice.apiserver.ApiServerService;
import com.microservice.apiserver.result.AnnotationResult;
import com.microservice.dataAccessLayer.entity.AnAtomicTerm;
//import com.microservice.dataAccessLayer.entity.Annotation;
import com.microservice.dataAccessLayer.entity.AnnotationWordPos;
import com.microservice.dataAccessLayer.entity.Corpus;
import com.microservice.dataAccessLayer.mapper.AnAtomicTermMapper;
import com.microservice.dataAccessLayer.mapper.CorpusMapper;
import com.microservice.enums.AnnotationStateEnum;
import com.microservice.enums.TermStateEnum;
import com.microservice.utils.AnnotationConvert;
import com.microservice.vo.AnnotationPagination;
import com.microservice.vo.AtomicTermAnnotation;
import com.microservice.vo.CombineAtomicTerm;
import com.microservice.vo.TermAnnotationModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Created by cjl on 2018/3/29.
 */
@Service
public class AnnotationBatchService extends AnnotationService {

    private Logger logger = Logger.getLogger(AnnotationBatchService.class);

    @Autowired
    private AnAtomicTermMapper anAtomicTermMapper;

    @Autowired
    private ApiServerService apiServerService;

    @Autowired
    private CorpusMapper corpusMapper;

    @Autowired
    private AsyAnnotationService asyAnnotationService;

    /**
     * 查询标注表的总条数
     * */
    private   int  countAnnotationSizeByState(String state){
        return  annotationWordPosMapper.countAnnotationSizeByState(state);
    }

    /**
     * 更新最终标注
     * @param anId
     * @param finalAnnotation
     */
    private void updateFinalAnnotation(String anId, String finalAnnotation) {

        AnnotationWordPos annotation = new AnnotationWordPos();
        annotation.setId(anId);
        annotation.setFinalAnnotation(finalAnnotation);
        annotation.setGmtModified(new Date());

        annotationWordPosMapper.updateAnnotationSelective(annotation);
    }


    /**
     * 批量自动标注
     * @param termList
     */
    public void autoAnnotationByTermList(List<Corpus> termList) {

        Map<String, String> termMap = new HashMap<>();
        for (Corpus corpus : termList) {
            termMap.put(corpus.getId(), corpus.getTerm());
        }
        List<AnnotationResult> annotationResultList = apiServerService
                .batchPhraseTokenize(termList);

        if (annotationResultList != null&&annotationResultList.size()>0) {
            for (AnnotationResult annotationResult : annotationResultList) {
                saveTermAnnotation(annotationResult.getId(), termMap.get(annotationResult.getId()),
                        annotationResult.getAnnotation());
            }
        }

    }

    public Page<Corpus> queryOnePageByState(TermStateEnum termStateEnum, int pageNum,
                                            int pageSize) {
        Page<Corpus> pageInfo = PageHelper.startPage(pageNum, pageSize);
        Corpus corpus=new Corpus();
        corpus.setState(termStateEnum.name());
        corpusMapper.listCorpusByCondition(corpus);
        return pageInfo;
    }

    /**
     * 过ApiServer批量对原始文本进行预标注
     */
    public void batchInitAnnotationOriginText(){
        int batchCount = 1;
        int pageSize = 10;
        int success = 0;
        Page<Corpus> page = null;
        try {
            do {
                LogUtil.info(logger, MessageFormat.format("开始处理第{0}批次", batchCount));
                page = queryOnePageByState(TermStateEnum.INIT, 1, pageSize);
                Future<Boolean> future = asyAnnotationService
                        .asyncAutoAnnotation(page.getResult());
                Boolean result = future.get();
                if (result) {
                    success = success + page.getResult().size();
                }
                LogUtil.info(logger, MessageFormat.format("结束处理第{0}批次", batchCount));

                batchCount++;

            } while (page.getTotal() > pageSize);

        } catch (Exception e) {
            LogUtil.error(logger, e, "预处理标注失败!terms:" + JSONObject.toJSONString(page.getResult()));
        }
    }

    /**
     * 批量替换单位标注的标签(类型)
     */
    public void batchReplaceUnitAnnotationType(String typeOld, String typeNew) {
        LogUtil.info(logger, "开始批量替换标注表单位标注中的type类型");
        int pageNum = 1;
        int pageSize = 1000;
        Page<AnnotationWordPos> pageInfo = null;
        List<AnnotationWordPos> finalAnnotation=new LinkedList<>();
        int total=countAnnotationSizeByState(null);
        int endPageIndex=total/pageSize;
        if(total%pageSize>0){
            endPageIndex++;
        }
        for(;pageNum<=endPageIndex;pageNum++){
            pageInfo=listAnnotationByStatesPaging(null,pageNum,pageSize);
            LogUtil.info(logger,
                    "开始处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次，剩余共"+(total-pageSize*pageNum)+"条记录");
            for (AnnotationWordPos annotation : pageInfo.getResult()){
                try {
                    annotation.setGmtModified(new Date());
                    List<Entity> manualList=AnnotationConvert.getUnitAnnotationList(annotation.getManualAnnotation());
                    manualList.stream()
                            .filter(x->x.getType().equals(typeOld)).forEach(x->x.setType(typeNew));

                    List<Entity> finalList=AnnotationConvert.getUnitAnnotationList(annotation.getFinalAnnotation());
                    finalList.stream()
                            .filter(x->x.getType().equals(typeOld)).forEach(x->x.setType(typeNew));
                    annotation.setManualAnnotation(AnnotationConvert.convertAnnotation2Str(manualList));
                    annotation.setFinalAnnotation(AnnotationConvert.convertAnnotation2Str(finalList));
                    finalAnnotation.add(annotation);
                }catch (Exception ex){
                    LogUtil.info(logger,
                            "结束处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次");
                }
            }
        }
        if(finalAnnotation.size()>0){
            LogUtil.info(logger, "存在带替换的标注术语,共:" + finalAnnotation.size()+"条需要处理");
            batchUpdateAnnotationFinalAndManual(finalAnnotation);
        }
    }


    /**
     * 批量合并包含指定单位标注(term/type)组标注数据
     */
    public void batchCombineUnitAnnotation(List<String> idList, List<CombineAtomicTerm> combineAtomicTermList, String newTerm, String newType){
        LogUtil.info(logger, "开始批量替换标注中的原子术语");
        //通过前台筛选的ids查询所有的符合标注的数据
        List<AnnotationWordPos> annotationList = annotationWordPosMapper.listAnnotationByIdArr(idList);
        for (AnnotationWordPos annotation : annotationList){
            try {
                //部分数字文本带有小数点，因此变换逻辑
                long count=combineAtomicTermList.stream()
                        .filter(x->annotation.getTerm().contains(x.getTerm()))
                        .count();
                if(combineAtomicTermList.size()==count){
                    String finalAnnotationNew= AnnotationConvert.getAnnotationAfterCombineAnnotationByLambda(
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
        Page<AnnotationWordPos> pageInfo = null;
        List<String> stateList = new ArrayList<>();
        stateList.add(AnnotationStateEnum.FINISH.name());
        String originText=atomicTermAnnotationList.stream()
                .sorted(Comparator.comparing(x->x.getStartPosition()))
                .map(
                        (x)-> x.getText()).collect(Collectors.joining(""));
        do {
            //根据finish状态到术语标注表中查询
            pageInfo =listAnnotationByStatesPaging(stateList, pageNum, pageSize);
            LogUtil.info(logger,
                    "开始处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次");
            for (AnnotationWordPos annotation : pageInfo.getResult()) {
                try {
                    if(AnnotationConvert.getUnitAnnotationList(annotation.getFinalAnnotation()).stream()
                            .filter(x->x.getTerm().equals(originText)).count()>0){
                        LogUtil.info(logger, "存在带替换的标注记录:" + annotation.getId());
                        String newAnnotation=AnnotationConvert.getAnnotationAfterDivideUnitAnnotation(atomicTermAnnotationList,annotation.getFinalAnnotation(),originText);
                        updateFinalAnnotation(annotation.getId(),newAnnotation);
                        LogUtil.info(logger, "标注数据:" + newAnnotation);
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
    public void deleteAtomicTermAndUnitAnnotation(String id,String term,String type){
        anAtomicTermMapper.deleteAnAtomicTermById(id);
        LogUtil.info(logger, "开始批量删除annotation表中符合当前原子术语的标注");
        Page<AnnotationWordPos> pageInfo=null;
        int pageNum=1;
        int pageSize=2000;
        do{
            pageInfo=listAnnotationByStatesPaging(null,pageNum,pageSize);
            LogUtil.info(logger,
                    "开始处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次");

            for (AnnotationWordPos annotation : pageInfo.getResult()) {
                try {

                    AnnotationConvert.getUnitAnnotationList(annotation.getFinalAnnotation()).stream()
                            .filter(x->x.getTerm().equals(term)&&x.getType().equals(type))
                            .forEach((x)->
                            {
                                updateFinalAnnotation(annotation.getId(),
                                        AnnotationConvert.deleteUnitAnnotationByLambda(annotation.getFinalAnnotation(),x.getTag()));
                                LogUtil.info(logger, "存在带替换的标注记录:" + annotation.getId());
                            });
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
        Page<AnnotationWordPos> pageInfo = null;
        List<String> stateList = new ArrayList<>();
        stateList.add(AnnotationStateEnum.FINISH.name());
        do {
            //根据finish状态到术语标注表中查询
            pageInfo = listAnnotationByStatesPaging(stateList, pageNum, pageSize);

            LogUtil.info(logger,
                    "开始处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次");

            for (AnnotationWordPos annotation : pageInfo.getResult()) {
                try {
                    List<Entity> entityList=AnnotationConvert.getUnitAnnotationList(annotation.getFinalAnnotation());
                    entityList.stream()
                            .filter(x->x.getType().equals(anAtomicTermOld.getType())&&x.getTerm().equals(anAtomicTermOld.getTerm()))
                            .forEach((x)->{
                                x.setTerm(anAtomicTermNew.getTerm());
                                x.setType(anAtomicTermNew.getType());
                                LogUtil.info(logger, "存在带替换的原子术语:" + annotation.getId());
                            });
                    updateFinalAnnotation(annotation.getId(),AnnotationConvert.convertAnnotation2Str(entityList));
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
    public List<AnnotationWordPos> listAnnotationByUnitAnnotationArr(List<CombineAtomicTerm> combineAtomicTermList) {
        int pageNum = 1;
        int pageSize = 2000;
        List<AnnotationWordPos> finalAnnotation = new ArrayList<>();
        Page<AnnotationWordPos> pageInfo = null;
        Set<String> setAtomicTerm=new HashSet<>();
        do {
            pageInfo = listAnnotationByStatesPaging(null, pageNum, pageSize);
            LogUtil.info(logger,
                    "开始处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次");
            for (AnnotationWordPos annotation : pageInfo.getResult()) {
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
        List<AnnotationWordPos> finalAnnotationList = new LinkedList<>();
        List<String> stateList = new ArrayList<>();
        stateList.add(AnnotationStateEnum.FINISH.name());
        outer:do {
            //cPageNum为前端传入的开始查询的startIndex
            List<AnnotationWordPos> anAtomicTermList = listAnnotationByStatesPaging(stateList, currentIndex, pageSize);
            for (AnnotationWordPos annotation : anAtomicTermList) {
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
        AnnotationPagination annotationPagination=new AnnotationPagination<AnnotationWordPos>();
        //传给前端两个参数，参数一：遍历Annotation表的的currentIndex,以及根据前端的pageSize大小的返回的集合
        annotationPagination.setLastIndex(currentIndex);
        annotationPagination.setList(finalAnnotationList);
        return annotationPagination;
    }
}
