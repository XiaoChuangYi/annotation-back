package cn.malgo.annotation.core.service.config;

import cn.malgo.annotation.common.dal.mapper.AnTypeMapper;
import cn.malgo.annotation.common.dal.mapper.DrawMapper;
import cn.malgo.annotation.common.dal.model.AnType;
import cn.malgo.annotation.common.dal.model.Draw;
import cn.malgo.annotation.common.dal.model.result.TypeHierarchyNode;
import cn.malgo.annotation.common.util.AssertUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cjl on 2017/12/13.
 */
@Service
public class VisualService {

    @Autowired
    private DrawMapper drawMapper;

    @Autowired
    private AnTypeMapper anTypeMapper;

    private  static   String [] reservedConfigName   = {"ANY", "ENTITY", "RELATION", "EVENT", "NONE", "EMPTY","REL-TYPE","URL", "URLBASE", "GLYPH-POS", "DEFAULT", "NORM", "OVERLAP", "OVL-TYPE"};

    public List<Object> fillTypeConfiguration(List<TypeHierarchyNode> typeHierarchyNodeList){
        JSONArray itemArr=new JSONArray();
        for(TypeHierarchyNode typeHierarchyNode:typeHierarchyNodeList){
            if(typeHierarchyNode.equals("SEPARATOR")){
                itemArr.add(new JSONObject());
            }else {
                String type=typeHierarchyNode.getTerms().get(0);
                JSONObject item=new JSONObject();
                item.put("name",anTypeMapper.selectTypeByTypeCodeEnable(type).getTypeName());
                item.put("type",type);
                item.put("unused",typeHierarchyNode.isUnused());
//                item.put("labels",)
            }
        }
        return  itemArr;
    }
    /**
     *获取type类型的配置
     * @return list
     */
    public List<TypeHierarchyNode> getEntitySection(){
        List<AnType> anTypeList=anTypeMapper.selectAll();
        List<TypeHierarchyNode> typeHierarchyNodeList=new ArrayList<>();
        for(AnType anType:anTypeList){
            List<String> anTypeListNew=new ArrayList<>();
            anTypeListNew.add(anType.getTypeCode());
            typeHierarchyNodeList.add(initTypeHierarchyNode(anTypeListNew,new ArrayList<>()));
        }
        return  typeHierarchyNodeList;
    }
    /**
     *获取渲染配置
     * @return list
     */
    public List<TypeHierarchyNode> getDrawingSection(){
        List<Draw> drawList=drawMapper.selectDrawLeftJoinType();
        List<TypeHierarchyNode> typeHierarchyNodeList=new ArrayList<>();
        for(Draw draw:drawList){
            String [] drawNameArr=draw.getDrawName().split(",");
            List<String> termList=new ArrayList<>();
            termList.add(draw.getTypeCode());
            typeHierarchyNodeList.add(initTypeHierarchyNode(termList,Arrays.asList(drawNameArr)));
        }
        return  typeHierarchyNodeList;
    }
    private TypeHierarchyNode initTypeHierarchyNode(List<String> terms, List<String> args){
        TypeHierarchyNode typeHierarchyNode=new TypeHierarchyNode();
        typeHierarchyNode.setTerms(terms);
        typeHierarchyNode.setArgs(args);
        AssertUtil.notEmpty(terms,"terms集合为空");
        for(String temp:terms){
            AssertUtil.notBlank(temp,"集合terms中"+temp+"为空");
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
                    pattern = Pattern.compile("^(\\S+?)(\\{\\S+\\}|\\?|\\*|\\+|)$");
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
}
