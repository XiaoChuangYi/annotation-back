package cn.malgo.annotation.core.service.config;

import cn.malgo.annotation.common.dal.mapper.AnTypeMapper;
import cn.malgo.annotation.common.dal.mapper.DrawMapper;
import cn.malgo.annotation.common.dal.model.AnType;
import cn.malgo.annotation.common.dal.model.Draw;
import cn.malgo.annotation.common.dal.model.result.Arcs;
import cn.malgo.annotation.common.dal.model.result.TypeHierarchyNode;
import cn.malgo.annotation.common.util.AssertUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
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

    /**
     * 构造bratConfig的entity_types的数据结构
     * @param typeHierarchyNodeList
     */
    public JSONArray fillTypeConfiguration(List<TypeHierarchyNode> typeHierarchyNodeList){
        JSONArray itemArr=new JSONArray();
        for(TypeHierarchyNode typeHierarchyNode:typeHierarchyNodeList){
            if(typeHierarchyNode.equals("SEPARATOR")){
                itemArr.add(new JSONObject());
            }else {
                String type=typeHierarchyNode.getTerms().get(0);
                JSONObject item=new JSONObject();
                item.put("name",anTypeMapper.selectTypeByTypeCode(type).getTypeName());
                item.put("type",type);
                item.put("unused",typeHierarchyNode.isUnused());
                item.put("labels",getTypeAliaArr(type));//通过type类型到AN_TYPE表里取到对应的别名，添加进去
                item.put("attributes",new JSONArray());//目前放一个空的json数组
                item.put("normalizations",new JSONArray());
                //添加绘图参数
                Map<String,String> map=getDrawArgument(type);
                for(String key:map.keySet()){
                    item.put(key,map.get(key));
                }
                //添加arcs数组
                List<Arcs> arcsList=new ArrayList<>();
                Arcs arcs1=new Arcs();
                    arcs1.setType("Adjective");
                    List<String> labelList1=new ArrayList<>();
                    labelList1.add("Adjective");
                    arcs1.setLabels(labelList1);
                    arcs1.setTargets(getAllTypes());
                arcsList.add(arcs1);
                    Arcs arcs2=new Arcs();
                    arcs2.setType("Rel");
                    List<String> labelList2=new ArrayList<>();
                    labelList2.add("Rel");
                    arcs2.setLabels(labelList2);
                    arcs2.setTargets(getAllTypes());
                arcsList.add(arcs2);
                item.put("arcs",arcsList);
                item.put("children",new JSONArray());
                itemArr.add(item);
            }
        }
        return  itemArr;
    }
    /**
     * 获取所有的type
     * @return
     */
    private List<String> getAllTypes(){
        List<AnType> anTypeList=anTypeMapper.selectAll();
        List<String> typeCodeList=new ArrayList<>();
        for(AnType anType:anTypeList){
            typeCodeList.add(anType.getTypeCode());
        }
        return  typeCodeList;
    }
    /**
     *根据对应的type，获取对应的DRAW表里的draw_name字段的数据
     * @param typeCode
     */
    private Map<String,String> getDrawArgument(String typeCode){
        Draw draw=drawMapper.selectDrawNameByTypeCode(typeCode);
        Map<String,String> map=new HashMap<>();
        if(!"".equals(draw.getDrawName())){
            String [] arguments=draw.getDrawName().split(",");
            if(arguments.length>0){
                for(String currentArg:arguments) {
                    map.put(currentArg.substring(0,currentArg.indexOf(":")),currentArg.substring(currentArg.indexOf(":")+1,currentArg.length()));
                }
            }
        }
        return map;
    }

    /**
     *获取对应type的别名
     * @param typeCode
     * @return aliaList
     */
    private List<String> getTypeAliaArr(String typeCode){
        AnType anType=anTypeMapper.selectTypeLabelByTypeCode(typeCode);
        List<String> aliaList=new ArrayList<>();
        if(anType!=null){
            if(!"".equals(anType.getTypeLabel())){
                String [] typeArr=anType.getTypeLabel().split("\\|");
                for(String temp:typeArr){
                    aliaList.add(temp);
                }
            }
        }
        return  aliaList;
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
            String [] drawNameArr=draw.getDrawName().isEmpty()?(new String[]{}):draw.getDrawName().split(",");
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
     * 分页查询draw表，并关联获取an_type的type_code
     * @param typeCode
     * @param color
     * @param pageNum
     * @param pageSize
     */
    public Page<Draw> queryOnePageDraw(String typeCode,String color,int pageNum, int pageSize){
        Page<Draw> pageInfo= PageHelper.startPage(pageNum, pageSize);
        drawMapper.selectDrawByCondition(typeCode,color);
        return  pageInfo;
    }
    /**
     * 更新draw表
     * @param id
     * @param drawName
     */
    public  void  updateDrawNameById(int id,String drawName){
        Draw pDraw=new Draw();
        pDraw.setDrawName(drawName);
        pDraw.setId(id);
        int updateResult=drawMapper.updateByPrimaryKeySelective(pDraw);
        AssertUtil.state(updateResult>0,"更新draw表失败");
    }
}
