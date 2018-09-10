package cn.malgo.annotation.vo;

import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.entity.TaskBlock;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.utils.AnnotationConvert;
import com.alibaba.fastjson.JSONObject;
import java.util.Comparator;
import lombok.Value;

@Value
public class AnnotationTaskBlockResponse {

  private final long id;

  private final long taskId;

  private final String text;

  private final JSONObject annotation;

  private final String state;

  private final AnnotationTypeEnum annotationType;

  private final double nerFreshRate;

  private final String assignee;

  public AnnotationTaskBlockResponse(final AnnotationTaskBlock block, final String assignee) {
    this(block, assignee, true);
  }

  public AnnotationTaskBlockResponse(
      final AnnotationTaskBlock block, String assignee, boolean parseAnnotation) {
    this.id = block.getId();
    this.taskId =
        block.getTaskBlocks().size() > 0
            ? block
                .getTaskBlocks()
                .stream()
                .sorted(Comparator.comparing(TaskBlock::getCreatedTime, Comparator.reverseOrder()))
                .findFirst()
                .get()
                .getTask()
                .getId()
            : 0;
    this.text = block.getText();
    this.state = block.getState().name();
    this.annotationType = block.getAnnotationType();
    this.nerFreshRate = block.getNerFreshRate();
    this.assignee = assignee;
    if (parseAnnotation) {
      this.annotation =
          AnnotationConvert.convertAnnotation2BratFormat(
              block.getText(), block.getAnnotation(), block.getAnnotationType().ordinal());
    } else {
      this.annotation = null;
    }
  }
}
