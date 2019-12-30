package cn.malgo.annotation.biz.export;

import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.dao.TaskBlockRepository;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.entity.TaskBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.request.export.ExportStructuredDataReq;
import cn.malgo.annotation.service.AddBlocksToTaskService;
import cn.malgo.service.biz.TransactionalBiz;
import cn.malgo.service.exception.InvalidInputException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExportStructuredDataBiz extends TransactionalBiz<ExportStructuredDataReq, Object> {

  private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;
  private final AnnotationTaskRepository annotationTaskRepository;
  private final TaskBlockRepository taskBlockRepository;
  private final AddBlocksToTaskService addBlocksToTaskService;

  public ExportStructuredDataBiz(
      final AnnotationTaskBlockRepository annotationTaskBlockRepository,
      final TaskBlockRepository taskBlockRepository,
      final AnnotationTaskRepository annotationTaskRepository,
      final AddBlocksToTaskService addBlocksToTaskService) {
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
    this.annotationTaskRepository = annotationTaskRepository;
    this.taskBlockRepository = taskBlockRepository;
    this.addBlocksToTaskService = addBlocksToTaskService;
  }

  @Override
  protected void validateRequest(ExportStructuredDataReq exportStructuredDataReq)
      throws InvalidInputException {
    if (exportStructuredDataReq.getMultipartFiles() == null
        && exportStructuredDataReq.getMultipartFiles().size() == 0) {
      throw new InvalidInputException("invalid-multipartFiles", "无效的参数multipart-files");
    }
  }

  @Override
  protected Object doBiz(ExportStructuredDataReq req) {
    try {
      final List<Pair<String, String>> pairList = Lists.newArrayList();
      extractUploadJsonInfo(req, pairList);
      createBlocksAndTask(pairList);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return "当前数据导入已完成";
  }

  private void createBlocksAndTask(List<Pair<String, String>> pairList) {
    final List<AnnotationTaskBlock> annotationTaskBlocks =
        pairList
            .parallelStream()
            .map(
                pair ->
                    new AnnotationTaskBlock(
                        pair.getKey(),
                        pair.getValue(),
                        "",
                        0d,
                        AnnotationTaskState.CREATED,
                        AnnotationTypeEnum.relation,
                        0L,
                        "结构化立项处理数据"))
            .collect(Collectors.toList());
    final String time = simpleDateFormat.format(System.currentTimeMillis());
    final AnnotationTask task = new AnnotationTask(StringUtils.join("结构化", time));
    Lists.partition(annotationTaskBlocks, Math.max(annotationTaskBlocks.size() / 10, 10))
        .forEach(sub -> annotationTaskBlockRepository.saveAll(sub));
    annotationTaskRepository.save(task);
    final List<TaskBlock> addTaskBlockList =
        annotationTaskBlocks.stream()
            .map(annotationTaskBlock -> new TaskBlock(task, annotationTaskBlock))
            .collect(Collectors.toList());
    Lists.partition(addTaskBlockList, Math.max(addTaskBlockList.size() / 10, 10))
        .forEach(sub -> taskBlockRepository.saveAll(sub));
    addBlocksToTaskService.addBlocksToTaskFast(task, annotationTaskBlocks);
  }

  private void extractUploadJsonInfo(
      ExportStructuredDataReq req, List<Pair<String, String>> pairList) throws IOException {
    final InputStream inputStream = req.getMultipartFiles().get(0).getInputStream();
    final String str =
        CharStreams.toString(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    final JSONObject jsonObject = JSON.parseObject(str);
    final JSONArray recordJsonArr = jsonObject.getJSONArray("RECORDS");
    for (int i = 0; i < recordJsonArr.size(); i++) {
      final JSONObject recordObj = recordJsonArr.getJSONObject(i);
      final String text = recordObj.getString("text");
      final String annotation = recordObj.getString("annotation");
      pairList.add(Pair.of(text, annotation));
    }
  }
}
