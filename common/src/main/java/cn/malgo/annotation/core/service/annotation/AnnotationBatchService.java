package cn.malgo.annotation.core.service.annotation;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;

import cn.malgo.annotation.common.dal.model.AnTermAnnotation;
import cn.malgo.annotation.common.util.log.LogUtil;
import cn.malgo.annotation.core.model.check.AnnotationChecker;
import cn.malgo.annotation.core.model.enums.annotation.AnnotationStateEnum;

/**
 * @author 张钟
 * @date 2017/11/2
 */
@Service
public class AnnotationBatchService extends AnnotationService {

    private Logger logger = Logger.getLogger(AnnotationBatchService.class);

    /**
     * 批量,全量检查标注的二义性
     */
    public void batchCheckAmbiguity() {

        LogUtil.info(logger, "开全量检查标注的二义性");
        int pageNum = 1;
        int pageSize = 10;
        Page<AnTermAnnotation> pageInfo = null;
        List<String> stateList = new ArrayList<>();
        stateList.add(AnnotationStateEnum.FINISH.name());
        do {

            pageInfo = queryByStateList(stateList, pageNum, pageSize);
            LogUtil.info(logger,
                "开始处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次");

            for (AnTermAnnotation anTermAnnotation : pageInfo.getResult()) {
                try {

                    boolean hasAmbiguity = AnnotationChecker
                        .hasAmbiguity(anTermAnnotation.getFinalAnnotation());

                    if (hasAmbiguity) {
                        //此时存在歧义,并且设置状态为UN_RECOGNIZE
                        LogUtil.info(logger, "发现二义性标注,ID:" + anTermAnnotation.getId());
                        updateAnnotationState(anTermAnnotation.getId(),
                            AnnotationStateEnum.UN_RECOGNIZE);
                    }

                } catch (Exception e) {
                    LogUtil.info(logger, "检查标注二义性异常,标注ID:" + anTermAnnotation.getId());
                }
            }
            LogUtil.info(logger,
                "结束处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次");
            pageNum++;

        } while (pageInfo.getPages() >= pageNum);

    }

    /**
     * 更新标注
     */
    public void updateUNEncryptedAnnotation(String userId) {

        int batchCount = 1;
        Page<AnTermAnnotation> page = null;
        do {
            LogUtil.info(logger, "开始处理第" + batchCount + "批次");
            page = queryOnePageUNEncrypted(AnnotationStateEnum.UN_ENCRYPTED.name(), userId, 1, 10);
            AnTermAnnotation anTermAnnotation_temp = null;
            try {
                for (AnTermAnnotation anTermAnnotation : page.getResult()) {
                    anTermAnnotation_temp = anTermAnnotation;
                    cryptAnnotationAndUpdate(anTermAnnotation, AnnotationStateEnum.FINISH);
                }
            } catch (Exception e) {
                LogUtil.info(logger, "加密失败anId" + anTermAnnotation_temp.getId());
            }
            LogUtil.info(logger, "结束处理第" + batchCount + "批次,剩余:" + (page.getTotal() - 10));
            batchCount++;
        } while (page.getTotal() > 10);

    }
}
