package cn.malgo.annotation.web.controller.tag;

import cn.malgo.annotation.common.dal.model.Tag;
import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.core.service.tag.TagService;
import cn.malgo.annotation.web.controller.common.BaseController;
import cn.malgo.annotation.web.controller.tag.request.TagArr;
import cn.malgo.annotation.web.result.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by cjl on 2017/11/30.
 */
@RestController
@RequestMapping(value = "/tag")
public class TagController  extends BaseController {

    @Autowired
    private TagService tagService;

    @RequestMapping(value="/getAllTags.do")
    public ResultVO<List<Tag>>  getAllTags(){
        List<Tag> tagList=tagService.listTag();
        return  ResultVO.success(tagList);
    }

    @RequestMapping(value = "/addTag.do")
    public ResultVO addTag(String tagName){
        AssertUtil.notBlank(tagName,"标签名称为空！");
        tagService.saveTag(tagName);
        return  ResultVO.success();
    }
    /**
     *批量新增标签
     */
    @RequestMapping(value = "/addTags.do")
    public ResultVO addTags(TagArr tagArr){
        AssertUtil.notEmpty(tagArr.getTagList(),"标签列表为空！");
        tagService.saveTagAggregate(tagArr.getTagList());
        return  ResultVO.success();
    }

}
