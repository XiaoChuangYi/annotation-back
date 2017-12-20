package cn.malgo.annotation.core.service.corpus;

import java.text.MessageFormat;
import java.util.*;

import cn.malgo.annotation.common.dal.model.MixtureTerm;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import cn.malgo.annotation.common.dal.mapper.AnAtomicTermMapper;
import cn.malgo.annotation.common.dal.model.AnAtomicTerm;
import cn.malgo.annotation.common.dal.sequence.CodeGenerateTypeEnum;
import cn.malgo.annotation.common.dal.sequence.SequenceGenerator;
import cn.malgo.annotation.common.service.integration.apiserver.vo.TermTypeVO;
import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.core.model.enums.CommonStatusEnum;
import cn.malgo.common.LogUtil;
import cn.malgo.common.security.SecurityUtil;

/**
 *
 * @author 张钟
 * @date 2017/10/23
 */
@Service
public class AtomicTermService {

    private Logger             logger = Logger.getLogger(AtomicTermService.class);

    @Autowired
    private SequenceGenerator  sequenceGenerator;

    @Autowired
    private AnAtomicTermMapper anAtomicTermMapper;

    /**
     * 批量更新原子术语表记录的concept_id
     * @param idList
     * @param conceptId
     */
    public void updateBatchConceptIdOfAtomicTerm(List<String> idList,String conceptId){
        int updateResult=anAtomicTermMapper.batchUpdateAtomicConceptId(idList,conceptId);
        AssertUtil.state(updateResult > 0, "更新原子术语concept_id字段失败");
    }
    /**
     * 置空当前原子术语表记录的concept_id
     * @param id
     */
    public void clearConceptIdOfAtomicTerm(String id){
        int updateResult=anAtomicTermMapper.updateConceptIdByPrimaryKey("",id);
        AssertUtil.state(updateResult > 0, "更新原子术语失败");
    }

    /**
     * 保存原子术语,如果术语已经存在,更新,否则,新增
     * @param termTypeVO
     * @param fromAnId 新词来源的标注ID
     */
    public void saveAtomicTerm(String fromAnId, TermTypeVO termTypeVO) {

        saveAtomicTerm(fromAnId, termTypeVO.getTerm(), termTypeVO.getType());

    }

    /**
     * 保存原子术语,如果术语已经存在,更新,否则,新增
     * @param fromAnId 新词来源的标注ID
     * @param term
     * @param termType
     */
    public void saveAtomicTerm(String fromAnId, String term, String termType) {

        String securityTerm = SecurityUtil.cryptAESBase64(term);

        AnAtomicTerm anAtomicTermOld = anAtomicTermMapper.selectByTermAndTypeNotNull(securityTerm,
            termType);
        if (anAtomicTermOld != null) {
            LogUtil.info(logger, MessageFormat.format("原子术语已经存在!术语:{0},类型:{1}", term, termType));
            return;
        }

        String id = sequenceGenerator.nextCodeByType(CodeGenerateTypeEnum.DEFAULT);
        AnAtomicTerm anAtomicTermNew = new AnAtomicTerm();
        anAtomicTermNew.setId(id);
        anAtomicTermNew.setFromAnId(fromAnId);
        anAtomicTermNew.setTerm(securityTerm);
        anAtomicTermNew.setType(termType);
        anAtomicTermNew.setState(CommonStatusEnum.ENABLE.name());

        int insertResult = anAtomicTermMapper.insertSelective(anAtomicTermNew);
        AssertUtil.state(insertResult > 0, "保存原子术语失败");

    }

    /**
     * 更新已经存在的原子术语
     * @param atomicTermId
     * @param termType
     */
    public void updateAtomicTerm(String atomicTermId, String termType) {
        AnAtomicTerm anAtomicTermOld = anAtomicTermMapper.selectByPrimaryKeyID(atomicTermId);
        anAtomicTermOld.setType(termType);
        anAtomicTermOld.setGmtModified(new Date());

        int updateResult = anAtomicTermMapper.updateByPrimaryKeySelective(anAtomicTermOld);
        AssertUtil.state(updateResult > 0, "更新原子术语失败");
    }

    /**
     *分页查询全部原子术语
     * @param pageNum
     * @param pageSize
     */
    public Page<AnAtomicTerm> QueryAll(int pageNum,int pageSize){
        Page<AnAtomicTerm> pageInfo=PageHelper.startPage(pageNum,pageSize);
        anAtomicTermMapper.selectAll();
        decrypt(pageInfo);
        return  pageInfo;
    }

