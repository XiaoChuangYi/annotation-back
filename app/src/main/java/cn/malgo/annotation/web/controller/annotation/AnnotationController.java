package cn.malgo.annotation.web.controller.annotation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;

import cn.malgo.annotation.common.dal.model.AnTermAnnotation;
import cn.malgo.annotation.common.dal.model.CrmAccount;
import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.core.model.convert.AnnotationConvert;
import cn.malgo.annotation.core.service.annotation.AnnotationService;
import cn.malgo.annotation.web.controller.annotation.request.AnnotationQueryRequest;
import cn.malgo.annotation.web.controller.annotation.request.UpdateAnnotationRequest;
import cn.malgo.annotation.web.controller.annotation.result.AnnotationBratVO;
import cn.malgo.annotation.web.result.PageVO;
import cn.malgo.annotation.web.result.ResultVO;

/**
 *
 * @author 张钟
 * @date 2017/10/19
 */

@RestController
@SessionAttributes("currentAccount")
@RequestMapping(value = { "/annotation" })
public class AnnotationController {

    @Autowired
    private AnnotationService annotationService;

    /**
     * 分页查询标注信息,不使用新词获取一页标注数据
     * @param request
     * @return
     */
    @RequestMapping(value = { "/list.do" })
    public ResultVO<PageVO<AnnotationBratVO>> getOnePage(AnnotationQueryRequest request,
                                                         @ModelAttribute("currentAccount") CrmAccount crmAccount) {
        //基础参数检查
        AnnotationQueryRequest.check(request);

        //分页查询
        Page<AnTermAnnotation> page = annotationService.queryOnePage(request.getState(),
            crmAccount.getId(), request.getPageNum(), request.getPageSize());

        List<AnnotationBratVO> annotationBratVOList = convertAnnotationBratVOList(page.getResult());
        PageVO<AnnotationBratVO> pageVO = new PageVO(page, false);
        pageVO.setDataList(annotationBratVOList);

        return ResultVO.success(pageVO);
    }

    /**
     * 更新单条标注信息,附带新词
     * @param request
     * @return
     */
    @RequestMapping(value = { "/updateSingle.do" })
    public ResultVO<AnTermAnnotation> updateTermAnnotation(UpdateAnnotationRequest request,
                                                           @ModelAttribute("currentAccount") CrmAccount crmAccount) {

        //基础参数检查
        UpdateAnnotationRequest.check(request);

        AnTermAnnotation anTermAnnotation = annotationService.queryByAnId(request.getAnId());
        AssertUtil.state(crmAccount.getId().equals(anTermAnnotation.getModifier()), "您无权操作当前术语");

        //更新单条标注信息
        AnTermAnnotation anTermAnnotationNew = annotationService.autoAnnotationByAnId(
            request.getAnId(), request.getManualAnnotation(), request.getNewTerms());

        return ResultVO.success(anTermAnnotationNew);
    }

    /**
     * 标注ID
     * @param anId
     * @return
     */
    @RequestMapping(value = "/finish.do")
    public ResultVO finishAnnotation(String anId,
                                     @ModelAttribute("currentAccount") CrmAccount crmAccount) {
        AssertUtil.notBlank(anId, "标注ID为空");
        AnTermAnnotation anTermAnnotation = annotationService.queryByAnId(anId);
        AssertUtil.state(crmAccount.getId().equals(anTermAnnotation.getModifier()), "您无权操作当前术语");
        annotationService.finishAnnotation(anId);
        return ResultVO.success();
    }

    /**
     * 设置术语的状态为无法识别
     * @param anId
     * @return
     */
    @RequestMapping(value = "/unRecognize.do")
    public ResultVO setUnRecognize(String anId,
                                   @ModelAttribute("currentAccount") CrmAccount crmAccount) {
        AssertUtil.notBlank(anId, "术语ID为空");
        AnTermAnnotation anTermAnnotation = annotationService.queryByAnId(anId);
        AssertUtil.state(crmAccount.getId().equals(anTermAnnotation.getModifier()), "您无权操作当前术语");

        annotationService.setUnRecognize(anId);
        return ResultVO.success();
    }

    private List<AnnotationBratVO> convertAnnotationBratVOList(List<AnTermAnnotation> anTermAnnotationList) {
        List<AnnotationBratVO> annotationBratVOList = new ArrayList<>();
        for (AnTermAnnotation anTermAnnotation : anTermAnnotationList) {
            JSONObject bratJson = AnnotationConvert.convertToBratFormat(anTermAnnotation);
            AnnotationBratVO annotationBratVO = new AnnotationBratVO();
            BeanUtils.copyProperties(anTermAnnotation, annotationBratVO);
            annotationBratVO.setBratData(bratJson);
            annotationBratVOList.add(annotationBratVO);
        }
        return annotationBratVOList;
    }
}
