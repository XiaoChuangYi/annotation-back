package cn.malgo.annotation.core.service.type;

import cn.malgo.annotation.common.dal.mapper.AnAtomicTermMapper;
import cn.malgo.annotation.common.dal.mapper.CorpusMapper;
import cn.malgo.annotation.common.dal.mapper.AnTypeMapper;
import cn.malgo.annotation.common.dal.model.*;
import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.core.model.annotation.TermAnnotationModel;
import cn.malgo.annotation.core.model.convert.AnnotationConvert;
import cn.malgo.annotation.core.model.enums.annotation.AnnotationStateEnum;
import cn.malgo.annotation.core.model.enums.annotation.TypeStateEnum;
import cn.malgo.annotation.core.service.annotation.AnnotationService;
import cn.malgo.common.LogUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mysql.jdbc.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by cjl on 2017/11/20.
 */
@Service
public class TypeService {

    private Logger logger = Logger.getLogger(TypeService.class);

    @Autowired
    private AnTypeMapper anTypeMapper;

    @Autowired
    private AnAtomicTermMapper anAtomicTermMapper;

    @Autowired
    private CorpusMapper corpusMapper;

    @Autowired
    private AnnotationService annotationService;


    /**
     * 根据ID查询其子数据
     * @param Id
     */
    public  List<AnType> selectAllTypesById(String Id){
        List<AnType> anTypeList=anTypeMapper.selectTypeByTypeId(Id);
        System.out.println(">>>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<");
        System.out.println(JSONArray.parse(JSON.toJSONString(anTypeList)));
        return  anTypeList;
    }
    /**
     * 查询所有类型
     */
    public Page<AnType> selectPaginationTypes(int pageNum,int pageSize){
        Page<AnType> pageInfo= PageHelper.startPage(pageNum, pageSize);
        anTypeMapper.selectEnableTypes();
        return pageInfo;
    }
    /**
     * 查询所有类型
     */
    public Page<AnType> selectPaginationTypesAndShowParent(int pageNum,int pageSize,String typeCode,String typeName){
        Page<AnType> pageInfo= PageHelper.startPage(pageNum, pageSize);
        anTypeMapper.selectEnableTypeAndShowParent(typeCode,typeName);
        return pageInfo;
    }
    /**
     * 查询所有类型
     */
    public List<AnType> selectAllTypes(){
        List<AnType> anTypeList=anTypeMapper.selectEnableTypes();
        return anTypeList;
    }
    /**
     * 根据id更新type表中的typeCode
     * @param id
     * @param typeCode
     */
    public void updateTypeCodeById(String id,String typeCode){
        anTypeMapper.updateTypeCodeById(typeCode,id);
    }
    /**
     * 批量更新原子术语表和标注表中的类型type
     *@param typeOld
     *@param typeNew
     */
    public void updateBatchTypeOnAtomicTerm(String typeOld,String typeNew){
        List<AnAtomicTerm> anAtomicTermList=anAtomicTermMapper.selectAtomicIDsByOldType(typeOld);
        if(anAtomicTermList.size()>0){
            List<String> idsList=new LinkedList<>();
            for(int k=0;k<anAtomicTermList.size();k++){
                idsList.add(anAtomicTermList.get(k).getId());
            }
            anAtomicTermMapper.batchUpdateAtomicType(idsList,typeNew);
        }
    }
    /**
     * 批量更新原子术语表和标注表中的类型type
     *@param typeOld
     *@param typeNew
     */
    public  void  updateBatchTypeOnTerm(String typeOld,String typeNew){
        List<Corpus> corpusList = corpusMapper.selectAnTermIDsByOldType(typeOld);
        if(corpusList.size()>0){
            List<String> idsList=new LinkedList<>();
            for(int k = 0; k< corpusList.size(); k++){
                idsList.add(corpusList.get(k).getId());
            }
            corpusMapper.batchUpdateAnTermType(idsList,typeNew);
        }
    }
    /**
     * 更新type多字段
     * @param id
     * @param parentId
     * @param typeName
     * */
    @Transactional
    public void updateType(String parentId,String id,String typeName,String originParentId){
        AnType anTypeParam=new AnType();
        anTypeParam.setId(id);
        anTypeParam.setTypeName(typeName);
        anTypeParam.setGmtModified(new Date());
        //如果当前的parentId不为空，则说明用户要改变当前的记录的父类型
        //根据当前的parentId作为id,将对应的记录的has_children字段+1
        //根据当前的originParentId,查询出当前记录原先所归属的type记录。
        // 1.如果没有，则说明当前行为顶级父类型，不用管
        // 2.如果有记录，则说明当前行曾经归属于该type类型下，对该hasChildren字段-1
        if(!StringUtils.isNullOrEmpty(parentId)){
            anTypeParam.setParentId(parentId);
            AnType anTypeForUpdateNum=anTypeMapper.selectTypeByParentId(parentId);
            if(anTypeForUpdateNum!=null){
                anTypeForUpdateNum.setHasChildren(anTypeForUpdateNum.getHasChildren()+1);
                int updateHasChildren=anTypeMapper.updateByPrimaryKeySelective(anTypeForUpdateNum);
                AssertUtil.state(updateHasChildren > 0, "更新hasChildren字段失败");
            }
        }
        if(!StringUtils.isNullOrEmpty(originParentId)) {
            AnType anTypeForUpdateSubtractNum = anTypeMapper.selectTypeByParentId(originParentId);
            if(anTypeForUpdateSubtractNum!=null){
                if(anTypeForUpdateSubtractNum.getHasChildren()>0)
                {
                    anTypeForUpdateSubtractNum.setHasChildren(anTypeForUpdateSubtractNum.getHasChildren()-1);
                    int updateHasChildren=anTypeMapper.updateByPrimaryKeySelective(anTypeForUpdateSubtractNum);
                    AssertUtil.state(updateHasChildren > 0, "更新hasChildren字段失败");
                }
            }
        }
        int updateResult=anTypeMapper.updateByPrimaryKeySelective(anTypeParam);
        AssertUtil.state(updateResult > 0, "更新类型名称失败");
    }
    /**
     * 新增type
     * @param typeName
     * */
    @Transactional
    public void insertType(String parentId,String typeName,String typeCode){
        AnType anTypeOld=anTypeMapper.selectTypeByTypeCodeEnable(typeCode);
        AssertUtil.state(anTypeOld==null,"该type类型已经存在");
        AnType anTypeNew=new AnType();
        anTypeNew.setParentId(parentId);
        anTypeNew.setTypeName(typeName);
        anTypeNew.setTypeCode(typeCode);
        anTypeNew.setState(TypeStateEnum.ENABLE.name());
        anTypeNew.setGmtCreated(new Date());
        anTypeNew.setGmtModified(new Date());
        anTypeOld=anTypeMapper.selectTypeByTypeCodeDisable(typeCode);
        int result;
        if(anTypeOld!=null){
             result=anTypeMapper.updateByTypeCodeSelective(anTypeNew);
        }else{
            int lastId=anTypeMapper.selectTypeCount();
            lastId++;
            anTypeNew.setId(lastId+"");
            result = anTypeMapper.insertSelective(anTypeNew);
        }
        //如果当前的parentId不为空，则说明用户要改变当前的记录的父类型
        //根据当前的parentId作为id,将对应的记录的has_children字段+1
        AnType anTypeForUpdateNum=anTypeMapper.selectTypeByParentId(parentId);
        if(anTypeForUpdateNum!=null){
            anTypeForUpdateNum.setHasChildren(anTypeForUpdateNum.getHasChildren()+1);
            int updateHasChildren=anTypeMapper.updateByPrimaryKeySelective(anTypeForUpdateNum);
            AssertUtil.state(updateHasChildren > 0, "更新hasChildren字段失败");
        }
        AssertUtil.state(result > 0, "插入类型失败");
    }
    /**
     * 删除type,设置该type的state为disable
     * @param typeId
     * */
    public void deleteType(String typeId){
        AnType anTypedel=anTypeMapper.selectByPrimaryKey(typeId);
        anTypedel.setState(TypeStateEnum.DISABLE.name());
        int delResult=anTypeMapper.updateByPrimaryKeySelective(anTypedel);
        AssertUtil.state(delResult > 0, "删除类型失败");
    }
//    /**
//     * 根据type/term查询标术语注表中的标注
//     *@param type
//     *@param term
//     */
//    public List<Annotation> queryAnnotationByType(String type, String term){
//        List<String> stateList=new ArrayList<>();
//        stateList.add(AnnotationStateEnum.FINISH.name());
//        int total=annotationService.annotationTermSize(AnnotationStateEnum.FINISH.name());
//        List<Annotation> finalAnnotation = new LinkedList<>();
//        Page<Annotation> pageInfo = annotationService.queryByStateList(stateList, 1, total);
//        for (Annotation annotation : pageInfo.getResult()) {
//            try {
//                //最终标注
//                List<TermAnnotationModel> finalModelList = AnnotationConvert
//                        .convertAnnotationModelList(annotation.getFinalAnnotation());
//                boolean isHave = false;
//                for (TermAnnotationModel currentModel : finalModelList) {
//                    //如果标注中存在待替换的type类型,进行替换
//                    if (currentModel.getType().equals(type) && currentModel.getTerm().equals(term)) {
//                        isHave = true;
//                        break;
//                    }
//                }
//                if (isHave) {
//                    finalAnnotation.add(annotation);
//                    LogUtil.info(logger, "存在带替换的标注术语为:" + annotation.getId());
//                }
//            } catch (Exception ex) {
//                LogUtil.info(logger,
//                        "结束处理第" + ex.getMessage());
//            }
//        }
//        return finalAnnotation;
//    }
    /**
     * 根据type/term查询标术语注表中的标注
     *@param type
     *@param term
     */
    public AnnotationPagination queryAnnotationByType(String type, String term,int cPageNum,int cPageSize){
        LogUtil.info(logger, "开始批量查询原子术语列表"+cPageNum);
        int pageNum = 1;
        int pageSize = 2000;
        int currentIndex=cPageNum;
        int total=annotationService.annotationTermSize(AnnotationStateEnum.FINISH.name());
        List<Annotation> finalAnnotationList = new LinkedList<>();
        outer:do {
            List<Annotation> anAtomicTermList = annotationService.queryFinalAnnotationPagination(AnnotationStateEnum.FINISH.name(), cPageNum, pageSize);
            for (Annotation annotation : anAtomicTermList) {
                try {
                    //最终标注
                    List<TermAnnotationModel> finalModelList = AnnotationConvert
                            .convertAnnotationModelList(annotation.getFinalAnnotation());
                    boolean isHave = false;
                    for (TermAnnotationModel currentModel : finalModelList) {
                        if (currentModel.getType().equals(type) && currentModel.getTerm().equals(term)) {
                            isHave = true;
                            break;
                        }
                    }
                    if (isHave) {
                        if(finalAnnotationList.size()==cPageSize)
                        {
                            currentIndex=currentIndex+cPageNum;
                            break outer;
                        }
                        finalAnnotationList.add(annotation);
                        LogUtil.info(logger, "存在带替换的标注术语为:" + annotation.getId());
                    }
                } catch (Exception ex) {
                    LogUtil.info(logger,
                            "结束处理第" + ex.getMessage());
                }
                cPageNum++;
            }
            pageNum++;
        }while((pageNum*pageSize+currentIndex)<total);
        System.out.println((pageNum*pageSize+currentIndex)+"<<<<<<<<<<<<<<<<>>>>>>>>>>"+total);
        AnnotationPagination annotationPagination=new AnnotationPagination<Annotation>();
        annotationPagination.setLastIndex(currentIndex);
        annotationPagination.setList(finalAnnotationList);
        return annotationPagination;
    }
}
