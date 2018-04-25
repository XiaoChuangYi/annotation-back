package com.microservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.microservice.dataAccessLayer.entity.AnAtomicTerm;
import com.microservice.result.PageResult;
import com.microservice.result.PageVO;
import com.microservice.result.ResultVO;
import com.microservice.service.annotation.AnnotationBatchService;
import com.microservice.service.atomicterm.AnAtomicTermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by cjl on 2018/4/11.
 */
@RestController
@RequestMapping(value = "/atomic")
public class AnAtomicController extends BaseController{
    @Autowired
    private AnAtomicTermService atomicTermService;

    @Autowired
    private AnnotationBatchService annotationBatchService;

    /**
     * 分页查询原子术语
     * @param params
     * @return
     */
    @RequestMapping(value = "/listAtomicTerm.do")
    public ResultVO<PageVO<AnAtomicTerm>> listAtomicTermCondition(@RequestBody JSONObject params) {

        int pageIndex=params.containsKey("pageIndex")?params.getInteger("pageIndex"):1;
        int pageSize=params.containsKey("pageSize")?params.getInteger("pageSize"):10;
        String term=params.getString("term");
        String type=params.getString("type");
        String id=params.getString("id");
        String checked=params.getString("checked");


        Page<AnAtomicTerm> page = atomicTermService.listAnAtomicTermByPagingCondition(term,type,id,pageIndex,pageSize,checked);
        PageVO<AnAtomicTerm> pageVO = new PageVO(page);

        return ResultVO.success(pageVO);
    }
    /**
     * 根据conceptId分页查询原子术语
     * @param params
     */
    @RequestMapping(value = "/queryPaginationByConceptId.do")
    public ResultVO<PageVO<AnAtomicTerm>> queryPaginationByConceptId(@RequestBody JSONObject params) {

        int pageIndex=params.containsKey("pageIndex")?params.getInteger("pageIndex"):1;
        int pageSize=params.containsKey("pageSize")?params.getInteger("pageSize"):10;
        String conceptId=params.getString("conceptId");

        Page<AnAtomicTerm> page = atomicTermService.listAnAtomicTermAssociatedConceptByConceptId(conceptId, pageIndex, pageSize);
        PageVO<AnAtomicTerm> pageVO = new PageVO(page);
        return ResultVO.success(pageVO);
    }

    /**
     *分页根据term模糊查询原子术语
     * @param params
     * @return
     */
    @RequestMapping(value = "/fuzzyList.do")
    public ResultVO<PageVO<AnAtomicTerm>> fuzzyQueryPage(@RequestBody JSONObject params){

        int pageIndex=params.containsKey("pageIndex")?params.getInteger("pageIndex"):1;
        int pageSize=params.containsKey("pageSize")?params.getInteger("pageSize"):10;
        String term=params.getString("term");
        String checked=params.getString("checked");

        Map<String,Object> atomicTermMap=atomicTermService.mapAnAtomicTermByPagingCondition(term,pageIndex,pageSize,checked);
        PageResult<AnAtomicTerm> pageResult=new PageResult<>();
        pageResult.setTotal(Integer.parseInt(atomicTermMap.get("total").toString()));
        pageResult.setDataList((List<AnAtomicTerm>)atomicTermMap.get("atomicTermList"));
        return  ResultVO.success(pageResult);
    }


    /**
     * 查询原子术语用来初始化下拉框
     */
    @RequestMapping(value = {"/queryAtomicTermForInitSelectBox.do"})
    public ResultVO<List<AnAtomicTerm>> queryAtomicTermForInitSelectBox(@RequestBody JSONObject params){
        String term=params.getString("term");
        List<AnAtomicTerm> anAtomicTermList=atomicTermService.listAtomicTermByCondition(term);
        return  ResultVO.success(anAtomicTermList);
    }

    /**
     * 新增concept信息
     * @param params
     * @return
     */
    @RequestMapping(value = { "/addConceptAndUpdateAtomic.do" })
    public ResultVO addConceptAndUpdateAtomic(@RequestBody JSONObject params){
        String id=params.getString("id");
        String originName=params.getString("originName");
        atomicTermService.saveConceptAndUpdateAtomicTerm(id,originName);
        return  ResultVO.success();
    }
    /**
     * 更新concept信息
     * @param
     * @return
     */
    @RequestMapping(value = { "/updateAtomicTermOrAddConcept.do" })
    public ResultVO updateAtomicTermOrAddConcept(@RequestBody JSONObject params){
        String id=params.getString("id");
        String conceptId=params.getString("conceptId");
        String conceptName=params.getString("conceptName");

        atomicTermService.updateAtomicTermOrAddConcept(id,conceptId,conceptName);
        return  ResultVO.success();
    }


    /**
     * 批量更新术语表conceptId字段
     */
    @RequestMapping(value = {"/updateBatchConceptIdOfAtomicTerm.do"})
    public ResultVO updateBatchConceptIdOfAtomicTerm(@RequestBody JSONObject params){

        List<String> idArr=params.getJSONArray("atomicTermArr").toJavaObject(List.class);
        String conceptId=params.getString("conceptId");
        atomicTermService.updateBatchConceptIdOfAtomicTerm(idArr,conceptId);
        return ResultVO.success();
    }

    /**
     * 置空原子术语表的concept_id
     * @param id
     */
    @RequestMapping(value = "/clearConceptIdOfAtomicTerm.do")
    public  ResultVO clearConceptIdOfAtomicTerm(String id){
        atomicTermService.clearConceptIdOfAtomicTerm(id);
        return  ResultVO.success();
    }

    /**
     * 删除原子术语，并删除对应的标注
     * @param id
     * @param term
     * @param type
     */
    @RequestMapping(value = {"/deleteAtomicTerm.do"})
    public ResultVO deleteAtomicTerm(String id,String term,String type){
        annotationBatchService.deleteAtomicTermAndUnitAnnotation(id,term,type);
        return  ResultVO.success();
    }

    /**
     * 遗弃原子术语信息
     * @param id
     * @return
     */
    @RequestMapping(value = { "/abandonAtomicTerm.do" })
    public ResultVO  abandonAtomicTerm(String id){
        atomicTermService.abandonAtomicTerm(id,"DISABLE");
        return  ResultVO.success();
    }

}
