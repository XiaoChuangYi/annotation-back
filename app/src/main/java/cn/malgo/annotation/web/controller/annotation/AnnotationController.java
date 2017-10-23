package cn.malgo.annotation.web.controller.annotation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;

import cn.malgo.annotation.common.dal.model.AnTermAnnotation;
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
    public ResultVO<PageVO<AnnotationBratVO>> getOnePage(AnnotationQueryRequest request) {
        //基础参数检查
        AnnotationQueryRequest.check(request);

        //分页查询
        Page<AnTermAnnotation> page = annotationService.queryOnePage(request.getState(),
            request.getUserId(), request.getPageNum(), request.getPageSize());

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
    public ResultVO<AnTermAnnotation> updateTermAnnotation(UpdateAnnotationRequest request) {

        //基础参数检查
        UpdateAnnotationRequest.check(request);

        //更新单条标注信息
        AnTermAnnotation anTermAnnotation = annotationService.autoAnnotationByAnId(request.getId(),
            request.getManualAnnotation(), request.getNewTerms());

        return ResultVO.success(anTermAnnotation);
    }

    /**
     * 更新指定的标注后,刷新当前页面中的其他标注
     * @param request
     * @return
     */
    @RequestMapping(value = { "/updateAndRefresh.do" })
    public ResultVO<PageVO<AnnotationBratVO>> updateTermAnnotationRefreshPage(UpdateAnnotationRequest request) {
        //基础参数检查
        UpdateAnnotationRequest.check(request);

        //更新单条标注信息
        annotationService.autoAnnotationByAnId(request.getId(), request.getManualAnnotation(),
            request.getNewTerms());

        //分页查询,且批量调用apiServer获取最新的标注,保存到数据库
        Page<AnTermAnnotation> page = annotationService.queryOnePageAndRefresh(request.getState(),
            request.getUserId(), request.getManualAnnotation(), request.getNewTerms(),
            request.getPageNum(), request.getPageSize());

        List<AnnotationBratVO> annotationBratVOList = convertAnnotationBratVOList(page.getResult());
        PageVO<AnnotationBratVO> pageVO = new PageVO(page, false);
        pageVO.setDataList(annotationBratVOList);

        return ResultVO.success(pageVO);
    }

    /**
     * 标注ID
     * @param id
     * @return
     */
    @RequestMapping(value = "/finish.do")
    public ResultVO finishAnnotation(String id) {
        AssertUtil.notBlank(id, "标注ID为空");
        annotationService.finishAnnotation(id);
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
