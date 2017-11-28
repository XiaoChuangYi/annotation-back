package cn.malgo.annotation.web.controller.knowledge;

import cn.malgo.annotation.common.dal.model.ConceptShow;
import cn.malgo.annotation.core.service.type.ConceptShowService;
import cn.malgo.annotation.web.controller.common.BaseController;
import cn.malgo.annotation.web.result.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by cjl on 2017/11/22.
 */
@RestController
@RequestMapping(value = "/knowledge")
public class ConceptController extends BaseController {

    @Autowired
    private ConceptShowService conceptShowService;

    @RequestMapping(value = "/getChildrenConcept.do")
    public ResultVO<List<ConceptShow>> getAllConceptById(String conceptId){
        List<ConceptShow> roleVOList= conceptShowService.selectAllByConcepId(conceptId);
        return ResultVO.success(roleVOList);
    }
}
