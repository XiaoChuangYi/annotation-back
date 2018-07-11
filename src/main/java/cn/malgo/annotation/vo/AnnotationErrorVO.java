package cn.malgo.annotation.vo;

import cn.malgo.annotation.dto.error.AnnotationWordError;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * [{ "word": "", "errors": [{ "type": "Disease", "annotations": [{ "id": "annotation-id", "start":
 * "", "end": "", "annotation": { "entities": [], "relations": [], "sentence_offsets": [], "text":
 * "", "token_offsets": [] } }] }] }]
 */
@Data
@AllArgsConstructor
public class AnnotationErrorVO {
  private List<AnnotationWordError> errors;
}
