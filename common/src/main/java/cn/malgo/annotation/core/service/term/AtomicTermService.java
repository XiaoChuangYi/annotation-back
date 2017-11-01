package cn.malgo.annotation.core.service.term;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import cn.malgo.annotation.common.dal.mapper.AnAtomicTermMapper;
import cn.malgo.annotation.common.dal.mapper.AnTermAnnotationMapper;
import cn.malgo.annotation.common.dal.model.AnAtomicTerm;
import cn.malgo.annotation.common.dal.model.AnTermAnnotation;
import cn.malgo.annotation.common.dal.sequence.CodeGenerateTypeEnum;
import cn.malgo.annotation.common.dal.sequence.SequenceGenerator;
import cn.malgo.annotation.common.service.integration.apiserver.vo.TermTypeVO;
import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.core.model.annotation.TermAnnotationModel;
import cn.malgo.annotation.core.model.convert.AnnotationConvert;
import cn.malgo.annotation.core.model.enums.CommonStatusEnum;
import cn.malgo.annotation.core.model.enums.annotation.AnnotationStateEnum;
import cn.malgo.common.LogUtil;
import cn.malgo.common.security.SecurityUtil;

/**
 *
 * @author 张钟
 * @date 2017/10/23
 */
@Service
public class AtomicTermService {

    private Logger                 logger = Logger.getLogger(AtomicTermService.class);

    @Autowired
    private SequenceGenerator      sequenceGenerator;

    @Autowired
    private AnAtomicTermMapper     anAtomicTermMapper;

    @Autowired
    private AnTermAnnotationMapper anTermAnnotationMapper;

    /**
     * 保存原子术语,如果术语已经存在,更新,否则,新增
     * @param termTypeVO
     * @param fromAnId 新词来源的标注ID
     */
    public void saveAtomicTerm(String fromAnId, TermTypeVO termTypeVO) {

        saveAtomicTerm(fromAnId, termTypeVO.getTerm(), termTypeVO.getType());

    }

    /**
     * 保存原子术语,如果术语已经存在,更新,否则,新增
     * @param fromAnId 新词来源的标注ID
     * @param term
     * @param termType
     */
    public void saveAtomicTerm(String fromAnId, String term, String termType) {

        String securityTerm = SecurityUtil.cryptAESBase64(term);

        AnAtomicTerm anAtomicTermOld = anAtomicTermMapper.selectByTerm(securityTerm);

        if (anAtomicTermOld == null) {
            String id = sequenceGenerator.nextCodeByType(CodeGenerateTypeEnum.DEFAULT);
            AnAtomicTerm anAtomicTermNew = new AnAtomicTerm();
            anAtomicTermNew.setId(id);
            anAtomicTermNew.setFromAnId(fromAnId);
            anAtomicTermNew.setTerm(securityTerm);
            anAtomicTermNew.setType(termType);
            anAtomicTermNew.setState(CommonStatusEnum.ENABLE.name());

            int insertResult = anAtomicTermMapper.insert(anAtomicTermNew);
            AssertUtil.state(insertResult > 0, "保存原子术语失败");
        } else {

            anAtomicTermOld.setType(termType);
            anAtomicTermOld.setTerm(securityTerm);
            anAtomicTermOld.setFromAnId(fromAnId);
            anAtomicTermOld.setGmtModified(new Date());

            int updateResult = anAtomicTermMapper.updateByPrimaryKeySelective(anAtomicTermOld);
            AssertUtil.state(updateResult > 0, "更新原子术语失败");
        }

    }

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
        Page<AnTermAnnotation> pageInfo = null;
        List<String> stateList = new ArrayList<>();
        stateList.add(AnnotationStateEnum.FINISH.name());
        do {

            pageInfo = PageHelper.startPage(pageNum, pageSize);
            anTermAnnotationMapper.selectByStateList(stateList);
            LogUtil.info(logger,
                "开始处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次");

            for (AnTermAnnotation anTermAnnotation : pageInfo.getResult()) {
                try {
                    String text = SecurityUtil
                        .decryptAESBase64(anTermAnnotation.getFinalAnnotation());
                    List<TermAnnotationModel> termAnnotationModelList = AnnotationConvert
                        .convertAnnotationModelList(text);

                    //用来记录当前词条的所有标注是否有对应的原子术语,默认是有对应
                    boolean isValidate = true;
                    for (TermAnnotationModel termAnnotationModel : termAnnotationModelList) {
                        String key = termAnnotationModel.getTerm() + ":"
                                     + termAnnotationModel.getType();
                        key = key.replace("-unconfirmed","");

                        AnAtomicTerm anAtomicTerm = anAtomicTermMap.get(key);

                        //词库中存在对应的词条,检查词条是否存在来源,如果不存在,将当前标注作为该原子词条的来源
                        if (anAtomicTerm != null) {

                            if (StringUtils.isBlank(anAtomicTerm.getFromAnId())) {
                                AnAtomicTerm anAtomicTermForUpdate = new AnAtomicTerm();
                                anAtomicTermForUpdate.setId(anAtomicTerm.getId());
                                anAtomicTermForUpdate.setFromAnId(anTermAnnotation.getId());
                                anAtomicTermMapper.updateByPrimaryKeySelective(anAtomicTermForUpdate);
                            }

                        }else{
                            isValidate = false;
                        }
                    }
                    if(!isValidate){
                        AnTermAnnotation anTermAnnotationForUpdate = new AnTermAnnotation();
                        anTermAnnotationForUpdate.setId(anTermAnnotation.getId());
                        anTermAnnotationForUpdate.setState(AnnotationStateEnum.UN_RECOGNIZE.name());
                        anTermAnnotationMapper.updateByPrimaryKeySelective(anTermAnnotationForUpdate);
                    }
                } catch (Exception e) {
                    LogUtil.info(logger, "排查原子词库中的词条是否存在于标注中异常,标注ID:" + anTermAnnotation.getId());
                }
            }
            LogUtil.info(logger,
                "结束处理第" + pageNum + "批次,剩余" + (pageInfo.getPages() - pageNum) + "批次");
            pageNum++;

        } while (pageInfo.getPages() >= pageNum);


    }
}
