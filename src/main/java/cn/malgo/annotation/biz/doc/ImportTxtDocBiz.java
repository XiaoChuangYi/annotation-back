package cn.malgo.annotation.biz.doc;

import cn.malgo.annotation.dao.OriginalDocRepository;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.utils.FileUtil;
import cn.malgo.service.biz.TransactionalBiz;
import cn.malgo.service.exception.InvalidInputException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ImportTxtDocBiz extends TransactionalBiz<Void, Object> {

  private final OriginalDocRepository originalDocRepository;
  private final String medicalBooksPath = "/Users/cjl/Documents/第三期语料剩余疾病86";

  public ImportTxtDocBiz(final OriginalDocRepository originalDocRepository) {
    this.originalDocRepository = originalDocRepository;
  }

  @Override
  protected void validateRequest(Void aVoid) throws InvalidInputException {}

  @Override
  public Object doBiz(Void aVoid) {
    List<Pair<String, String>> pairs = Collections.emptyList();
    try {
      pairs = FileUtil.getContent(medicalBooksPath, false);
    } catch (IOException e) {
      e.printStackTrace();
    }
    log.info("result-pairs：{}", pairs);
    return null;
    //    return originalDocRepository.saveAll(
    //        pairs
    //            .parallelStream()
    //            .filter(
    //                pair ->
    //                    StringUtils.isNotBlank(pair.getLeft())
    //                        && StringUtils.isNotBlank(pair.getRight()))
    //            .map(
    //                pair ->
    //                    new OriginalDoc(pair.getLeft().substring(0, pair.getLeft().indexOf(".")),
    //                        pair.getRight(), "txt", "万方|诊疗指南|教材|第三批")
    //            )
    //            .collect(Collectors.toList()));
  }
}
