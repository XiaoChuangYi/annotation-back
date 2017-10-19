package cn.malgo.annotation.common.service.integration.apiserver.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;

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
