package cn.malgo.annotation.core.service.type;

import cn.malgo.annotation.common.dal.model.Annotation;
import cn.malgo.annotation.core.model.annotation.TermAnnotationModel;
import cn.malgo.annotation.core.model.convert.AnnotationConvert;
import cn.malgo.annotation.core.service.annotation.AnnotationService;
import cn.malgo.common.LogUtil;
import cn.malgo.common.security.SecurityUtil;
import com.github.pagehelper.Page;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by cjl on 2017/11/23.
 */
@Service
public class TypeAnnotationBatchService {

    private Logger logger = Logger.getLogger(TypeAnnotationBatchService.class);

    @Autowired
    private AnnotationService annotationService;

    public void batchReplaceAnnotationTerm(String typeOld, String typeNew) {
        LogUtil.info(logger, "开始批量替换术语标注表中的type类型");
        int pageNum = 1;
        int pageSize = 1000;
        Page<Annotation> pageInfo = null;
        List<Annotation> finalAnno=new LinkedList<>();
        int total=annotationService.annotationTermSize(null);
        int endPageIndex=total/pageSize;
        if(total%pageSize>0){
            endPageIndex++;
        }
        for(;pageNum<=endPageIndex;pageNum++){
            pageInfo=annotationService.queryByStateList(null,pageNum,pageSize);
            LogUtil.info(logger,
                    "开始处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次，该批次共"+(pageInfo.getEndRow()-pageInfo.getStartRow())+"条记录");
            for (Annotation annotation : pageInfo.getResult()){
                try {
                    //手动标注
                    List<TermAnnotationModel> manualModelList = AnnotationConvert
                            .convertAnnotationModelList(annotation.getManualAnnotation());
                    //最终标注
                    List<TermAnnotationModel> finalModelList = AnnotationConvert
                            .convertAnnotationModelList(annotation.getFinalAnnotation());
                    for (TermAnnotationModel currentModel : finalModelList) {
                        //如果标注中存在待替换的type类型,进行替换
                        if (currentModel.getType().equals(typeOld)) {
                            currentModel.setType(typeNew);
                        }
                    }
                    for (TermAnnotationModel currentModel : manualModelList) {
                        //如果标注中存在待替换的type类型,进行替换
                        if (currentModel.getType().equals(typeOld)) {
                            currentModel.setType(typeNew);
                        }
                    }
                    annotation.setGmtModified(new Date());
                    annotation.setManualAnnotation(SecurityUtil.cryptAESBase64(AnnotationConvert.convertToText(manualModelList)));
                    annotation.setFinalAnnotation(SecurityUtil.cryptAESBase64(AnnotationConvert.convertToText(finalModelList)));
                    finalAnno.add(annotation);
                }catch (Exception ex){
                    LogUtil.info(logger,
                            "结束处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次");
                }
            }
        }
        if(finalAnno.size()>0){
            LogUtil.info(logger, "存在带替换的标注术语:" + finalAnno.size());
            annotationService.updateBatchAnnotation(finalAnno);
        }
    }
    /**
     * 批量更新术语标注表中的FINAL_ANNOTATION和MANUAL_ANNOTATION字段中的type类型
     */
//    public void batchReplaceAnnotationTerm(String typeOld, String typeNew) {
//            LogUtil.info(logger, "开始批量替换术语标注表中的type类型");
//            int pageNum = 1;
//            int pageSize = 1000;
//            Page<Annotation> pageInfo = null;
////            List<String> stateList = new ArrayList<>();
////            stateList.add(AnnotationStateEnum.PROCESSING.name());
//            int total=annotationService.annotationTermSize(null);
//            System.out.println(total);
//            int endPageIndex=total/pageSize;
//            System.out.println(endPageIndex);
//            if(total%pageSize>0){
//                endPageIndex++;
//            }
//            System.out.println(endPageIndex);
//            for(;pageNum<=endPageIndex;pageNum++){
//                pageInfo=annotationService.queryByStateList(null,pageNum,pageSize);
//                LogUtil.info(logger,
//                        "开始处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次，该批次共"+(pageInfo.getEndRow()-pageInfo.getStartRow())+"条记录");
//                for (Annotation anTermAnnotation : pageInfo.getResult()) {
//                    try {
//                        //手动标注
//                        List<TermAnnotationModel> manualModelList = AnnotationConvert
//                                .convertAnnotationModelList(anTermAnnotation.getManualAnnotation());
//                        //最终标注
//                        List<TermAnnotationModel> finalModelList = AnnotationConvert
//                                .convertAnnotationModelList(anTermAnnotation.getFinalAnnotation());
//                        boolean finalIsChange = false;
//                        for (TermAnnotationModel currentModel : finalModelList) {
//                            //如果标注中存在待替换的type类型,进行替换
//                            if (currentModel.getType().equals(typeOld)) {
//                                currentModel.setType(typeNew);
//                                finalIsChange=true;
//                            }
//                        }
//                        boolean manualIsChange=false;
//                        for (TermAnnotationModel currentModel : manualModelList) {
//                            //如果标注中存在待替换的type类型,进行替换
//                            if (currentModel.getType().equals(typeOld)) {
//                                currentModel.setType(typeNew);
//                                manualIsChange=true;
//                            }
//                        }
//                        if (finalIsChange) {
//                            LogUtil.info(logger, "存在带替换的最终标注术语:" + anTermAnnotation.getId());
//                            String newText = AnnotationConvert.convertToText(finalModelList);
//                            annotationService.updateFinalAnnotation(anTermAnnotation.getId(), newText);
//                        }
//                        if(manualIsChange){
//                            LogUtil.info(logger, "存在带替换的手动标注术语:" + anTermAnnotation.getId());
//                            String newText = AnnotationConvert.convertToText(manualModelList);
//                            annotationService.updateMunalAnnotation(anTermAnnotation.getId(), newText);
//                        }
//                    }catch (Exception ex){
//                        LogUtil.info(logger,
//                                "结束处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次");
//                    }
//                }
//        }
//
//    }
}
