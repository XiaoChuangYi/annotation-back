package cn.malgo.annotation.biz.doc;

import cn.malgo.annotation.dao.OriginalDocRepository;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.utils.FileUtil;
import cn.malgo.service.biz.TransactionalBiz;
import cn.malgo.service.exception.InvalidInputException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

@Component
public class ImportJsonDocBiz extends TransactionalBiz<Void, Object> {

  private final OriginalDocRepository originalDocRepository;
  private final Predicate<String> stringPredicate =
      Pattern.compile("(禁忌|不良反应|保健功能|药品名称|功能主治|适应症)").asPredicate();
  private final String sanJiuPath = "/Users/cjl/Documents/39说明书";

  public ImportJsonDocBiz(final OriginalDocRepository originalDocRepository) {
    this.originalDocRepository = originalDocRepository;
  }

  @Override
  protected void validateRequest(Void aVoid) throws InvalidInputException {}

  @Override
  protected Object doBiz(Void aVoid) {
    final List<Pair<String, String>> pairs = FileUtil.getDirectoryContent(sanJiuPath, false);
    return originalDocRepository.saveAll(
        pairs
            .parallelStream()
            .filter(
                pair ->
                    StringUtils.isNotBlank(pair.getLeft())
                        && StringUtils.isNotBlank(pair.getRight()))
            .map(
                pair ->
                    new OriginalDoc(
                        StringUtils.substring(pair.getLeft(), 0, 512),
                        formatJson(pair.getRight()),
                        "json",
                        "三九"))
            .collect(Collectors.toList()));
  }

  private String formatJson(String jsonStr) {
    final JSONObject jsonObject = JSONObject.parseObject(jsonStr, Feature.OrderedField);
    return jsonObject
        .entrySet()
        .parallelStream()
        .filter(stringObjectEntry -> stringPredicate.test(stringObjectEntry.getKey()))
        .map(
            stringObjectEntry ->
                stringObjectEntry
                        .getKey()
                        .replaceAll("【", "")
                        .replaceAll("】", "")
                        .replaceAll("[　*| *| *| *|\\s*]*", "")
                    + ":"
                    + stringObjectEntry.getValue().toString().replaceAll("[　*| *| *| *|\\s*]*", ""))
        .collect(Collectors.joining("\n"));
  }
}
