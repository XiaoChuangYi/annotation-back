package cn.malgo.annotation.result;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

/** Created by cjl on 2018/4/16. */
@Data
@NoArgsConstructor
public class PageVO<T> {
  private long total;
  private List<T> dataList;

  public PageVO(Page<T> page) {
    this.setTotal(page.getTotalElements());
    this.setDataList(page.getContent());
  }

  public PageVO(com.github.pagehelper.Page<T> page) {
    this.setTotal(page.getTotal());
    this.setDataList(page.getResult());
  }

  public PageVO(Page<T> page, boolean setData) {
    this.setTotal(page.getTotalElements());
    if (setData) {
      this.setDataList(page.getContent());
    }
  }
}
