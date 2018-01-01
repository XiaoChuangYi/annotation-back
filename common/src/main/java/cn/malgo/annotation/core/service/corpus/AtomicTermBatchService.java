package cn.malgo.annotation.core.service.corpus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.malgo.annotation.common.dal.model.Annotation;
import cn.malgo.annotation.core.model.annotation.AtomicTermAnnotation;
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
                    for (TermAnnotationModel termAnnotationModel : termAnnotationModelList) {
                        for(AtomicTermAnnotation atomicTermAnnotationModel: atomicTermAnnotationList){
                            //如果标注中存在待替换的原子术语,设置为false,不用进行替换
                            if (termAnnotationModel.getTerm().equals(atomicTermAnnotationModel.getText())) {
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
                        LogUtil.info(logger, "存在带替换的标注记录:" + annotation.getId());
                        LogUtil.info(logger, "当前标注记录为:" + finalAnnotationNew);
                        annotationService.updateFinalAnnotation(annotation.getId(), finalAnnotationNew);
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
