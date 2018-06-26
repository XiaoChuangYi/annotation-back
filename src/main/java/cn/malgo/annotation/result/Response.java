package cn.malgo.annotation.result;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/** Created by cjl on 2018/5/28. */
@EqualsAndHashCode
@ToString
public class Response<T> {
  // 实际数据
  @Getter
  @JSONField(serialzeFeatures = {SerializerFeature.WriteMapNullValue})
  private T data;

  // String类型的code, 'not-exists', 'server-error'
  // 正常这个为空
  @Getter
  @JSONField(serialzeFeatures = {SerializerFeature.WriteMapNullValue})
  private String code;

  // 中文的error message
  @Getter
  @JSONField(serialzeFeatures = {SerializerFeature.WriteMapNullValue})
  private String message;

  public Response(T data) {
    this.data = data;
  }

  public Response(T data, String message) {
    this.data = data;
    this.message = message;
  }

  public Response(String code, String message) {
    this.code = code;
    this.message = message;
  }
}
