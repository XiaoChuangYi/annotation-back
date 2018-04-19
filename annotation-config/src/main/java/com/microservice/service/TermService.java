package com.microservice.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.microservice.dataAccessLayer.entity.Term;
import com.microservice.dataAccessLayer.mapper.TermMapper;
import com.microservice.pojo.OriginNameGroup;
import com.microservice.pojo.TermLabel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cjl on 2018/4/11.
 */
@Service
public class TermService {

    @Autowired
    private TermMapper termMapper;


    /**
     * 根据conceptId分页查询
     * @param conceptId
     * @param pageNum
     * @param pageSize
     */
    public Page<Term> listTermByConceptId(String conceptId, int pageNum, int pageSize){
        Page<Term> pageInfo= PageHelper.startPage(pageNum,pageSize);
        Term term=new Term();
        term.setConceptId(conceptId);
        termMapper.listTermJoinConceptByTerm(term);
        return  pageInfo;
    }

    /**
     * 按条件分页查询术语
     * @param pageNum
     * @param pageSize
     * @param termName
     * @param termType
     * @param label
     */
    public Page<Term> listTermByPagingCondition(int pageNum, int pageSize,String termName,String termType,String label,String checked,String originName){
        Page<Term> pageInfo= PageHelper.startPage(pageNum,pageSize);
        Term paramTerm=new Term();
        paramTerm.setTermName(termName);
        paramTerm.setTermType(termType);
        paramTerm.setLabel(label);
        paramTerm.setOriginName(originName);
        termMapper.listTermJoinConcept(paramTerm,checked);
        return  pageInfo;
    }
    /**
     * 更据termId查询相关联的标准术语
     * @termId
     */
    public Page<Term> listTermAssociatedConceptByTermId(int pageNum,int pageSize,String termId){
        Page<Term> pageInfo= PageHelper.startPage(pageNum,pageSize);
        Term term=new Term();
        term.setTermId(termId);
        termMapper.listTermJoinConceptByTerm(term);
        return  pageInfo;
    }

    /**
     * 分页查询无过滤标准术语
     * @param pageNum
     * @param pageSize
     */
    public Page<Term> listTermByPaging(int pageNum, int pageSize){
        Page<Term> pageInfo= PageHelper.startPage(pageNum,pageSize);
        termMapper.listTermByCondition(new Term());
        return  pageInfo;
    }

    /**
     * 根据termName过滤查询标准术语
     * @param termName
     */
    public List<Term> listTermByTermName(String termName){
        Term term=new Term();
        term.setTermName(termName);
        List<Term> termList=termMapper.listTermByCondition(term);
        return termList;
    }

    /**
     * 去重查询出所有term_type字段
     */
    public List<String> listDistinctTermType(){
        List<String> typeList=termMapper.listDistinctTermType();
        return typeList;
    }

    /**
     *根据origin_name分组查询数据
     */
    public List<List<Term>> listTermGroupByOriginName(int groupIndex,int groupSize){
        int groups=(groupIndex==0?0:(groupIndex-1))*groupSize;
        List<OriginNameGroup> groupTermList=termMapper.listTermGroupByOriginName(groups,groupSize);
        List<List<Term>> finalList=new ArrayList<>();
        for(OriginNameGroup currentGroupTerm:groupTermList){
            System.out.println("当前的组名："+currentGroupTerm.getOriginName()+"组量的大小："+currentGroupTerm.getIdGroups());
            Term paramTerm=new Term();
            paramTerm.setOriginName(currentGroupTerm.getOriginName());
            List<Term> termList=termMapper.listTermJoinConceptByTerm(paramTerm);
            finalList.add(termList);
        }
        return  finalList;
    }

    /**
     * 更新术语部分字段
     * @param id
     * @param pTermId
     * @param termName
     */
    public  void updateTermSelective(int id,String pTermId,String termName){
        Term term=new Term();
        term.setId(id);
        term.setpTermId(pTermId);
        term.setTermName(termName);
        termMapper.updateTermSelective(term);
    }
    /**
     * 置空术语表的conceptId
     * @param  id
     */
    public void clearConceptIdOfTerm(int id){
        termMapper.blankTermConceptId(id);
    }

    /**
     * 批量更新选取行的conceptId
     */
    public void updateBatchTermConceptId(List<Integer> idList,String conceptId){
        termMapper.batchUpdateTermConceptId(idList,conceptId);
    }

    /**
     * 更新指定行的label字段
     * @param  id
     * @param  label
     */
    public  void updateTermLabel(int id,String label){
        Term term=new Term();
        term.setId(id);
        term.setLabel(label);
        termMapper.updateTermSelective(term);
    }
    /**
     *批量覆盖所选行的label字段
     */
    public void coverBatchTermLabel(List<Integer> idsList,String pLabel){
        if(idsList.size()>0){
            termMapper.batchCoverTermLabel(idsList,pLabel);
        }
    }

    /**
     *批量更新指定行的label字段
     */
    public void updateBatchTermLabel(List<Integer> idList,String pLabel){
        if(idList.size()>0){
            List<TermLabel> termLabelList=new ArrayList<>();
            for(int k=0;k<idList.size();k++){
                Term currentTerm=termMapper.getTermById(idList.get(k));
                TermLabel termLabel=new TermLabel();
                termLabel.setId(idList.get(k));
                if(currentTerm.getLabel()!=null&&!"".equals(currentTerm.getLabel())){
                    String [] labelArr=currentTerm.getLabel().substring(currentTerm.getLabel().indexOf("[")+1,currentTerm.getLabel().indexOf("]")).split(",");
                    List<String> labelList= new ArrayList<>();
                    for(String temp:labelArr){
                        labelList.add(temp);
                    }
                    String [] pLabelArr=pLabel.substring(pLabel.indexOf("[")+1,pLabel.indexOf("]")).split(",");
                    for(String str :pLabelArr){
                        if(!labelList.contains(str)){
                            labelList.add(str);
                        }
                    }
                    String temp="[";
                    for(String str:labelList){
                        temp+=str+",";
                    }
                    termLabel.setLabel(temp.substring(0,temp.length()-1)+"]");
                }else {
                    termLabel.setLabel(pLabel);
                }
                termLabelList.add(termLabel);
            }
            termMapper.batchUpdateTermLabel(termLabelList);
        }
    }
    /**
     *弃用术语
     * @param id
     * @param state
     */
    public  void  abandonTerm(int id,String state){
        Term term=new Term();
        term.setState(state);
        term.setId(id);
        termMapper.updateTermSelective(term);
    }

    /**
     *新增术语
     * @param termId
     * @param pTermId
     * @param termName
     * @param termCode
     * @param termType
     * @param label
     * @param state
     */
    public void saveTerm(String termId,String pTermId,String termName,
                         String termCode,String termType,String originName,String label,String state){
        Term term=new Term();
        term.setTermId(termId);
        term.setpTermId(pTermId);
        term.setTermName(termName);
        term.setOriginName(originName);
        term.setLabel(label);
        term.setTermCode(termCode);
        term.setState(state);
        term.setTermType(termType);
        termMapper.insertTermSelective(term);
    }
    /**
     * 删除术语
     * @param id
     */
    public void removeTerm(int id){
        termMapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据originName分组，查询对应的组数
     */
    public int countGroupsByOriginName(){
        return termMapper.countGroupsByOriginName();
    }

}
