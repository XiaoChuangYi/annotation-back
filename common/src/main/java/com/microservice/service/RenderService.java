package com.microservice.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.microservice.dataAccessLayer.entity.BratDraw;
import com.microservice.dataAccessLayer.entity.Type;
import com.microservice.dataAccessLayer.mapper.BratDrawMapper;
import com.microservice.dataAccessLayer.mapper.TypeMapper;
import com.microservice.pojo.Arcs;
import com.microservice.pojo.TypeHierarchyNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cjl on 2018/4/2.
 */
@Service
public class RenderService {

    @Autowired
    private BratDrawMapper drawMapper;

    @Autowired
    private TypeMapper typeMapper;

    private  static   String [] reservedConfigName   = {"ANY", "ENTITY", "RELATION", "EVENT", "NONE", "EMPTY","REL-TYPE","URL", "URLBASE", "GLYPH-POS", "DEFAULT", "NORM", "OVERLAP", "OVL-TYPE"};

//    public static Logger logger = Logger.getLogger(RenderService.class);

    /**
     *获取渲染配置
     * @return list
     */
    public List<TypeHierarchyNode> getDrawingSection(int taskId){
        List<BratDraw> drawList=drawMapper.selectDrawJoinAnType(taskId);
        List<TypeHierarchyNode> typeHierarchyNodeList=new ArrayList<>();
        for(BratDraw draw:drawList){
            String [] drawNameArr=draw.getDrawName().isEmpty()?(new String[]{}):draw.getDrawName().split(",");
            List<String> termList=new ArrayList<>();
            termList.add(draw.getTypeCode());
            typeHierarchyNodeList.add(initTypeHierarchyNode(termList, Arrays.asList(drawNameArr)));
        }
        return  typeHierarchyNodeList;
    }
    private TypeHierarchyNode initTypeHierarchyNode(List<String> terms, List<String> args){
        TypeHierarchyNode typeHierarchyNode=new TypeHierarchyNode();
        typeHierarchyNode.setTerms(terms);
        typeHierarchyNode.setArgs(args);
        if(terms.size()==0) {
//            logger.info("terms集合为空");
            return typeHierarchyNode;
        }
        for(String temp:terms){
//            logger.info("集合terms中"+temp+"为空");
            if(StringUtils.isBlank(temp))
            return typeHierarchyNode;
        }
        typeHierarchyNode.setUnused(false);
        //unused if any of the terms marked with "!"
        for(String temp:terms){
            if(temp.contains("!")){
                typeHierarchyNode.setUnused(true);
            }
        }
        typeHierarchyNode.setChildren(new ArrayList<>());
        List<String> argList=new ArrayList<>();
        Map<String,Integer> mapArgMinCount=new HashMap<>();
        Map<String,Integer> mapArgMaxCount=new HashMap<>();
        Map<String,List<String>> mapKeysByType=new HashMap<>();
        Map<String,List<String>> mapArgument=new HashMap<>();
        Map<String, List<String>> stringListMap = new HashMap<>();
        if(args!=null&&args.size()>0) {
            for (String temp : args) {
                temp = temp.trim();
                //创建java的正则表打式对象
                Pattern pattern = Pattern.compile("^(\\S*?):(\\S*)$");
                Matcher matcher = pattern.matcher(temp);
                if (matcher.find()) {
                    String key = matcher.group(1);
                    String aTypes = matcher.group(2);
                    if (reservedConfigName.toString().contains(key.toUpperCase())) {
                        if (key.equals("<REL-TYPE>")) {
                            stringListMap.put(key, Arrays.asList(aTypes.split("\\|")));
                        } else {
                            stringListMap.put(key, Arrays.asList(new String[]{aTypes}));
                        }
                        continue;
                    }
                    pattern = Pattern.compile("^(\\S+?)(\\{\\S+}|\\?|\\*|\\+|)$");
                    matcher = pattern.matcher(key);
                    String rep = "";
                    if (matcher.find()) {
                        key = matcher.group(1);
                        rep = matcher.group(2);
                    }
                    int minimum_count = 0, maximum_count = 0;
                    if (rep.equals("")) {
                        minimum_count = 1;
                        maximum_count = 1;
                    } else if (rep == "?") {
                        minimum_count = 0;
                        maximum_count = 1;
                    } else if (rep == "*") {
                        minimum_count = 0;
                        maximum_count = Integer.MAX_VALUE;
                    } else if (rep == "+") {
                        minimum_count = 1;
                        maximum_count = Integer.MAX_VALUE;
                    } else {
                        //todo
                    }
                    mapArgMinCount.put(key, minimum_count);
                    mapArgMaxCount.put(key, maximum_count);
                    argList.add(key);
                    List<String> argumentsList = new ArrayList<>();
                    String[] typeArr = aTypes.split("\\|");
                    for (String aType : typeArr) {
                        if (aType.trim() != "") {
                            argumentsList.add(aType);
                            List<String> keysByTypeList = new ArrayList<>();
                            keysByTypeList.add(key);
                            mapKeysByType.put(aType, keysByTypeList);
                        }
                    }
                    mapArgument.put(key, argumentsList);
                }
            }
        }
        typeHierarchyNode.setArguments(mapArgument);
        typeHierarchyNode.setKeys_by_type(mapKeysByType);
        typeHierarchyNode.setArg_min_count(mapArgMinCount);
        typeHierarchyNode.setArg_list(argList);
        typeHierarchyNode.setArg_max_count(mapArgMaxCount);
        typeHierarchyNode.setSpecial_arguments(stringListMap);
        return  typeHierarchyNode;
    }
    /**
     * 构造bratConfig的entity_types的数据结构
     * @param typeHierarchyNodeList
     */
    public JSONArray fillTypeConfiguration(List<TypeHierarchyNode> typeHierarchyNodeList,int taskId) {
        JSONArray itemArr = new JSONArray();
        if (typeHierarchyNodeList.size() > 0) {
            for (TypeHierarchyNode typeHierarchyNode : typeHierarchyNodeList) {
                if (typeHierarchyNode.equals("SEPARATOR")) {
                    itemArr.add(new JSONObject());
                } else {
                    String typeCode = typeHierarchyNode.getTerms().get(0);
                    JSONObject item = new JSONObject();
                    item.put("name", typeMapper.getTypeByTypeCode(typeCode,taskId).getTypeName());
                    item.put("type", typeCode);
                    item.put("unused", typeHierarchyNode.isUnused());
                    item.put("labels", getTypeAliaArr(typeCode,taskId));//通过type类型到AN_TYPE表里取到对应的别名，添加进去
                    item.put("attributes", new JSONArray());//目前放一个空的json数组
                    item.put("normalizations", new JSONArray());
                    //添加绘图参数
                Map<String,String> map=getDrawArgument(typeCode,taskId);
                for(String key:map.keySet()){
                    item.put(key,map.get(key));
                }
//                    添加arcs数组
                List<String> typeList=listEnableType(taskId);
                List<Arcs> arcsList=new ArrayList<>();
                Arcs arcs1=new Arcs();
                arcs1.setType("Adjective");
                List<String> labelList1=new ArrayList<>();
                labelList1.add("Adjective");
                arcs1.setLabels(labelList1);
                arcs1.setTargets(typeList);
                arcsList.add(arcs1);
                Arcs arcs2=new Arcs();
                arcs2.setType("Rel");
                List<String> labelList2=new ArrayList<>();
                labelList2.add("Rel");
                arcs2.setLabels(labelList2);
                arcs2.setTargets(typeList);
                arcsList.add(arcs2);
                item.put("arcs",arcsList);
                    item.put("children", new JSONArray());
                    itemArr.add(item);
                }
            }
        }
        return itemArr;
    }
    /**
     *获取对应type的别名
     * @param typeCode
     * @return aliaList
     */
    private List<String> getTypeAliaArr(String typeCode,int taskId){
        BratDraw draw=drawMapper.selectDrawByTypeCode(typeCode,taskId);
        List<String> aliaList=new ArrayList<>();
        if(draw!=null){
            if(!"".equals(draw.getTypeLabel())){
                String [] typeArr=draw.getTypeLabel().split("\\|");
                for(String temp:typeArr){
                    aliaList.add(temp);
                }
            }
        }
        return  aliaList;
    }
    /**
     *根据对应的type，获取对应的DRAW表里的draw_name字段的数据
     * @param typeCode
     */
    private Map<String,String> getDrawArgument(String typeCode,int taskId){
        BratDraw draw=drawMapper.selectDrawByTypeCode(typeCode,taskId);
        Map<String,String> map=new HashMap<>();
        if(!"".equals(draw.getDrawName())){
            String [] arguments=draw.getDrawName().split(",");
            if(arguments.length>0){
                for(String currentArg:arguments) {
                    if(!StringUtils.isNotBlank(currentArg)) {
                        if (currentArg.startsWith("dashArray")) {
                            String value = currentArg.substring(currentArg.indexOf(":") + 1, currentArg.length()).replace("-", ",");
                            map.put(currentArg.substring(0, currentArg.indexOf(":")), value);
                        } else {
                            map.put(currentArg.substring(0, currentArg.indexOf(":")), currentArg.substring(currentArg.indexOf(":") + 1, currentArg.length()));
                        }
                    }
                }
            }
        }
        return map;
    }
    /**
     * 获取所有的type
     */
    private List<String> listEnableType(int taskId){
        List<Type> anTypeList=typeMapper.listEnableType(taskId);
        List<String> typeCodeList=new ArrayList<>();
        for(Type anType:anTypeList){
            typeCodeList.add(anType.getTypeCode());
        }
        return  typeCodeList;
    }

