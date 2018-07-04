package cn.malgo.annotation.biz.task;

import cn.malgo.annotation.biz.AdminBaseBiz;
import cn.malgo.annotation.dao.OriginalDocRepository;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.task.ImportDocRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ImportDocBiz extends AdminBaseBiz<ImportDocRequest, List<OriginalDoc>> {
  private static final List<Pair<Predicate<String>, String>> TYPE_PATTERNS =
      Arrays.asList(
          Pair.of(Pattern.compile("(外伤|骨折|骨裂)").asPredicate(), "外伤"),
          Pair.of(Pattern.compile("(心梗|中风|脑淤血|心脏病|冠心病|心肌)").asPredicate(), "心脑血管"));

  private final OriginalDocRepository originalDocRepository;

  public ImportDocBiz(final OriginalDocRepository originalDocRepository) {
    this.originalDocRepository = originalDocRepository;
  }

  private static String getDocType(ImportDocRequest.ImportDoc doc) {
    if (StringUtils.contains(doc.getName(), "癌")) {
      return "癌";
    }

    for (final Pair<Predicate<String>, String> pattern : TYPE_PATTERNS) {
      if (pattern.getLeft().test(doc.getName())) {
        return pattern.getRight();
      }
    }

    return "UNKNOWN";
  }

  @Override
  protected void validateRequest(ImportDocRequest request) throws InvalidInputException {}

  @Override
  protected List<OriginalDoc> doBiz(ImportDocRequest request) {
    return originalDocRepository.saveAll(
        request
            .getData()
            .stream()
            .filter(
                doc ->
                    StringUtils.isNotBlank(doc.getName()) && StringUtils.isNotBlank(doc.getText()))
            .map(
                doc ->
                    new OriginalDoc(
                        StringUtils.substring(doc.getName(), 0, 512),
                        doc.getText(),
                        getDocType(doc),
                        request.getSource()))
            .collect(Collectors.toList()));
  }
}
