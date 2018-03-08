package cn.malgo.annotation.core.service.tag;

import cn.malgo.annotation.common.dal.mapper.TagMapper;
import cn.malgo.annotation.common.dal.model.Tag;
import cn.malgo.annotation.common.util.AssertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by cjl on 2017/11/30.
 */
@Service
public class TagService {
    @Autowired
    private TagMapper tagMapper;

    /**
     *查询所有标签
     */
    public List<Tag> listTag(){
        List<Tag> tagList=tagMapper.selectAll();
        return tagList;
    }
    /**
     *新增标签
     */
    public void saveTag(String tagName){
        Tag tag=new Tag();
        tag.setTagName(tagName);
        int insertResult=tagMapper.insertUseGeneratedKeys(tag);
        AssertUtil.state(insertResult>0,"插入标签失败！");
    }
    /**
     *新增标签列表
     */
    public  void saveTagAggregate(List<String> tags){
        int insertResult=tagMapper.insertBatch(tags);
        AssertUtil.state(insertResult>0,"批量插入标签失败！");
    }
    /**
     *
     */
}
