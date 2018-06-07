package com.malgo.request;

import lombok.Data;

/**
 * Created by cjl on 2018/5/30.
 */
@Data
public class ListUserAccountRequest {
  private int pageIndex;
  private int pageSize;
  private boolean all;
}
