package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AtomicTermRepository;
import cn.malgo.annotation.entity.AtomicTerm;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.service.AtomicTermSegmentService;
import cn.malgo.core.definition.Entity;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.SpeedTokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AtomicTermSegmentServiceImpl implements AtomicTermSegmentService {
  private final AtomicTermRepository atomicTermRepository;
  private final Map<String, String> wordTypes = new HashMap<>(10000);

  public AtomicTermSegmentServiceImpl(final AtomicTermRepository atomicTermRepository) {
    this.atomicTermRepository = atomicTermRepository;
  }

  @PostConstruct
  public void init() {
    for (final AtomicTerm atomicTerm : atomicTermRepository.findAll()) {
      if (atomicTerm.getAnnotationType() == AnnotationTypeEnum.disease) {
        CustomDictionary.add(atomicTerm.getTerm(), atomicTerm.getAnType() + " 1");
        wordTypes.put(atomicTerm.getTerm(), atomicTerm.getAnType());
      }
    }

    SpeedTokenizer.SEGMENT.enableOffset(true);
    SpeedTokenizer.SEGMENT.enableCustomDictionaryForcing(true);
    SpeedTokenizer.SEGMENT.enablePartOfSpeechTagging(true);
  }

  @Override
  public List<Entity> seg(final AnnotationTypeEnum annotationType, final String text) {
    if (annotationType != AnnotationTypeEnum.disease) {
      log.warn("不支持的标注类型: {}", annotationType);
      return new ArrayList<>();
    }

    final List<Term> segment = SpeedTokenizer.segment(text);
    if (segment == null) {
      log.warn("{} 分词结果未null", text);
      return new ArrayList<>();
    }

    final List<Entity> entities = new ArrayList<>(segment.size());
    for (int i = 1; i < segment.size() + 1; ++i) {
      final Term term = segment.get(i - 1);
      if (wordTypes.containsKey(term.word)) {
        entities.add(
            new Entity(
                "T" + i,
                term.offset,
                term.offset + term.length(),
                wordTypes.get(term.word),
                term.word));
      } else if (log.isDebugEnabled()) {
        log.debug("{} not found in atomic terms", term.word);
      }
    }
    return entities;
  }

  @Override
  public void addAtomicTerms(
      final AnnotationTypeEnum annotationType, final List<AtomicTerm> newTerms) {
    if (annotationType == AnnotationTypeEnum.disease) {
      newTerms.forEach(
          atomicTerm -> {
            CustomDictionary.add(atomicTerm.getTerm(), atomicTerm.getAnType() + " 1");
            wordTypes.put(atomicTerm.getTerm(), atomicTerm.getAnType());
          });
    }
  }
}
