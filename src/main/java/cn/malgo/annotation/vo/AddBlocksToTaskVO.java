package cn.malgo.annotation.vo;

import lombok.Value;

@Value
public class AddBlocksToTaskVO {
  private final long taskId;
  private final int addedBlocks;
  private final int addedWordNum;
}
