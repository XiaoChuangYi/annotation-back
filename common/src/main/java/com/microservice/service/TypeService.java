package com.microservice.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.microservice.dataAccessLayer.entity.Type;
import com.microservice.dataAccessLayer.mapper.TypeMapper;
import com.microservice.enums.TypeStateEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by cjl on 2018/4/11.
 */
@Service
public class TypeService {

    @Autowired
    private TypeMapper typeMapper;

    /**
     * 根据ID查询其子类型数据
     * @param parentId
     * @param taskId
     */
    public  List<Type> listChildrenTypeByParentId(String parentId ,int taskId){
        List<Type> anTypeList=typeMapper.listChildrenTypeById(parentId,taskId);
        return  anTypeList;
    }

    /**
     * 分页，条件查询类型
     * @param pageNum
     * @param pageSize
     * @param typeCode
     * @param typeName
     * @param taskId
     */
    public Page<Type> listEnableTypeByPagingCondition(int pageNum, int pageSize, String typeCode, String typeName,int taskId){
        Page<Type> pageInfo= PageHelper.startPage(pageNum, pageSize);
        Type paramType=new Type();
        paramType.setTypeCode(typeCode);
        paramType.setTypeName(typeName);
        paramType.setTaskId(taskId);
        typeMapper.listTypeAndShowParent(paramType);
        return pageInfo;
    }
    /**
     * 查询所有可用类型
     * @param taskId
     */
    public List<Type> listEnableType(int taskId){
        List<Type> anTypeList=typeMapper.listEnableType(taskId);
        return anTypeList;
    }

    /**
     * 根据id更新type表中的typeCode
     * @param id
     * @param typeCode
     */
    public void updateTypeCodeById(String id,String typeCode){
        typeMapper.updateTypeCodeById(typeCode,id);
    }



    /**
     * 多字段更新type
     * @param id
     * @param parentId
     * @param typeName
     * @param originParentId
     * @param taskId
     * */
    @Transactional
    public void updateType(String parentId,String id,String typeName,String originParentId,int taskId){
        Type anTypeParam=new Type();
        anTypeParam.setId(id);
        anTypeParam.setTypeName(typeName);
        anTypeParam.setGmtModified(new Date());
        //如果当前的parentId不为空，则说明用户要改变当前的记录的父类型
        //根据当前的parentId作为id,将对应的记录的has_children字段+1
        //根据当前的originParentId,查询出当前记录原先所归属的type记录。
        // 1.如果没有，则说明当前行为顶级父类型，不用管
        // 2.如果有记录，则说明当前行曾经归属于该type类型下，对该hasChildren字段-1
        if(StringUtils.isNotBlank(parentId)){
            anTypeParam.setParentId(parentId);
            Type anTypeForUpdateNum=typeMapper.getTypeByParentId(parentId,taskId);
            if(anTypeForUpdateNum!=null){
                anTypeForUpdateNum.setHasChildren(anTypeForUpdateNum.getHasChildren()+1);
                typeMapper.updateTypeSelectiveById(anTypeForUpdateNum);
            }
        }

        if(StringUtils.isNotBlank(originParentId)) {
            Type anTypeForUpdateSubtractNum = typeMapper.getTypeByParentId(originParentId,taskId);
            if(anTypeForUpdateSubtractNum!=null){
                if(anTypeForUpdateSubtractNum.getHasChildren()>0)
                {
                    anTypeForUpdateSubtractNum.setHasChildren(anTypeForUpdateSubtractNum.getHasChildren()-1);
                    typeMapper.updateTypeSelectiveById(anTypeForUpdateSubtractNum);
                }
            }
        }
        typeMapper.updateTypeSelectiveById(anTypeParam);
    }

    /**
     * 新增type
     * @param typeName
     * */
    @Transactional
    public void saveType(String parentId,String typeName,String typeCode,int taskId){
        Type anTypeOld=typeMapper.getTypeByTypeCode(typeCode,taskId);
        if(anTypeOld!=null)
            return;
        Type anTypeNew=new Type();
        anTypeNew.setParentId(parentId);
        anTypeNew.setTypeName(typeName);
        anTypeNew.setTypeCode(typeCode);
        anTypeNew.setTaskId(taskId);
        anTypeNew.setState(TypeStateEnum.ENABLE.name());
        anTypeNew.setGmtCreated(new Date());
        anTypeNew.setGmtModified(new Date());
        anTypeOld=typeMapper.getDisableTypeByTypeCode(typeCode,taskId);
        if(anTypeOld!=null){
            typeMapper.updateTypeSelectiveById(anTypeNew);
        }else{
            int lastId=typeMapper.getTypeMaxId();
            lastId++;
            anTypeNew.setId(lastId+"");
            typeMapper.insertTypeSelective(anTypeNew);
        }
        //如果当前的parentId不为空，则说明用户要改变当前的记录的父类型
        //根据当前的parentId作为id,将对应的记录的has_children字段+1
        Type anTypeForUpdateNum=typeMapper.getTypeByParentId(parentId,taskId);
        if(anTypeForUpdateNum!=null){
            anTypeForUpdateNum.setHasChildren(anTypeForUpdateNum.getHasChildren()+1);
            typeMapper.updateTypeSelectiveById(anTypeForUpdateNum);
        }
    }
    /**
     * 删除type,设置该type的state为disable
     * @param typeId
     * */
    public void removeType(String typeId,int taskId){
        Type anType=typeMapper.getTypeByParentId(typeId,taskId);
        anType.setState(TypeStateEnum.DISABLE.name());
        typeMapper.updateTypeSelectiveById(anType);
    }
}
