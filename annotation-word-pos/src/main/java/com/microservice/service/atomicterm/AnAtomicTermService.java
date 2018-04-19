package com.microservice.service.atomicterm;

import cn.malgo.common.LogUtil;
import cn.malgo.common.security.SecurityUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.microservice.apiserver.vo.TermTypeVO;
import com.microservice.dataAccessLayer.entity.AnAtomicTerm;
import com.microservice.dataAccessLayer.entity.Concept;
import com.microservice.dataAccessLayer.mapper.AnAtomicTermMapper;
import com.microservice.dataAccessLayer.mapper.ConceptMapper;
import com.microservice.enums.CodeGenerateTypeEnum;
import com.microservice.result.MixtureTerm;
import com.microservice.sequence.SequenceGenerator;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by cjl on 2018/4/11.
 */
@Service
public class AnAtomicTermService {

    private Logger logger = Logger.getLogger(AnAtomicTermService.class);

    @Autowired
    private AnAtomicTermMapper anAtomicTermMapper;

    @Autowired
    private ConceptMapper conceptMapper;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    /**
     * 分页查询原子术语
     * @param term 传入明文
     * @param type
     * @param pageNum
     * @param pageSize
     * @param checked
     * @return
     */
    public Page<AnAtomicTerm> listAnAtomicTermByPagingCondition(String term, String type, String id , int pageNum, int pageSize, String checked) {
        Page<AnAtomicTerm> pageInfo= PageHelper.startPage(pageNum, pageSize);
        String termAfterDecrypt = SecurityUtil.cryptAESBase64(term);
        AnAtomicTerm paramAnAtomicTerm=new AnAtomicTerm();
        paramAnAtomicTerm.setId(id);
        paramAnAtomicTerm.setTerm(termAfterDecrypt);
        paramAnAtomicTerm.setType(type);
        anAtomicTermMapper.listAnAtomicTermJoinConcept(paramAnAtomicTerm,checked);
        pageInfo.getResult().stream()
                .forEach(x->x.setTerm(SecurityUtil.decryptAESBase64(x.getTerm())));
        return pageInfo;
    }

    /**
     * 模糊查询包含参数term的'ENABLE'状态的原子术语
     * @param term
     */
    public List<AnAtomicTerm> listAtomicTermByCondition(String term){
        AnAtomicTerm paramAnAtomicTerm=new AnAtomicTerm();
        paramAnAtomicTerm.setState("ENABLE");
        List<AnAtomicTerm> anAtomicTermList=anAtomicTermMapper.listAnAtomicTermBySelective(paramAnAtomicTerm);
        List<AnAtomicTerm> finalAtomicTermList=anAtomicTermList.stream()
                .filter(x->SecurityUtil.decryptAESBase64(x.getTerm()).contains(term)).collect(Collectors.toList());
        return  finalAtomicTermList;
    }

