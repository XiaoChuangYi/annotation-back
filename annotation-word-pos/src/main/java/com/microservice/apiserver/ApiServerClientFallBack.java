package com.microservice.apiserver;

import cn.malgo.common.LogUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.microservice.apiserver.request.AnnotationOriginTextRequest;
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
            /**
             * 根据单个原始文本，获取算法后台对应的标注
             *
             * @param text
             */
            @Override
            public String ruleTokenizePos(String text) {
                LogUtil.error(logger, cause, MessageFormat.format("调用自动标注接口异常,文本内容:{0}", text));
                return null;
            }

            @Override
            public List<AnnotationResult> batchTokenizePos(List<AnnotationOriginTextRequest> annotationOriginTextRequestList) {
                LogUtil.error(logger, cause, MessageFormat.format("调用自动标注接口异常,文本内容:{0}",
                        JSON.toJSONString(annotationOriginTextRequestList)));
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

            /**
             * 病历分词
             *
             * @param text
             */
            @Override
            public String getCaseWordPos(String text) {
                LogUtil.error(logger, cause, MessageFormat.format("调用分词接口异常,文本内容:{0}", text));
                return null;
            }

            /**
             * 批量病历分词
             *
             * @param annotationOriginTextRequestList
             */
            @Override
            public List<AnnotationResult> listCaseWordPos(List<AnnotationOriginTextRequest> annotationOriginTextRequestList) {
                return null;
            }

            /**
             * 病历预标注
             *
             * @param text
             */
            @Override
            public String getCasePrepareAnnotation(String text) {
                return null;
            }

            /**
             * 批量病历预标注
             *
             * @param annotationOriginTextRequestList
             */
            @Override
            public List<AnnotationResult> listCasePrepareAnnotation(List<AnnotationOriginTextRequest> annotationOriginTextRequestList) {
                return null;
            }

            /**
             * 药品分词
             *
             * @param text
             */
            @Override
            public String getDrugWordPos(String text) {
                return null;
            }

            /**
             * 批量药品分词
             *
             * @param annotationOriginTextRequestList
             */
            @Override
            public List<AnnotationResult> listDrugWordPos(List<AnnotationOriginTextRequest> annotationOriginTextRequestList) {
                return null;
            }

            /**
             * 器材分词
             *
             * @param text
             */
            @Override
            public String getDeviceTokenize(String text) {
                return null;
            }

            /**
             * 批量器材分词
             *
             * @param annotationOriginTextRequestList
             */
            @Override
            public List<AnnotationResult> listDeviceTokenize(List<AnnotationOriginTextRequest> annotationOriginTextRequestList) {
                return null;
            }

            /**
             * 更新分词词性和标志
             *
             * @param updateAnnotationRequest
             */
            @Override
            public String updateAnnotationTokenizePos(UpdateAnnotationRequest updateAnnotationRequest) {
                return null;
            }

            /**
             * 批量更新分词词性和
             *
             * @param updateAnnotationRequestList
             */
            @Override
            public List<AnnotationResult> batchUpdateAnnotationTokenizePos(List<UpdateAnnotationRequest> updateAnnotationRequestList) {
                LogUtil.error(logger, cause, MessageFormat.format("批量请求附带新词和手工标注的最终标注:{0}",
                        JSONObject.toJSONString(updateAnnotationRequestList)));
                return null;
            }
        };
    }
}
