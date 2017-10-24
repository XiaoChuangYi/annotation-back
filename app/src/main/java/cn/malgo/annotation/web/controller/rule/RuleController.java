package cn.malgo.annotation.web.controller.rule;

import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.malgo.annotation.common.dal.model.CrmAccount;
import cn.malgo.annotation.common.dal.model.RlEntityRule;
import cn.malgo.annotation.core.service.rule.RuleService;
import cn.malgo.annotation.web.controller.common.BaseController;
import cn.malgo.annotation.web.controller.rule.request.RuleAddRequest;
import cn.malgo.annotation.web.controller.rule.request.RulePageQueryRequest;
import cn.malgo.annotation.web.controller.rule.request.RuleUpdateRequest;
import cn.malgo.annotation.web.result.PageVO;
import cn.malgo.annotation.web.result.ResultVO;

/**
 * @author 张钟
 * @date 2017/10/24
 */

@RestController
@RequestMapping(value = "/rule")
public class RuleController extends BaseController {

    @Autowired
    private RuleService ruleService;

    /**
     * 新增规则
     * @param request
     * @param crmAccount
     * @return
     */
    @RequestMapping(value = "/add.do")
    public ResultVO addRule(RuleAddRequest request,
                            @ModelAttribute("currentAccount") CrmAccount crmAccount) {
        RuleAddRequest.check(request);

        ruleService.saveRule(request.getRuleName(), request.getRuleValue(), request.getRuleType(),
            request.getMemo());

        return ResultVO.success();
    }

    /**
     * 更新规则
     * @param request
     * @param crmAccount
     * @return
     */
    @RequestMapping(value = "/update.do")
    public ResultVO updateRule(RuleUpdateRequest request,
                               @ModelAttribute("currentAccount") CrmAccount crmAccount) {
        RuleUpdateRequest.check(request);

        ruleService.modifyRuleValue(request.getRuleId(), request.getRuleName(),
            request.getRuleValue(), request.getRuleType(), request.getMemo());

        return ResultVO.success();

    }

    /**
     * 分页查询规则列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/list.do")
    public ResultVO<PageVO<RlEntityRule>> getOnePage(RulePageQueryRequest request) {

        Page<RlEntityRule> page = ruleService.queryOnePage(request.getRuleName(), request.getRuleValue(),
            request.getRuleType(), request.getState(), request.getPageNum(), request.getPageSize());

        PageVO<RlEntityRule> result = new PageVO(page);

        return ResultVO.success(result);
    }

}
