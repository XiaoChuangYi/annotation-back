package cn.malgo.annotation.biz.doc;

import cn.malgo.annotation.dao.OriginalDocRepository;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.utils.FileUtil;
import cn.malgo.service.biz.TransactionalBiz;
import cn.malgo.service.exception.InvalidInputException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import java.io.IOException;
import java.util.Collections;
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
      Pattern.compile(
              "(药品名称|通用名|通用名称|产品名称|商品名|商品名称|化学名称|适应症|适应症/主治功能|适应症/功能主治|功能主治|主要作用|作用类别|保健功能|注射剂辅料|注意事项|禁忌|禁忌症|不良反应|药物过量| 药物互相作用|药物相互作用|药理作用|药用相互作用|不适宜人群|适宜人群|特殊人群用药|儿童用药|老年患者用药|老年用药|妊娠及哺乳期妇女用药|妊娠期妇女及哺乳期妇女用药|孕妇及哺乳其妇女用药|孕妇及哺乳妇女用药| 孕妇及哺乳期妇女用药|孕妇及哺乳期用药|给药说明)")
          .asPredicate();
  private final String sanJiuPath = "/Users/cjl/Documents/2019-02-27_第二批药品/第二批药品";

  public ImportJsonDocBiz(final OriginalDocRepository originalDocRepository) {
    this.originalDocRepository = originalDocRepository;
  }

  @Override
  protected void validateRequest(Void aVoid) throws InvalidInputException {}

  @Override
  protected Object doBiz(Void aVoid) {
    List<Pair<String, String>> pairs = Collections.emptyList();
    try {
      pairs = FileUtil.getContent(sanJiuPath, false);
    } catch (IOException e) {
      e.printStackTrace();
    }
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
                        "三九第二期"))
            .collect(Collectors.toList()));
  }

  private String formatJson(String jsonStr) {
    final JSONObject jsonObject = JSONObject.parseObject(jsonStr, Feature.OrderedField);
    return jsonObject
        .entrySet()
        .parallelStream()
        //        .filter(stringObjectEntry -> stringPredicate.test(stringObjectEntry.getKey()))
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
