package cn.malgo.annotation.vo;

import cn.malgo.annotation.entity.AnType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

@Value
public class AnTypeVO {
  private long id;
  private String typeName;
  private String typeCode;
  private long parentId;
  private int hasChildren;
  private int taskId;
  private List<String> labels;
  private String bgColor;
  private String fgColor;

  public AnTypeVO(final AnType anType) {
    this.id = anType.getId();
    this.typeName = anType.getTypeName();
    this.typeCode = anType.getTypeCode();
    this.parentId = anType.getParentId();
    this.hasChildren = anType.getHasChildren();
    this.taskId = anType.getTaskId();
    this.bgColor = anType.getBgColor();
    this.fgColor = anType.getFgColor();

    this.labels = new ArrayList<>();
    if (StringUtils.isNotBlank(anType.getLabels())) {
      this.labels.addAll(Arrays.asList(StringUtils.split(anType.getLabels(), ",")));
    }

    if (!this.labels.contains(this.typeName)) {
      this.labels.add(0, this.typeName);
    }
  }
}
