package cn.malgo.annotation.core.service.annotation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.malgo.annotation.common.dal.model.Annotation;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;

import cn.malgo.annotation.common.dal.mapper.AnAtomicTermMapper;
import cn.malgo.annotation.common.dal.model.AnAtomicTerm;
import cn.malgo.annotation.common.util.log.LogUtil;
import cn.malgo.annotation.core.model.annotation.TermAnnotationModel;
import cn.malgo.annotation.core.model.check.AnnotationChecker;
import cn.malgo.annotation.core.model.convert.AnnotationConvert;
import cn.malgo.annotation.core.model.enums.annotation.AnnotationStateEnum;
import cn.malgo.core.util.security.SecurityUtil;

/**
 * @author 张钟
 * @date 2017/11/2
 */
@Service
public class AnnotationBatchService extends AnnotationService {

    private Logger             logger = Logger.getLogger(AnnotationBatchService.class);

    @Autowired
    private AnAtomicTermMapper anAtomicTermMapper;

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

            pageInfo = queryByStateList(stateList, pageNum, pageSize);
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
     * 更新标注
     */
    public void updateUNEncryptedAnnotation(String userId) {

        int batchCount = 1;
        Page<Annotation> page = null;
        do {
            LogUtil.info(logger, "开始处理第" + batchCount + "批次");
            page = queryOnePageUNEncrypted(AnnotationStateEnum.UN_ENCRYPTED.name(), userId, 1, 10);
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

}
