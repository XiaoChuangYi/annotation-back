package cn.malgo.annotation.common.service.integration.apiserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by 张钟 on 2017/10/18.
 */
@Service
public class ApiServerService {

    @Autowired
    private ApiServerClient apiServerClient;

    /**
     * demo接口
     * @param text
     * @return
     */
    public String docPosTag(String text) {
        return apiServerClient.docPosTag(text);
    }

    /**
     * 自动标注
     * @param text
     * @return
     */
    public String phraseTokenize(String text) {
        String result = apiServerClient.phraseTokenize(text);
        //TODO 后续处理
        return result;
    }

    /**
     * 自动标注,通过给定的新词和手工标注
     * @param body
     * @return
     */
    public String phraseUpdatePosWithNewTerm(JSONObject body) {
        String result = apiServerClient.phraseUpdatePosWithNewTerm(body);
        if (result != null) {
            //TODO 后续处理
        }
        return result;
    }

}
