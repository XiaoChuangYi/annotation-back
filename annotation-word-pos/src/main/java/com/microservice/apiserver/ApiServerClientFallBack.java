package com.microservice.apiserver;

import cn.malgo.common.LogUtil;
import com.alibaba.fastjson.JSONObject;
import com.microservice.apiserver.request.UpdateAnnotationRequest;
import com.microservice.apiserver.result.AnnotationResult;
import com.microservice.vo.TermVO;
import feign.hystrix.FallbackFactory;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by cjl on 2018/4/11.
 */
@Component
public class ApiServerClientFallBack implements FallbackFactory<ApiServerClient>{

    private Logger logger = Logger.getLogger(ApiServerClientFallBack.class);

    @Override
    public ApiServerClient create(Throwable cause) {
        return new ApiServerClient() {
            @Override
            public String docPosTag(String text) {
                LogUtil.error(logger, cause, MessageFormat.format("调用demo接口异常,文本内容:{0}", text));
                return null;
            }

            @Override
            public String phraseTokenize(String text) {
                LogUtil.error(logger, cause, MessageFormat.format("调用自动标注接口异常,文本内容:{0}", text));
                return null;
            }

            @Override
            public List<AnnotationResult> batchPhraseTokenize(List<TermVO> texts) {
                LogUtil.error(logger, cause,
                        MessageFormat.format("调用自动标注接口异常,文本内容:{0}", JSONObject.toJSONString(texts)));
                return null;
            }

            @Override
            public String phraseUpdatePosWithNewTerm(UpdateAnnotationRequest updateAnnotationRequest) {
                LogUtil.error(logger, cause, MessageFormat.format("单条请求附带新词和手工标注的最终标注:{0}",
                        JSONObject.toJSONString(updateAnnotationRequest)));
                return null;
            }

            @Override
            public List<AnnotationResult> batchPhraseUpdatePosWithNewTerm(List<UpdateAnnotationRequest> updateAnnotationDTOList) {
                LogUtil.error(logger, cause, MessageFormat.format("批量请求附带新词和手工标注的最终标注:{0}",
                        JSONObject.toJSONString(updateAnnotationDTOList)));
                return null;
            }
        };
    }
}
