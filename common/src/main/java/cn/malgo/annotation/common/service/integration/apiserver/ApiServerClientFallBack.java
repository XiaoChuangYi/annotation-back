package cn.malgo.annotation.common.service.integration.apiserver;

import java.text.MessageFormat;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import cn.malgo.annotation.common.service.integration.apiserver.request.UpdateAnnotationRequest;
import cn.malgo.annotation.common.service.integration.apiserver.result.AnnotationResult;
import cn.malgo.annotation.common.util.log.LogUtil;
import feign.hystrix.FallbackFactory;

/**
 *
 * @author 张钟
 * @date 2017/10/18
 */
@Component
public class ApiServerClientFallBack implements FallbackFactory<ApiServerClient> {

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
            public String phraseUpdatePosWithNewTerm(JSONObject body) {
                LogUtil.error(logger, cause,
                    MessageFormat.format("调用二次,参数内容:{0}", body.toJSONString()));
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