package cn.malgo.annotation.core.service.atomicTerm;

import cn.malgo.annotation.common.dal.mapper.AnAtomicTermMapper;
import cn.malgo.annotation.common.dal.mapper.ConceptMapper;
import cn.malgo.annotation.common.dal.model.AnAtomicTerm;
import cn.malgo.annotation.common.dal.model.Concept;
import cn.malgo.annotation.core.business.mixtrue.MixtureTerm;
import cn.malgo.annotation.common.dal.sequence.CodeGenerateTypeEnum;
import cn.malgo.annotation.common.dal.sequence.SequenceGenerator;
import cn.malgo.annotation.common.service.integration.apiserver.vo.TermTypeVO;
import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.core.tool.enums.CommonStatusEnum;
import cn.malgo.common.LogUtil;
import cn.malgo.common.security.SecurityUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.*;

/**
 * Created by cjl on 2018/3/7.
 */
@Service
public class AtomicTermService {

    private Logger logger = Logger.getLogger(AtomicTermService.class);

    @Autowired
    private SequenceGenerator sequenceGenerator;

    @Autowired
    private AnAtomicTermMapper anAtomicTermMapper;


    @Autowired
    private ConceptMapper conceptMapper;

    /**
     * 分页查询全部原子术语
     * @param pageNum
     * @param pageSize
     */
    public Page<AnAtomicTerm> listAnAtomicTermByPaging(int pageNum, int pageSize){
        Page<AnAtomicTerm> pageInfo= PageHelper.startPage(pageNum,pageSize);
        anAtomicTermMapper.selectAllAtomicTerm();
        decrypt(pageInfo);
        return  pageInfo;
    }


