package cn.malgo.annotation.core.service.term;

import java.text.MessageFormat;
import java.util.Date;
import java.util.concurrent.Future;

import cn.malgo.annotation.common.util.AssertUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import cn.malgo.annotation.common.dal.mapper.AnTermMapper;
import cn.malgo.annotation.common.dal.model.AnTerm;
import cn.malgo.annotation.common.util.log.LogUtil;
import cn.malgo.annotation.core.model.enums.term.TermStateEnum;
import cn.malgo.annotation.core.service.annotation.AsyncAnnotationService;

/**
 *
 * @author 张钟
 * @date 2017/10/18
 */
@Service
public class TermService {

    private Logger                 logger = Logger.getLogger(TermService.class);

    @Autowired
    private AnTermMapper           anTermMapper;

    @Autowired
    private AsyncAnnotationService asyncAnnotationService;

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
    public AnTerm queryByTermId(String termId) {
        return anTermMapper.selectByPrimaryKey(termId);
    }

    /**
     * 更新术语的状态
     * @param termId
     * @param termStateEnum
     */
    public void updateTermState(String termId, TermStateEnum termStateEnum) {
        AnTerm anTerm = new AnTerm();
        anTerm.setId(termId);
        anTerm.setState(termStateEnum.name());
        anTerm.setGmtModified(new Date());
        int updateResult = anTermMapper.updateByPrimaryKeySelective(anTerm);
        AssertUtil.state(updateResult>0,"更新术语状态失败");
    }

    /**
     * 批量自动标注
     */
    public void batchAutoAnnotation() {
        int batchCount = 1;
        int pageSize = 10;
        int success = 0;
        Page<AnTerm> page = null;
        try {
            do {
                LogUtil.info(logger, MessageFormat.format("开始处理第{0}批次", batchCount));
                page = queryOnePageByState(TermStateEnum.INIT, 1, pageSize);
                Future<Boolean> future = asyncAnnotationService
                    .asyncAutoAnnotation(page.getResult());
                Boolean result = future.get();
                if (result) {
                    success = success + page.getResult().size();
                }
                LogUtil.info(logger, MessageFormat.format("结束处理第{0}批次", batchCount));

                batchCount++;

            } while (page.getTotal() > pageSize);

        } catch (Exception e) {
            LogUtil.error(logger, e, "预处理标注失败!terms:" + JSONObject.toJSONString(page.getResult()));
        }

    }
}
