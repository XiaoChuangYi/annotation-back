package cn.malgo.annotation.core.service.type;

import cn.malgo.annotation.common.dal.mapper.AnAtomicTermMapper;
import cn.malgo.annotation.common.dal.mapper.AnTermMapper;
import cn.malgo.annotation.common.dal.mapper.AnTypeMapper;
import cn.malgo.annotation.common.dal.model.AnAtomicTerm;
import cn.malgo.annotation.common.dal.model.AnTerm;
import cn.malgo.annotation.common.dal.model.AnType;
import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.core.model.enums.annotation.TypeStateEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by cjl on 2017/11/20.
 */
@Service
public class TypeService {
    @Autowired
    private AnTypeMapper anTypeMapper;

    @Autowired
    private AnAtomicTermMapper anAtomicTermMapper;

    @Autowired
    private AnTermMapper anTermMapper;


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
        List<AnTerm> anTermList=anTermMapper.selectAnTermIDsByOldType(typeOld);
        if(anTermList.size()>0){
            List<String> idsList=new LinkedList<>();
            for(int k=0;k<anTermList.size();k++){
                idsList.add(anTermList.get(k).getId());
            }
            anTermMapper.batchUpdateAnTermType(idsList,typeNew);
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
}
