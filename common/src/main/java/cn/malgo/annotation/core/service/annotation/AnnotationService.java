package cn.malgo.annotation.core.service.annotation;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import cn.malgo.annotation.common.dal.mapper.AnTermAnnotationMapper;
import cn.malgo.annotation.common.dal.model.AnTerm;
import cn.malgo.annotation.common.dal.model.AnTermAnnotation;
import cn.malgo.annotation.common.dal.sequence.CodeGenerateTypeEnum;
import cn.malgo.annotation.common.dal.sequence.SequenceGenerator;
import cn.malgo.annotation.common.service.integration.apiserver.ApiServerService;
import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.core.model.enums.annotation.AnnotationStateEnum;
import cn.malgo.annotation.core.service.term.TermService;

/**
 *
 * @author 张钟
 * @date 2017/10/18
 */
@Service
public class AnnotationService {

    @Autowired
    private SequenceGenerator      sequenceGenerator;

    @Autowired
    private AnTermAnnotationMapper anTermAnnotationMapper;

    @Autowired
    private ApiServerService       apiServerService;

    @Autowired
    private TermService            termService;

    /**
     * 根据状态分页查询标注
     * @param annotationStateEnum
     * @return
     */
    public Page<AnTermAnnotation> queryOnePage(AnnotationStateEnum annotationStateEnum, int pageNum,
                                               int pageSize) {
        Page<AnTermAnnotation> pageInfo = PageHelper.startPage(pageNum, pageSize);
        anTermAnnotationMapper.selectByState(annotationStateEnum.name());
        return pageInfo;
    }

    /**
     * 通过termId 来自动标注
     * 首次标注,主要用于定时任务的批处理
     * @param termId
     */
    public void autoAnnotationByTermId(String termId) {

        //通过调用apiServer服务,获取自动标注结果
        AnTerm anTerm = termService.queryByTermId(termId);
        String autoAnnotation = apiServerService.phraseTokenize(anTerm.getTerm());

        //检查是否存标注信息
        AnTermAnnotation anTermAnnotation = anTermAnnotationMapper.selectByTermId(termId);
        if (anTermAnnotation == null) {
            saveTermAnnotation(anTerm, autoAnnotation);
        } else {
            updateAutoAnnotation(anTermAnnotation.getId(), autoAnnotation);
        }
    }

    /**
     * 通过annotationId 来自动标注
     * 用户手动标注后,调用apiServer,合成手动标注和自动标注
     * @param anId
     */
    public void autoAnnotationByAnId(String anId) {

    }

    /**
     * 保存标注,主要用于标注自动标注
     * @param anTerm
     * @param autoAnnotation
     */
    private void saveTermAnnotation(AnTerm anTerm, String autoAnnotation) {

        String id = sequenceGenerator.nextCodeByType(CodeGenerateTypeEnum.DEFAULT);
        AnTermAnnotation anTermAnnotation = new AnTermAnnotation();
        anTermAnnotation.setId(id);
        anTermAnnotation.setTermId(anTerm.getId());
        anTermAnnotation.setTerm(anTerm.getTerm());
        anTermAnnotation.setAutoAnnotation(autoAnnotation);
        anTermAnnotation.setFinalAnnotation(autoAnnotation);
        anTermAnnotation.setState(AnnotationStateEnum.INIT.name());

        int saveResult = anTermAnnotationMapper.insert(anTermAnnotation);
        AssertUtil.state(saveResult > 0, "保存自动标注失败");
    }

    /**
     * 更新自动标注
     * @param anId
     * @param autoAnnotation
     */
    private void updateAutoAnnotation(String anId, String autoAnnotation) {
        AnTermAnnotation anTermAnnotation = new AnTermAnnotation();
        anTermAnnotation.setId(anId);
        anTermAnnotation.setAutoAnnotation(autoAnnotation);
        anTermAnnotation.setFinalAnnotation(autoAnnotation);
        anTermAnnotation.setGmtModified(new Date());

        int updateResult = anTermAnnotationMapper.updateByPrimaryKeySelective(anTermAnnotation);
        AssertUtil.state(updateResult > 0, "更新自动标注失败");
    }

}
