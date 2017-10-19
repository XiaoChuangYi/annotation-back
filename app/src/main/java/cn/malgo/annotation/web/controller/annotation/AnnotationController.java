package cn.malgo.annotation.web.controller.annotation;

import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;

import cn.malgo.annotation.common.dal.model.AnTermAnnotation;
import cn.malgo.annotation.core.service.annotation.AnnotationService;
import cn.malgo.annotation.web.controller.annotation.request.AnnotationQueryRequest;
import cn.malgo.annotation.web.controller.annotation.request.UpdateAnnotationRequest;
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
     * demo接口
     * @return
     */
    @RequestMapping(value = { "/demo.do" })
    public ResultVO<JSONObject> demo() {
        String data = "[{\"entities\":[[\"T1\",\"Observable-entity\",[[0,3]]],[\"T2\",\"Number\",[[3,5]]],[\"T3\",\"Unit\",[[5,6]]],[\"T4\",\"Token\",[[6,7]]],[\"T5\",\"Zone\",[[7,8]]],[\"T6\",\"Body-structure\",[[7,8]]],[\"T7\",\"Qualifier\",[[8,9]]],[\"T8\",\"Token\",[[9,10]]],[\"T9\",\"Body-structure\",[[10,12]]],[\"T10\",\"Token\",[[12,14]]],[\"T11\",\"Token\",[[14,15]]],[\"T12\",\"Body-structure\",[[15,17]]],[\"T13\",\"Zone\",[[15,16]]],[\"T14\",\"Body-structure\",[[15,16]]],[\"T15\",\"Zone\",[[16,17]]],[\"T16\",\"Logic\",[[17,18]]],[\"T17\",\"Token\",[[18,19]]],[\"T18\",\"Logic\",[[19,20]]],[\"T19\",\"Clinical-finding\",[[20,22]]],[\"T20\",\"Body-structure\",[[22,25]]],[\"T21\",\"Token\",[[25,26]]],[\"T22\",\"Body-structure\",[[27,29]]],[\"T23\",\"Space\",[[27,28]]],[\"T24\",\"Body-structure\",[[28,29]]],[\"T25\",\"Token\",[[29,31]]],[\"T26\",\"Logic\",[[31,32]]],[\"T27\",\"Measure\",[[32,34]]],[\"T28\",\"Token\",[[34,36]]],[\"T29\",\"Token\",[[36,38]]],[\"T30\",\"Token\",[[38,39]]],[\"T31\",\"Body-structure\",[[40,42]]],[\"T32\",\"Space\",[[40,41]]],[\"T33\",\"Body-structure\",[[41,42]]],[\"T34\",\"Observable-entity\",[[42,45]]],[\"T35\",\"Qualifier\",[[45,46]]],[\"T36\",\"Token\",[[46,47]]],[\"T37\",\"Token\",[[47,49]]],[\"T38\",\"Logic\",[[49,50]]],[\"T39\",\"Token\",[[50,52]]],[\"T40\",\"Token\",[[52,54]]],[\"T41\",\"Token\",[[54,55]]],[\"T42\",\"Body-structure\",[[55,56]]],[\"T43\",\"Qualifier\",[[56,57]]],[\"T44\",\"Token\",[[57,58]]],[\"T45\",\"Body-structure\",[[58,60]]],[\"T46\",\"Space\",[[58,59]]],[\"T47\",\"Body-structure\",[[59,60]]],[\"T48\",\"Logic\",[[60,61]]],[\"T49\",\"Observable-entity\",[[61,62]]],[\"T50\",\"Clinical-finding\",[[62,63]]],[\"T51\",\"Logic\",[[63,64]]],[\"T52\",\"Token\",[[64,65]]],[\"T53\",\"Token\",[[65,67]]],[\"T54\",\"Token\",[[67,68]]],[\"T55\",\"Body-structure\",[[68,72]]],[\"T56\",\"Body-structure\",[[68,71]]],[\"T57\",\"Body-structure\",[[68,70]]],[\"T58\",\"Body-structure\",[[68,69]]],[\"T59\",\"Body-structure\",[[69,72]]],[\"T60\",\"Body-structure\",[[69,71]]],[\"T61\",\"Body-structure\",[[69,70]]],[\"T62\",\"Body-structure\",[[70,72]]],[\"T63\",\"Body-structure\",[[70,71]]],[\"T64\",\"Space\",[[71,72]]],[\"T65\",\"Logic\",[[72,73]]],[\"T66\",\"Token\",[[73,75]]],[\"T67\",\"Token\",[[75,76]]],[\"T68\",\"Body-structure\",[[76,79]]],[\"T69\",\"Space\",[[76,77]]],[\"T70\",\"Body-structure\",[[77,79]]],[\"T71\",\"Logic\",[[79,80]]],[\"T72\",\"Clinical-finding\",[[80,82]]]],\"token_offsets\":[[0,3],[3,5],[5,6],[6,7],[7,8],[7,8],[8,9],[9,10],[10,12],[12,14],[14,15],[15,16],[15,16],[16,17],[17,18],[18,19],[19,20],[20,22],[22,25],[25,26],[27,28],[28,29],[29,31],[31,32],[32,34],[34,36],[36,38],[38,39],[40,41],[41,42],[42,45],[45,46],[46,47],[47,49],[49,50],[50,52],[52,54],[54,55],[55,56],[56,57],[57,58],[58,59],[59,60],[60,61],[61,62],[62,63],[63,64],[64,65],[65,67],[67,68],[68,71],[68,70],[68,69],[69,71],[69,70],[70,71],[71,72],[72,73],[73,75],[75,76],[76,77],[77,79],[79,80],[80,82]],\"text\":\"KPS90分，颈软，气管居中，颈部未扪及肿大淋巴结。 双肺未闻及明显干湿罗音。 左肺呼吸音清，未闻及干湿啰音。腹软，全腹无压痛及反跳痛，肝脾肋下未触及。双下肢无水肿\",\"sentence_offsets\":[[0,26],[26,39],[39,55],[55,76],[76,82]]}]";

        JSONArray result = JSONArray.parseArray(data);
        return ResultVO.success(result);

    }

    /**
     * 获取用于渲染的配置数据
     * @return
     */
    @RequestMapping(value = { "config.do" })
    public ResultVO<JSONObject> configDemo() {

        return null;
    }

    /**
     * 分页查询标注信息,不使用新词获取一页标注数据
     * @param request
     * @return
     */
    @RequestMapping(value = { "/list.do" })
    public ResultVO<PageVO> getOnePage(AnnotationQueryRequest request) {
        //基础参数检查
        AnnotationQueryRequest.check(request);

        //分页查询
        Page<AnTermAnnotation> page = annotationService.queryOnePage(request.getState(),
            request.getUserId(), request.getPageNum(), request.getPageSize());
        PageVO pageVO = new PageVO(page);
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
    @RequestMapping(value = {"/updateAndRefresh.do"})
    public ResultVO<PageVO> updateTermAnnotationRefreshPage(UpdateAnnotationRequest request) {
        //基础参数检查
        UpdateAnnotationRequest.check(request);

        //分页查询
        Page<AnTermAnnotation> page = annotationService.queryOnePageAndRefresh(request.getState(),
            request.getUserId(), request.getManualAnnotation(), request.getNewTerms(),
            request.getPageNum(), request.getPageSize());

        PageVO pageVO = new PageVO(page);
        return ResultVO.success(pageVO);
    }
}
