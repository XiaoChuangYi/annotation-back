package cn.malgo.annotation.vo;

import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.utils.AnnotationConvert;
import com.alibaba.fastjson.JSONObject;
import lombok.Value;

@Value
public class AnnotationTaskBlockResponse {
  private final long id;

  private final String text;

  private final JSONObject annotation;

  private final String state;

  private final AnnotationTypeEnum annotationType;

  private final double nerFreshRate;

  public AnnotationTaskBlockResponse(final AnnotationTaskBlock block) {
    this(block, true);
  }

  public AnnotationTaskBlockResponse(final AnnotationTaskBlock block, boolean parseAnnotation) {
    this.id = block.getId();
    this.text = block.getText();
    this.state = block.getState().name();
    this.annotationType = block.getAnnotationType();
    this.nerFreshRate = block.getNerFreshRate();

    if (parseAnnotation) {
      this.annotation =
          AnnotationConvert.convertAnnotation2BratFormat(
              block.getText(), block.getAnnotation(), block.getAnnotationType().ordinal());
    } else {
      this.annotation = null;
    }
  }
}
