package com.malgo.vo;

import com.malgo.dto.AnnotationWordError;
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