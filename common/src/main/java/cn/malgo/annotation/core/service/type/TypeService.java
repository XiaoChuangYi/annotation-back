package cn.malgo.annotation.core.service.type;

import cn.malgo.annotation.common.dal.mapper.AnAtomicTermMapper;
import cn.malgo.annotation.common.dal.mapper.CorpusMapper;
import cn.malgo.annotation.common.dal.mapper.AnTypeMapper;
import cn.malgo.annotation.common.dal.model.AnAtomicTerm;
import cn.malgo.annotation.common.dal.model.Annotation;
import cn.malgo.annotation.common.dal.model.Corpus;
import cn.malgo.annotation.common.dal.model.AnType;
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
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
     * 更新type的名称
     * @param typeId
     * */
    public void updateTypeName(String parentId,String typeId,String typeName){
        AnType anTypeParam=new AnType();
        anTypeParam.setId(typeId);
        anTypeParam.setParentId(parentId);
        anTypeParam.setTypeName(typeName);
        anTypeParam.setGmtModified(new Date());
        int updateResult=anTypeMapper.updateByPrimaryKeySelective(anTypeParam);
        AssertUtil.state(updateResult > 0, "更新类型名称失败");
    }
    /**
     * 新增type
     * @param typeName
     * */
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
            result = anTypeMapper.insert(anTypeNew);
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
    /**
     * 根据type/term查询标术语注表中的标注
     *@param type
     *@param term
     */
    public List<Annotation> queryAnnotationByType(String type, String term){
        List<String> stateList=new ArrayList<>();
        stateList.add(AnnotationStateEnum.FINISH.name());
        int total=annotationService.annotationTermSize(AnnotationStateEnum.FINISH.name());
        List<Annotation> finalAnnotation = new LinkedList<>();
        Page<Annotation> pageInfo = annotationService.queryByStateList(stateList, 1, total);
        for (Annotation annotation : pageInfo.getResult()) {
            try {
                //最终标注
                List<TermAnnotationModel> finalModelList = AnnotationConvert
                        .convertAnnotationModelList(annotation.getFinalAnnotation());
                boolean isHave = false;
                for (TermAnnotationModel currentModel : finalModelList) {
                    //如果标注中存在待替换的type类型,进行替换
                    if (currentModel.getType().equals(type) && currentModel.getTerm().equals(term)) {
                        isHave = true;
                        break;
                    }
                }
                if (isHave) {
                    finalAnnotation.add(annotation);
                    LogUtil.info(logger, "存在带替换的标注术语为:" + annotation.getId());
                }
            } catch (Exception ex) {
                LogUtil.info(logger,
                        "结束处理第" + ex.getMessage());
            }
        }
        return finalAnnotation;
    }
//    /**
//     * 根据type/term查询标术语注表中的标注
//     *@param type
//     *@param term
//     */
//    public List<Annotation> queryAnnotationByType(String type, String term,int pageIndex,int pageSize){
//        List<String> stateList=new ArrayList<>();
//        stateList.add(AnnotationStateEnum.FINISH.name());
//        List<Annotation> finalAnnotation = new LinkedList<>();
//        Page<Annotation> pageInfo = annotationService.queryByStateList(stateList, pageIndex, pageSize);
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
}