    /**
     * 分页模糊查询原子术语
     * @param term 传入明文
     * @param type
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<AnAtomicTerm> fuzzyQueryOnePage(String term, String type, int pageNum, int pageSize){
        String termAfterDecrypt = SecurityUtil.cryptAESBase64(term);
        Page<AnAtomicTerm> pageInfo = PageHelper.startPage(pageNum, pageSize);
        anAtomicTermMapper.fuzzyQueryByTermAndType(termAfterDecrypt, type);
        decrypt(pageInfo);
        return pageInfo;
    }

    /**
     * 分页查询原子术语
     * @param term 传入明文
     * @param type
     * @param pageNum
     * @param pageSize
     * @param checked
     * @return
     */
    public Page<AnAtomicTerm> queryOnePage(String term, String type,String id ,int pageNum, int pageSize,String checked) {
        Page<AnAtomicTerm> pageInfo=PageHelper.startPage(pageNum, pageSize);
        String termAfterDecrypt = SecurityUtil.cryptAESBase64(term);
        anAtomicTermMapper.selectByTermAndTypeIsSynonyms(termAfterDecrypt,type,id,checked);
        decrypt(pageInfo);
        return pageInfo;
    }
    /**
     * 根据termText模糊查询原子术语
     * @param mixtureTermList
     * @param term
     */
    public List<MixtureTerm> queryMixtureFuzzyByTerm(List<MixtureTerm> mixtureTermList,String term){
        List<AnAtomicTerm> anAtomicTermList=anAtomicTermMapper.selectAllAtomicTerm();
        for(AnAtomicTerm anAtomicTerm:anAtomicTermList){
            String currentTerm=SecurityUtil.decryptAESBase64(anAtomicTerm.getTerm());
            if(currentTerm.contains(term)){
                MixtureTerm current=new MixtureTerm();
                current.setTermName(currentTerm);
                current.setTermId(anAtomicTerm.getId());
                mixtureTermList.add(current);
            }
        }
        return  mixtureTermList;
    }
    /**
     * 分页根据term模糊查询原子术语
     * @param term
     * @param checked
     */
    public Map<String,Object> queryFuzzyByTerm(String term, int pageIndex, int pageSize, String checked){
        //从数据库中查出所有的数据然后遍历每条记录，匹配是否有指定的记录，有则加入新的集合，直到结束，返回最终的集合
        List<AnAtomicTerm> anAtomicTermList=anAtomicTermMapper.selectAllByCondition(checked);
        List<AnAtomicTerm> finalAtomicTermList=decryptTerm(anAtomicTermList,term);
        Map<String,Object> termMap=new HashMap<>();
        if(finalAtomicTermList.size()==0){
            termMap.put("total",finalAtomicTermList.size());
            termMap.put("atomicTermList",finalAtomicTermList);
        }else {
            termMap.put("total", finalAtomicTermList.size());
            termMap.put("atomicTermList", finalAtomicTermList.subList((pageIndex - 1) * pageSize,
                    pageIndex * pageSize >= finalAtomicTermList.size() ? finalAtomicTermList.size() : pageIndex * pageSize));
        }
        return termMap;
    }

    private List<AnAtomicTerm> decryptTerm(List<AnAtomicTerm> list,String term){
        List<AnAtomicTerm> anAtomicTermList=new LinkedList<>();
        for (AnAtomicTerm anAtomicTerm : list) {
            String currentTerm=SecurityUtil.decryptAESBase64(anAtomicTerm.getTerm());
            if(currentTerm.contains(term)){
                anAtomicTerm.setTerm(currentTerm);
                anAtomicTermList.add(anAtomicTerm);
            }
        }
        return anAtomicTermList;
    }
    /**
     * 分页查询原子术语
     * @param term 传入明文
     * @param type
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<AnAtomicTerm> queryOnePage(String term, String type, int pageNum, int pageSize) {
        String termAfterDecrypt = SecurityUtil.cryptAESBase64(term);
        Page<AnAtomicTerm> pageInfo = PageHelper.startPage(pageNum, pageSize);
        anAtomicTermMapper.selectByTermAndType(termAfterDecrypt, type);
        decrypt(pageInfo);
        return pageInfo;
    }

    /**
     * 根据conceptId分页查询原子术语
     * @param conceptId
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<AnAtomicTerm> queryOnePageByConceptId(String conceptId, int pageNum, int pageSize) {
        Page<AnAtomicTerm> pageInfo = PageHelper.startPage(pageNum, pageSize);
        anAtomicTermMapper.selectAllByConceptId(conceptId);
        decrypt(pageInfo);
        return pageInfo;
    }
    /**
     * 根据原子术语ID查询原子术语
     * @param atomicTermId
     * @return
     */
    public AnAtomicTerm queryByAtomicTermId(String atomicTermId) {
        AnAtomicTerm anAtomicTerm = anAtomicTermMapper.selectByPrimaryKeyID(atomicTermId);
        decrypt(anAtomicTerm);
        return anAtomicTerm;
    }

    /**
     * 根据原子术语文本查询
     * @param term
     * @return
     */
    public List<AnAtomicTerm> queryByAtomicTerm(String term) {
        String termAfterDecrypt = SecurityUtil.cryptAESBase64(term);
        List<AnAtomicTerm> anAtomicTermList = anAtomicTermMapper.selectByTerm(termAfterDecrypt);
        decrypt(anAtomicTermList);
        return anAtomicTermList;
    }

    /**
     * 解密原子术语的分页查询结果
     * @param page
     * @return
     */
    private Page<AnAtomicTerm> decrypt(Page<AnAtomicTerm> page) {
        for (AnAtomicTerm anAtomicTerm : page.getResult()) {
            decrypt(anAtomicTerm);
        }
        return page;
    }

    /**
     * 批量解密
     * @param anAtomicTermList
     * @return
     */
    public List<AnAtomicTerm> decrypt(List<AnAtomicTerm> anAtomicTermList){
        for (AnAtomicTerm anAtomicTerm :anAtomicTermList) {
            decrypt(anAtomicTerm);
        }
        return anAtomicTermList;
    }

    /**
     * 解密原子术语
     * @param anAtomicTerm
     * @return
     */
    private AnAtomicTerm decrypt(AnAtomicTerm anAtomicTerm) {
        anAtomicTerm.setTerm(SecurityUtil.decryptAESBase64(anAtomicTerm.getTerm()));
        return anAtomicTerm;
    }

}