    /**
     * 根据termText模糊查询原子术语
     * @param mixtureTermList
     * @param term
     */
    public List<MixtureTerm> listMixtureTermByCondition(List<MixtureTerm> mixtureTermList, String term){
        List<AnAtomicTerm> anAtomicTermList=anAtomicTermMapper.listAnAtomicTermBySelective(new AnAtomicTerm());
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
        List<AnAtomicTerm> anAtomicTermList=anAtomicTermMapper.listAnAtomicTermJoinConcept(new AnAtomicTerm(),checked);
        anAtomicTermList.stream()
                .forEach(x->x.setTerm(SecurityUtil.decryptAESBase64(x.getTerm())));
        List<AnAtomicTerm> finalAtomicTermList=anAtomicTermList.stream().filter(x->x.getTerm().contains(term)).collect(Collectors.toList());
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
     * 批量更新原子术语表中的类型type
     * @param typeOld
     * @param typeNew
     */
    public void batchUpdateAtomicTermType(String typeOld,String typeNew){
        AnAtomicTerm anAtomicTerm=new AnAtomicTerm();
        anAtomicTerm.setType(typeOld);
        List<AnAtomicTerm> anAtomicTermList=anAtomicTermMapper.listAnAtomicTermBySelective(anAtomicTerm);
        if(anAtomicTermList.size()>0){
            List<String> idsList=new LinkedList<>();
            for(int k=0;k<anAtomicTermList.size();k++){
                idsList.add(anAtomicTermList.get(k).getId());
            }
            anAtomicTermMapper.batchUpdateAnAtomicTermTypeByIdArr(idsList,typeNew);
        }
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
        anAtomicTermMapper.listAnAtomicTermByConceptId(conceptId);
        pageInfo.getResult().stream()
                .forEach(x->x.setTerm(SecurityUtil.decryptAESBase64(x.getTerm())));
        return pageInfo;
    }
    /**
     * 根据原子术语ID查询原子术语
     * @param atomicTermId
     * @return
     */
    public AnAtomicTerm getAnAtomicTermById(String atomicTermId) {
        AnAtomicTerm anAtomicTerm = anAtomicTermMapper.getAnAtomicTerm(atomicTermId);
        anAtomicTerm.setTerm(SecurityUtil.decryptAESBase64(anAtomicTerm.getTerm()));
        return anAtomicTerm;
    }

    /**
     * 更新已经存在的原子术语
     * @param atomicTermId
     * @param termType
     */
    public void updateAtomicTerm(String atomicTermId, String termType) {
        AnAtomicTerm anAtomicTermOld = anAtomicTermMapper.getAnAtomicTerm(atomicTermId);
        anAtomicTermOld.setType(termType);
        anAtomicTermOld.setGmtModified(new Date());

        anAtomicTermMapper.updateAnAtomicTermSelective(anAtomicTermOld);
    }
    /**
     * 批量更新原子术语表记录的concept_id
     * @param idList
     * @param conceptId
     */
    public void updateBatchConceptIdOfAtomicTerm(List<String> idList, String conceptId){
        anAtomicTermMapper.batchUpdateAnAtomicTermConceptIdByIdArr(idList,conceptId);
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
        if(StringUtils.isNotBlank(conceptId)) {
            //当前concept表中已有该条记录，直接更新term表中的conceptID
            anAtomicTerm.setConceptId(conceptId);
        }else {
            Concept concept=new Concept();
            String pConceptId=sequenceGenerator.nextCodeByType(CodeGenerateTypeEnum.DEFAULT);
            anAtomicTerm.setConceptId(pConceptId);
            concept.setStandardName(conceptName);
            concept.setConceptId(pConceptId);
            conceptMapper.insertConceptSelective(concept);
        }
        anAtomicTermMapper.updateAnAtomicTermSelective(anAtomicTerm);
    }

    /**
     * 置空当前原子术语表记录的concept_id
     * @param id
     */
    public void clearConceptIdOfAtomicTerm(String id){
        anAtomicTermMapper.blankAnAtomicTermConceptId(id);
    }

    /**
     * 新增同义词并同时更新原子术语表的concept_id
     *@param id
     *@param originName
     */
    @Transactional
    public void saveConceptAndUpdateAtomicTerm(String id, String originName){
        Concept paramConcept=new Concept();
        paramConcept.setStandardName(originName);
        List<Concept> conceptList=conceptMapper.listConceptByCondition(paramConcept);
        if(conceptList.size()>0)
            return;
        String conceptId=sequenceGenerator.nextCodeByType(CodeGenerateTypeEnum.DEFAULT);
        AnAtomicTerm anAtomicTerm=new AnAtomicTerm();
        anAtomicTerm.setId(id);
        anAtomicTerm.setConceptId(conceptId);
        anAtomicTerm.setGmtModified(new Date());

        Concept concept=new Concept();
        concept.setStandardName(originName);
        concept.setConceptId(conceptId);

        conceptMapper.insertConceptSelective(concept);
        anAtomicTermMapper.updateAnAtomicTermConceptIdById(conceptId,id);
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
        AnAtomicTerm anAtomicTermOld = anAtomicTermMapper.getAnAtomicTermAndTypeTermNotNull(securityTerm,termType);
        if (anAtomicTermOld != null) {
            LogUtil.info(logger, MessageFormat.format("原子术语已经存在!术语:{0},类型:{1},ID值:{2}", term, termType,anAtomicTermOld.getId()));
            return;
        }

        String id = sequenceGenerator.nextCodeByType(CodeGenerateTypeEnum.DEFAULT);
        AnAtomicTerm anAtomicTermNew = new AnAtomicTerm();
        anAtomicTermNew.setId(id);
        anAtomicTermNew.setFromAnid(fromAnId);
        anAtomicTermNew.setTerm(securityTerm);
        anAtomicTermNew.setType(termType);
        anAtomicTermNew.setState("ENABLE");
        anAtomicTermNew.setGmtModified(new Date());
        anAtomicTermNew.setGmtModified(new Date());

        anAtomicTermMapper.insertAnAtomicTermSelective(anAtomicTermNew);
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
        anAtomicTermMapper.updateAnAtomicTermSelective(anAtomicTerm);
    }
}
