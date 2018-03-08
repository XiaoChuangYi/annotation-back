package cn.malgo.annotation.core.service.term;

import cn.malgo.annotation.common.dal.mapper.TermMapper;
import cn.malgo.annotation.common.dal.model.*;
import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.core.business.mixtrue.MixtureTerm;
import cn.malgo.annotation.core.business.term.GroupTerm;
import cn.malgo.annotation.core.business.term.TermLabel;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by cjl on 2017/11/28.
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
    public Page<Term> listTermByConceptId(String conceptId,int pageNum,int pageSize){
        Page<Term> pageInfo=PageHelper.startPage(pageNum,pageSize);
        termMapper.selectByConceptId(conceptId);
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
    public Page<Term> listTermByPagingCondition(int pageNum, int pageSize,String termName,String termType,String label,String checked,String OriginName){
        Page<Term> pageInfo= PageHelper.startPage(pageNum,pageSize);
        termMapper.selectTermByCondition(termName,termType,label,checked,OriginName);
        return  pageInfo;
    }
    /**
     * 更据termId查询相关联的标准术语
     * @termId
     */
    public Page<Term> listTermAssociatedConceptByTermId(int pageNum,int pageSize,String termId){
        Page<Term> pageInfo= PageHelper.startPage(pageNum,pageSize);
        termMapper.selectTermByTermId(termId);
        return  pageInfo;
    }
    /**
     *分页查询术语
     * @param pageNum
     * @param pageSize
     */
    public Page<Term> listTermByPaging(int pageNum, int pageSize){
        Page<Term> pageInfo= PageHelper.startPage(pageNum,pageSize);
        termMapper.selectEnableTerm();
        return  pageInfo;
    }
    /**
     * @param  termName
     */
    public List<MixtureTerm> listMixtureTermByTermName(String termName){
        List<MixtureTerm> termList=termMapper.selectAllByTermName(termName);
        return termList;
    }
    public List<String> listDistinctTermType(){
        List<String> typeList=termMapper.selectTermType();
        return typeList;
    }
    /**
     *根据origin_name分组查询数据
     */
    public List<List<Term>> listTermGroupByOriginName(int groupIndex,int groupSize){
        int groups=(groupIndex==0?0:(groupIndex-1))*groupSize;
        List<GroupTerm> groupTermList=termMapper.selectGroupsAndOriginName(groups,groupSize);
        List<List<Term>> finalList=new ArrayList<>();
        System.out.println();
        for(GroupTerm currentGroupTerm:groupTermList){
            System.out.println("当前的组名："+currentGroupTerm.getOriginName()+"组量的大小："+currentGroupTerm.getIdGroups());
            List<Term> termList=termMapper.selectTermsByOriginName(currentGroupTerm.getOriginName());
            finalList.add(termList);
        }
        return  finalList;
    }
    /**
     *更新术语
     * @param id
     * @param pTermId
     * @param termName
     */
    public  void updateTerm(int id,String pTermId,String termName){
        Term term=new Term();
        term.setId(id);
        term.setPtermId(pTermId);
        term.setTermName(termName);
        int updateResult=termMapper.updateByPrimaryKeySelective(term);
        AssertUtil.state(updateResult > 0, "更新术语失败");
    }
    /**
     *置空术语表的conceptId
     * @param  id
     */
    public void clearConceptIdOfTerm(int id){
        Term pTerm=new Term();
        pTerm.setId(id);
        pTerm.setConceptId("");
        int updateResult=termMapper.updateByPrimaryKeySelective(pTerm);
        AssertUtil.state(updateResult > 0, "更新术语conceptId失败");
    }

    /**
     * 批量更新选取行的conceptId
     */
    public void updateBatchTermConceptId(List<Integer> idList,String conceptId){
        int updateResult=termMapper.updateBatchConceptIdOfTerm(idList,conceptId);
        AssertUtil.state(updateResult>0,"更新术语conceptId失败");
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
        int updateResult=termMapper.updateByPrimaryKeySelective(term);
        AssertUtil.state(updateResult > 0, "更新术语标签失败");
    }
    /**
     *批量覆盖所选行的label字段
     */
    public void coverBatchTermLable(List<Integer> idsList,String pLabel){
         if(idsList.size()>0){
             int updateBatch=termMapper.coverBatchLabelOfTerm(idsList,pLabel);
             AssertUtil.state(updateBatch>0,"批量更新术语标签失败");
         }
    }
    /**
     *批量更新指定行的label字段
     */
    public void updateBatchTermLabel(List<Integer> idList,String pLabel){
        if(idList.size()>0){
            List<TermLabel> termLabelList=new ArrayList<>();
            for(int k=0;k<idList.size();k++){
                Term currentTerm=termMapper.selectByPrimaryKeyID(idList.get(k));
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
            int updateResult=termMapper.updateBatchLabelOfTerm(termLabelList);
            AssertUtil.state(updateResult > 0, "批量更新术语标签失败");
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
        int deleteResult=termMapper.updateByPrimaryKeySelective(term);
        AssertUtil.state(deleteResult > 0, "弃用术语失败");
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
        term.setPtermId(pTermId);
        term.setTermName(termName);
        term.setOriginName(originName);
        term.setLabel(label);
        term.setTermCode(termCode);
        term.setState(state);
        term.setTermType(termType);
        int insertResult=termMapper.insertSelective(term);
        AssertUtil.state(insertResult > 0, "新增术语失败");
    }
    /**
     * 删除术语
     * @param id
     */
    public void removeTerm(int id){
        int deleteResult=termMapper.deleteByPrimaryKey(id);
        AssertUtil.state(deleteResult>0,"删除术语失败");
    }
    /**
     *获取当前术语表根据origin_name分组的组数
     */
    public int countTermGroupsByOriginName(){
        return termMapper.getGroupsByOriginName();
    }
}
