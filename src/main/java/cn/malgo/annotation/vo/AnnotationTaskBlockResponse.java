package cn.malgo.annotation.vo;

import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.utils.AnnotationConvert;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Value;

@Value
public class AnnotationTaskBlockResponse {
  private final int id;

  private final String text;

  private final JSONObject annotation;

  @JSONField(serialzeFeatures = SerializerFeature.WriteEnumUsingName)
  private final AnnotationTaskState state;

  private final AnnotationTypeEnum annotationType;

  public AnnotationTaskBlockResponse(final AnnotationTaskBlock block) {
    this.id = block.getId();
    this.text = block.getText();
    this.annotation =
        AnnotationConvert.convertAnnotation2BratFormat(
            block.getText(), block.getAnnotation(), block.getAnnotationType().ordinal());
    this.state = block.getState();
    this.annotationType = block.getAnnotationType();
  }
}
