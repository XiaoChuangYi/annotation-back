package cn.malgo.annotation.core.service.type;

import cn.malgo.annotation.common.dal.model.AnTermAnnotation;
import cn.malgo.annotation.core.model.annotation.TermAnnotationModel;
import cn.malgo.annotation.core.model.convert.AnnotationConvert;
import cn.malgo.annotation.core.model.enums.annotation.AnnotationStateEnum;
import cn.malgo.annotation.core.service.annotation.AnnotationService;
import cn.malgo.common.LogUtil;
import com.github.pagehelper.Page;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cjl on 2017/11/23.
 */
@Service
public class TypeAnnotationBatchService {

    private Logger logger = Logger.getLogger(TypeAnnotationBatchService.class);

    @Autowired
    private AnnotationService annotationService;

    /**
     * 批量更新术语标注表中的FINAL_ANNOTATION和MANUAL_ANNOTATION字段中的type类型
     *
     */
    public void batchReplaceAnnotationTerm(String typeOld, String typeNew) {
            LogUtil.info(logger, "开始批量替换术语标注表中的type类型");
            int pageNum = 1;
            int pageSize = 1000;
            Page<AnTermAnnotation> pageInfo = null;
            List<String> stateList = new ArrayList<>();
            stateList.add(AnnotationStateEnum.PROCESSING.name());
            int total=annotationService.annotationTermSize(AnnotationStateEnum.PROCESSING.name());
            System.out.println(total);
            int endPageIndex=total/pageSize;
            System.out.println(endPageIndex);
            if(total%pageSize>0){
                endPageIndex++;
            }
            System.out.println(endPageIndex);
            for(;pageNum<=endPageIndex;pageNum++){
                pageInfo=annotationService.queryByStateList(stateList,pageNum,pageSize);
                LogUtil.info(logger,
                        "开始处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次");
                for (AnTermAnnotation anTermAnnotation : pageInfo.getResult()) {
                    try {
                        //手动标注
                        List<TermAnnotationModel> manualModelList = AnnotationConvert
                                .convertAnnotationModelList(anTermAnnotation.getManualAnnotation());
                        //最终标注
                        List<TermAnnotationModel> finalModelList = AnnotationConvert
                                .convertAnnotationModelList(anTermAnnotation.getFinalAnnotation());
                        boolean finalIsChange = false;
                        for (TermAnnotationModel currentModel : finalModelList) {
                            //如果标注中存在待替换的type类型,进行替换
                            if (currentModel.getType().equals(typeOld)) {
                                currentModel.setType(typeNew);
                                finalIsChange=true;
                            }
                        }
                        boolean manualIsChange=false;
                        for (TermAnnotationModel currentModel : manualModelList) {
                            //如果标注中存在待替换的type类型,进行替换
                            if (currentModel.getType().equals(typeOld)) {
                                currentModel.setType(typeNew);
                                manualIsChange=true;
                            }
                        }
                        if (finalIsChange) {
                            LogUtil.info(logger, "存在带替换的标注术语:" + anTermAnnotation.getId());
                            String newText = AnnotationConvert.convertToText(finalModelList);
                            annotationService.updateFinalAnnotation(anTermAnnotation.getId(), newText);
                        }
                        if(manualIsChange){
                            LogUtil.info(logger, "存在带替换的标注术语:" + anTermAnnotation.getId());
                            String newText = AnnotationConvert.convertToText(manualModelList);
                            annotationService.updateMunalAnnotation(anTermAnnotation.getId(), newText);
                        }
                    }catch (Exception ex){
                        LogUtil.info(logger,
                                "结束处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次");
                    }
                }
        }

    }
}
