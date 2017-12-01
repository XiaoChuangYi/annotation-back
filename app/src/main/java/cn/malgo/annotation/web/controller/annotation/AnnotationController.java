package cn.malgo.annotation.web.controller.annotation;

import java.util.ArrayList;
import java.util.List;

import cn.malgo.annotation.common.dal.model.Annotation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;

import cn.malgo.annotation.common.dal.model.CrmAccount;
import cn.malgo.annotation.common.service.integration.apiserver.vo.TermTypeVO;
import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.core.model.convert.AnnotationConvert;
import cn.malgo.annotation.core.model.enums.annotation.AnnotationOptionEnum;
import cn.malgo.annotation.core.service.annotation.AnnotationService;
import cn.malgo.annotation.web.controller.annotation.request.*;
import cn.malgo.annotation.web.controller.annotation.result.AnnotationBratVO;
import cn.malgo.annotation.web.controller.common.BaseController;
import cn.malgo.annotation.web.request.PageRequest;
import cn.malgo.annotation.web.result.PageVO;
import cn.malgo.annotation.web.result.ResultVO;

/**
 *
 * @author 张钟
 * @date 2017/10/19
 */

@RestController
@RequestMapping(value = { "/annotation" })
public class AnnotationController extends BaseController {

    @Autowired
    private AnnotationService annotationService;

    /**
     * 分页查询标注信息,不使用新词获取一页标注数据
     * @param request
     * @return
     */
    @RequestMapping(value = { "/list.do" })
    public ResultVO<PageVO<AnnotationBratVO>> getOnePageThroughApiServer(PageRequest request,
                                                                         @ModelAttribute("currentAccount") CrmAccount crmAccount) {

        //分页查询
        Page<Annotation> page = annotationService.queryOnePageThroughApiServer(
            crmAccount.getId(), request.getPageNum(), request.getPageSize());

        List<AnnotationBratVO> annotationBratVOList = convertAnnotationBratVOList(page.getResult());
        PageVO<AnnotationBratVO> pageVO = new PageVO(page, false);
        pageVO.setDataList(annotationBratVOList);

        return ResultVO.success(pageVO);
    }

