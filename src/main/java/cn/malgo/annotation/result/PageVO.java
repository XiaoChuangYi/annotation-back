package cn.malgo.annotation.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PageVO<T> {
  private long total;
  private List<T> dataList;

  public PageVO(long total) {
    this.total = total;
  }

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