    /**
     *查询所有的'ENABLE'状态的原子术语
     */
    public List<AnAtomicTerm> listAtomicTermByCondition(String term){
        List<AnAtomicTerm> anAtomicTermList=anAtomicTermMapper.fuzzyQueryByTermAndType(null,null);
        List<AnAtomicTerm> finalAtomicTermList=decryptTerm(anAtomicTermList,term);
        return  finalAtomicTermList;
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
    public Page<AnAtomicTerm> listAnAtomicTermByPagingCondition(String term, String type,String id ,int pageNum, int pageSize,String checked) {
        Page<AnAtomicTerm> pageInfo=PageHelper.startPage(pageNum, pageSize);
        String termAfterDecrypt = SecurityUtil.cryptAESBase64(term);
        anAtomicTermMapper.selectByTermAndTypeIsSynonyms(termAfterDecrypt,type,id,checked);
        decrypt(pageInfo);
        return pageInfo;
    }
//    /**
//     * 分页查询原子术语
//     * @param term 传入明文
//     * @param type
//     * @param pageNum
//     * @param pageSize
//     * @return
//     */
//    public Page<AnAtomicTerm> listAnAtomicTermByPagingCondition(String term, String type, int pageNum, int pageSize) {
//        String termAfterDecrypt = SecurityUtil.cryptAESBase64(term);
//        Page<AnAtomicTerm> pageInfo = PageHelper.startPage(pageNum, pageSize);
//        anAtomicTermMapper.selectByTermAndType(termAfterDecrypt, type);
//        decrypt(pageInfo);
//        return pageInfo;
//    }
    /**
     * 根据termText模糊查询原子术语
     * @param mixtureTermList
     * @param term
     */
    public List<MixtureTerm> listMixtureTermByCondition(List<MixtureTerm> mixtureTermList, String term){
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
    public Map<String,Object> mapAnAtomicTermByPagingCondition(String term, int pageIndex, int pageSize, String checked){
        //从数据库中查出所有的数据然后遍历每条记录，匹配是否有指定的记录，有则加入新的集合，直到结束，返回最终的集合
        List<AnAtomicTerm> anAtomicTermList=anAtomicTermMapper.selectAllByCondition(checked);
        List<AnAtomicTerm> finalAtomicTermList=decryptTerm(anAtomicTermList,term);
        //根据term/checked查询出的数据的总条数，除以前台的pageSize,获取到当前后台可以分几页，
        int pages=finalAtomicTermList.size()/pageSize;
        int rest=finalAtomicTermList.size()%pageSize;
        Map<String,Object> termMap=new HashMap<>();
        if(pages==0&&rest>=0) {
            termMap.put("total",finalAtomicTermList.size());
            termMap.put("atomicTermList",finalAtomicTermList);
            return termMap;
        }
        if(pageIndex<=pages){
            termMap.put("total",finalAtomicTermList.size());
            termMap.put("atomicTermList", finalAtomicTermList.subList((pageIndex - 1) * pageSize,
                    pageIndex * pageSize >= finalAtomicTermList.size() ? finalAtomicTermList.size() : pageIndex * pageSize));
        }
        if(pageIndex==(pages+1)&&rest>0){
            termMap.put("total",finalAtomicTermList.size());
            termMap.put("atomicTermList",finalAtomicTermList.subList((pageIndex-1)*pageSize,finalAtomicTermList.size()));
        }
        if(pageIndex>(pages+1)){
            //说明当前的分页的索引超过了，默认返回第一页数据过去
            termMap.put("total",finalAtomicTermList.size());
            termMap.put("atomicTermList",finalAtomicTermList.subList(0,pageSize));
        }
        return termMap;
    }


    /**
     * 根据conceptId分页查询原子术语
     * @param conceptId
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<AnAtomicTerm> listAnAtomicTermAssociatedConceptByConceptId(String conceptId, int pageNum, int pageSize) {
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
    public AnAtomicTerm getAnAtomicTermById(String atomicTermId) {
        AnAtomicTerm anAtomicTerm = anAtomicTermMapper.selectByPrimaryKeyID(atomicTermId);
        decrypt(anAtomicTerm);
        return anAtomicTerm;
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
     * 批量更新原子术语表记录的concept_id
     * @param idList
     * @param conceptId
     */
    public void updateBatchConceptIdOfAtomicTerm(List<String> idList, String conceptId){
        int updateResult=anAtomicTermMapper.batchUpdateAtomicConceptId(idList,conceptId);
        AssertUtil.state(updateResult > 0, "更新原子术语concept_id字段失败");
    }



    /**
     * 更新将当前原子术语的conceptId更新为一个新的conceptId,如果concept表有则更新
     *@param  id
     *@param  conceptId
     */
    @Transactional
    public void updateAtomicTermOrAddConcept(String id,String conceptId,String conceptName){
        AnAtomicTerm anAtomicTerm=new AnAtomicTerm();
        anAtomicTerm.setId(id);
        anAtomicTerm.setGmtModified(new Date());
        if(conceptId!=null&&!"".equals(conceptId)) {
            //当前concept表中已有该条记录，直接更新term表中的conceptID
            anAtomicTerm.setConceptId(conceptId);
        }else {
            Concept concept=new Concept();
            String pConceptId=sequenceGenerator.nextCodeByType(CodeGenerateTypeEnum.DEFAULT);
            anAtomicTerm.setConceptId(pConceptId);
            concept.setStandardName(conceptName);
            concept.setConceptId(pConceptId);
            int insertResult=conceptMapper.insertUseGeneratedKeys(concept);
            AssertUtil.state(insertResult > 0, "插入概念失败");
        }
        int updateResult=anAtomicTermMapper.updateByPrimaryKeySelective(anAtomicTerm);
        AssertUtil.state(updateResult > 0, "更新原子术语失败");
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
     * 新增同义词并同时更新原子术语表的concept_id
     *@param id
     *@param originName
     */
    @Transactional
    public void saveConceptAndUpdateAtomicTerm(String id, String originName){
        List<Concept> conceptList=conceptMapper.selectConceptByStandardName(originName);
        AssertUtil.state(conceptList.size()==0,"同义词表中已有"+originName+"记录");
        String conceptId=sequenceGenerator.nextCodeByType(CodeGenerateTypeEnum.DEFAULT);
        AnAtomicTerm anAtomicTerm=new AnAtomicTerm();
        anAtomicTerm.setId(id);
        anAtomicTerm.setConceptId(conceptId);
        anAtomicTerm.setGmtModified(new Date());

        Concept concept=new Concept();
        concept.setStandardName(originName);
        concept.setConceptId(conceptId);

        int insertResult=conceptMapper.insertUseGeneratedKeys(concept);
        AssertUtil.state(insertResult > 0, "新增概念失败");
        int updateResult=anAtomicTermMapper.updateConceptIdByPrimaryKey(conceptId,id);
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
        AnAtomicTerm anAtomicTermOld = anAtomicTermMapper.selectByTermAndTypeNotNull(securityTerm,termType);
        if (anAtomicTermOld != null) {
            LogUtil.info(logger, MessageFormat.format("原子术语已经存在!术语:{0},类型:{1},ID值:{2}", term, termType,anAtomicTermOld.getId()));
            return;
        }

        String id = sequenceGenerator.nextCodeByType(CodeGenerateTypeEnum.DEFAULT);
        AnAtomicTerm anAtomicTermNew = new AnAtomicTerm();
        anAtomicTermNew.setId(id);
        anAtomicTermNew.setFromAnId(fromAnId);
        anAtomicTermNew.setTerm(securityTerm);
        anAtomicTermNew.setType(termType);
        anAtomicTermNew.setState(CommonStatusEnum.ENABLE.name());
        anAtomicTermNew.setGmtModified(new Date());
        anAtomicTermNew.setGmtModified(new Date());

        int insertResult = anAtomicTermMapper.insertSelective(anAtomicTermNew);
        AssertUtil.state(insertResult > 0, "保存原子术语失败");
    }

    /**
     * 遗弃原子术语
     * @param id
     * @param state
     */
    public  void  abandonAtomicTerm(String id,String state){
        AnAtomicTerm anAtomicTerm=new AnAtomicTerm();
        anAtomicTerm.setState(state);
        anAtomicTerm.setId(id);
        int deleteResult=anAtomicTermMapper.updateByPrimaryKeySelective(anAtomicTerm);
        AssertUtil.state(deleteResult > 0, "删除术语失败");
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
     * 解密原子术语
     * @param anAtomicTerm
     * @return
     */
    private AnAtomicTerm decrypt(AnAtomicTerm anAtomicTerm) {
        anAtomicTerm.setTerm(SecurityUtil.decryptAESBase64(anAtomicTerm.getTerm()));
        return anAtomicTerm;
    }
}