    /**
     * 分页查询标注信息,不使用新词获取一页标注数据
     * @param request
     * @return
     */
    @RequestMapping(value = { "/queryDirectly.do" })
    public ResultVO<PageVO<AnnotationBratVO>> getOnePage(QueryDirectlyRequest request,
                                                         @ModelAttribute("currentAccount") CrmAccount crmAccount) {

        //分页查询
        Page<Annotation> page = annotationService.queryOnePageDirectly(request.getTerm(),request.getState(),
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
    public ResultVO<AnnotationBratVO> updateTermAnnotation(UpdateAnnotationRequest request,
                                                           @ModelAttribute("currentAccount") CrmAccount crmAccount) {
        //基础参数检查
        UpdateAnnotationRequest.check(request);

        Annotation annotation = annotationService.queryByAnId(request.getAnId());
        AssertUtil.state(crmAccount.getId().equals(annotation.getModifier()), "您无权操作当前术语");

        //更新单条标注信息
        Annotation annotationNew = annotationService.autoAnnotationByAnId(
            request.getAnId(), request.getManualAnnotation(), request.getNewTerms());

        AnnotationBratVO annotationBratVO = convertFromAnTermAnnotation(annotationNew);

        return ResultVO.success(annotationBratVO);
    }

    /**
     * 新增标注或者新词
     * @param request
     * @return
     */
    @RequestMapping(value = { "/add.do" })
    public ResultVO<AnnotationBratVO> addAnnotation(AddAnnotationRequest request,
                                                    @ModelAttribute("currentAccount") CrmAccount crmAccount) {

        AddAnnotationRequest.check(request);

        //权限检查,当前的标注是否属于当前用户
        Annotation annotation = annotationService.queryByAnId(request.getAnId());
        AssertUtil.state(crmAccount.getId().equals(annotation.getModifier()), "您无权操作当前术语");

        //构建新的手工标注
        String manualAnnotationNew = AnnotationConvert.addNewTag(
            annotation.getManualAnnotation(), request.getAnnotationType(),
            request.getStartPosition(), request.getEndPosition(), request.getText());

        //获取原有的新词列表
        String newTermsText = annotation.getNewTerms();
        //如果是新词,原有新词列表增加新词
        if (AnnotationOptionEnum.NEW_TERM.name().equals(request.getOption())) {
            newTermsText = AnnotationConvert.addNewTerm(newTermsText, request.getText(),
                request.getAnnotationType());
        }
        List<TermTypeVO> newTerms = TermTypeVO.convertFromString(newTermsText);

        //更新单条标注信息,先调用apiServer获取,后保存到数据库
        Annotation annotationNew = annotationService
            .autoAnnotationByAnId(request.getAnId(), manualAnnotationNew, newTerms);

        AnnotationBratVO annotationBratVO = convertFromAnTermAnnotation(annotationNew);

        return ResultVO.success(annotationBratVO);

    }

    /**
     * 删除标注或者同时删除新词
     * @param request
     * @return
     */
    @RequestMapping(value = "/delete.do")
    public ResultVO<AnnotationBratVO> deleteAnnotation(DeleteAnnotationRequest request,
                                                       @ModelAttribute("currentAccount") CrmAccount crmAccount) {
        //基础参数校验
        DeleteAnnotationRequest.check(request);

        //权限检查,当前的标注是否属于当前用户
        Annotation annotation = annotationService.queryByAnId(request.getAnId());
        AssertUtil.state(crmAccount.getId().equals(annotation.getModifier()), "您无权操作当前术语");

        //构建新的手工标注
        String manualAnnotationNew = AnnotationConvert
            .deleteTag(annotation.getManualAnnotation(), request.getTag());

        //获取原有的新词列表
        String newTermsText = annotation.getNewTerms();
        //如果是新词,从新词列表中删除新词
        if (AnnotationOptionEnum.NEW_TERM.name().equals(request.getOption())) {
            //从原有手工标注中,查找tag对应的标注,构造成新词
            TermTypeVO termTypeVO = AnnotationConvert
                .getTermTypeVOByTag(annotation.getManualAnnotation(), request.getTag());
            if (termTypeVO != null) {
                newTermsText = AnnotationConvert.deleteNewTerm(newTermsText, termTypeVO.getTerm(),
                    termTypeVO.getType());
            }
        }
        List<TermTypeVO> newTerms = TermTypeVO.convertFromString(newTermsText);

        //更新单条标注信息,先调用apiServer获取,后保存到数据库
        Annotation annotationNew = annotationService
            .autoAnnotationByAnId(request.getAnId(), manualAnnotationNew, newTerms);

        AnnotationBratVO annotationBratVO = convertFromAnTermAnnotation(annotationNew);

        return ResultVO.success(annotationBratVO);
    }

    /**
     * 删除新词
     * @param request
     * @return
     */
    @RequestMapping(value = "/deleteNewTerm.do")
    public ResultVO<AnnotationBratVO> deleteNewTerms(DeleteNewTermsRequest request,
                                                     @ModelAttribute("currentAccount") CrmAccount crmAccount) {

        DeleteNewTermsRequest.check(request);

        //权限检查,当前的标注是否属于当前用户
        Annotation annotation = annotationService.queryByAnId(request.getAnId());
        AssertUtil.state(crmAccount.getId().equals(annotation.getModifier()), "您无权操作当前术语");

        String newTermsAfterDelete = AnnotationConvert.deleteNewTerm(annotation.getNewTerms(),
            request.getTerm(), request.getTermType());

        //如果删除后的新词与删除前的一致,则无需更新删除后的新词
        if (newTermsAfterDelete.equals(annotation.getNewTerms())) {
            AnnotationBratVO annotationBratVO = convertFromAnTermAnnotation(annotation);
            return ResultVO.success(annotationBratVO);
        }

        //构建最新的新词列表
        List<TermTypeVO> newTerms = TermTypeVO.convertFromString(newTermsAfterDelete);

        //更新单条标注信息,先调用apiServer获取,后保存到数据库
        Annotation annotationNew = annotationService.autoAnnotationByAnId(
            annotation.getId(), annotation.getManualAnnotation(), newTerms);

        AnnotationBratVO annotationBratVO = convertFromAnTermAnnotation(annotationNew);

        return ResultVO.success(annotationBratVO);

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
        Annotation annotation = annotationService.queryByAnId(anId);
        AssertUtil.state(crmAccount.getId().equals(annotation.getModifier()), "您无权操作当前术语");

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
        Annotation annotation = annotationService.queryByAnId(anId);
        AssertUtil.state(crmAccount.getId().equals(annotation.getModifier()), "您无权操作当前术语");

        annotationService.setUnRecognize(anId);
        return ResultVO.success();
    }


    /**
     * 根据标注id,获取经过apiServer处理的最新标注数据
     * @param anId
     * @param crmAccount
     * @return
     */
    @RequestMapping(value = "/getAnnotationText.do")
    public ResultVO<String> getAnnotationText(String anId,@ModelAttribute("currentAccount") CrmAccount crmAccount){
        AssertUtil.notBlank(anId, "术语ID为空");
        Annotation annotation = annotationService.queryByAnId(anId);
        AssertUtil.state(crmAccount.getId().equals(annotation.getModifier()), "您无权操作当前术语");

        Annotation annotationResult =  annotationService.queryByAnIdThroughApiServer(anId);

        return ResultVO.success(annotationResult);
    }

    /**
     * 模型转换,标注模型转换成brat模型
     * @param annotationList
     * @return
     */
    private List<AnnotationBratVO> convertAnnotationBratVOList(List<Annotation> annotationList) {
        List<AnnotationBratVO> annotationBratVOList = new ArrayList<>();
        for (Annotation annotation : annotationList) {
            AnnotationBratVO annotationBratVO = convertFromAnTermAnnotation(annotation);
            annotationBratVOList.add(annotationBratVO);
            annotationBratVO.setMemo(JSONArray.parseArray(annotation.getMemo()));
        }
        return annotationBratVOList;
    }

    /**
     * 模型转换,标注模型转换成brat模型
     * @param annotation
     * @return
     */
    private AnnotationBratVO convertFromAnTermAnnotation(Annotation annotation) {
        JSONObject bratJson = AnnotationConvert.convertToBratFormat(annotation);
        AnnotationBratVO annotationBratVO = new AnnotationBratVO();
        BeanUtils.copyProperties(annotation, annotationBratVO);
        annotationBratVO.setBratData(bratJson);
        annotationBratVO.setNewTerms(JSONArray.parseArray(annotation.getNewTerms()));
        return annotationBratVO;
    }

}
