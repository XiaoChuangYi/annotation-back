package cn.malgo.annotation.core.service.term;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import cn.malgo.annotation.common.dal.mapper.AnTermMapper;
import cn.malgo.annotation.common.dal.model.AnTerm;
import cn.malgo.annotation.core.model.enums.term.TermStateEnum;

/**
 *
 * @author 张钟
 * @date 2017/10/18
 */
@Service
public class TermService {

    @Autowired
    private AnTermMapper anTermMapper;

    /**
     * 根据状态分页查询术语(term)
     * @param termStateEnum
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<AnTerm> queryOnePageByState(TermStateEnum termStateEnum, int pageNum,
                                            int pageSize) {
        Page<AnTerm> pageInfo = PageHelper.startPage(pageNum, pageSize);
        anTermMapper.selectByState(termStateEnum.name());
        return pageInfo;
    }

    /**
     * 通过ID查询术语
     * @param termId
     * @return
     */
    public AnTerm queryByTermId(String termId){
        return anTermMapper.selectByPrimaryKey(termId);
    }
}
