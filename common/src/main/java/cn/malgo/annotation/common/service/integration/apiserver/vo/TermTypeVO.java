package cn.malgo.annotation.common.service.integration.apiserver.vo;

import java.util.*;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;

/**
 * Created by 张钟 on 2017/10/18.
 */
public class TermTypeVO {

    private String term;
    private String type;

    /**
     * 转换成字符
     * 例如:[["展神经","Body-structure"],["面","Zone"],["胰头","Body-structure"]]
     * @param termTypeVOList
     * @return
     */
    public static String convertToString(List<TermTypeVO> termTypeVOList) {
        List<Map<String, String>> maps = new ArrayList<>();
        for (TermTypeVO termTypeVO : termTypeVOList) {
            Map<String, String> tempMap = new HashMap();
            tempMap.put(termTypeVO.getTerm(), termTypeVO.getType());
            maps.add(tempMap);
        }
        return JSONArray.toJSONString(maps);
    }

    /**
     * 将json格式的newTerm string 格式化bean 的list
     * @param termTypeString
     * @return
     */
    public static List<TermTypeVO> convertFromString(String termTypeString){
        List<TermTypeVO> termTypeVOList = new ArrayList<>();
        if (StringUtils.isNotBlank(termTypeString)) {
            JSONArray jsonArray = JSONArray.parseArray(termTypeString);
            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;
                Set<String> set = jsonObject.keySet();
                String termStr = set.iterator().next();
                String termType = jsonObject.getString(termStr);
                TermTypeVO termTypeVO = new TermTypeVO();
                termTypeVO.setTerm(termStr);
                termTypeVO.setType(termType);
                termTypeVOList.add(termTypeVO);
            }
        }
        return termTypeVOList;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