    public List<BratDraw> listDraw(){
        return drawMapper.selectDrawByCondition(new BratDraw());
    }

    public Page<BratDraw> listDrawByCondition(int pageNum,int pageSize,String drawName,String typeCode,int taskId){
        Page<BratDraw> pageInfo= PageHelper.startPage(pageNum, pageSize);
        BratDraw draw=new BratDraw();
        draw.setTypeCode(typeCode);
        draw.setDrawName(drawName);
        draw.setTaskId(taskId);
        drawMapper.selectDrawByCondition(draw);
        return pageInfo;
    }

    public BratDraw getDrawByTypeCode(String typeCode ,int taskId){
        return drawMapper.selectDrawByTypeCode(typeCode,taskId);
    }

    /**
     * 更新draw表
     * @param id
     * @param drawName
     */
    public void updateDrawById(int id,String drawName,String typeLabel){
        BratDraw draw=new BratDraw();
        draw.setId(id);
        if(StringUtils.isNotBlank(drawName))
            draw.setDrawName(drawName);
        if(StringUtils.isNotBlank(typeLabel))
            draw.setTypeLabel(typeLabel);
        drawMapper.updateDrawSelectiveByPrimaryKey(draw);
    }
    /**
     * 新增draw表
     * @param typeId
     * @param drawName
     */
    public  void  addDraw(int typeId,String drawName,String typeLabel,int taskId){
        BratDraw pDraw=new BratDraw();
        pDraw.setTypeId(typeId);
        pDraw.setDrawName(drawName);
        pDraw.setTypeLabel(typeLabel);
        pDraw.setTaskId(taskId);
        drawMapper.insertDrawSelective(pDraw);
    }

}
