package cn.malgo.annotation.core.service.corpus;

import java.text.MessageFormat;
import java.util.Date;
import java.util.concurrent.Future;

import cn.malgo.annotation.common.dal.model.Corpus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import cn.malgo.annotation.common.dal.mapper.CorpusMapper;
import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.common.util.log.LogUtil;
import cn.malgo.annotation.core.tool.enums.term.TermStateEnum;
import cn.malgo.annotation.core.service.annotation.AsyncAnnotationService;

/**
 *
 * @author 张钟
 * @date 2017/10/18
 */
@Service
public class CorpusService {

    private Logger                 logger = Logger.getLogger(CorpusService.class);

    @Autowired
    private CorpusMapper corpusMapper;

    @Autowired
    private AsyncAnnotationService asyncAnnotationService;

    /**
     * 根据状态分页查询术语(task)
     * @param termStateEnum
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<Corpus> listCorpusByPagingCondition(TermStateEnum termStateEnum, int pageNum,
                                            int pageSize) {
        Page<Corpus> pageInfo = PageHelper.startPage(pageNum, pageSize);
        corpusMapper.selectByState(termStateEnum.name());
        return pageInfo;
    }



    /**
     * 更新术语的状态
     * @param termId
     * @param termStateEnum
     */
    public void updateTermState(String termId, TermStateEnum termStateEnum) {
        Corpus corpus = new Corpus();
        corpus.setId(termId);
        corpus.setState(termStateEnum.name());
        corpus.setGmtModified(new Date());
        int updateResult = corpusMapper.updateByPrimaryKeySelective(corpus);
        AssertUtil.state(updateResult > 0, "更新术语状态失败");
    }

    /**
     * 批量对Corpus表的原始文本进行自动标注
     */
    public void batchAutoAnnotation() {
        int batchCount = 1;
        int pageSize = 10;
        int success = 0;
        Page<Corpus> page = null;
        try {
            do {
                LogUtil.info(logger, MessageFormat.format("开始处理第{0}批次", batchCount));
                page = listCorpusByPagingCondition(TermStateEnum.INIT, 1, pageSize);
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
