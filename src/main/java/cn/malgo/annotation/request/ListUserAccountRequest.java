package cn.malgo.annotation.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Created by cjl on 2018/5/30. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListUserAccountRequest {
  private int pageIndex;
  private int pageSize;
  private boolean all;

  public ListUserAccountRequest(int pageIndex, int pageSize) {
    this.pageIndex = pageIndex;
    this.pageSize = pageSize;
  }
}
