package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AtomicTermRepository;
import cn.malgo.annotation.entity.AtomicTerm;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.service.AtomicTermSegmentService;
import cn.malgo.core.definition.Entity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.stereotype.Service;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.dic.Dictionary;
import org.wltea.analyzer.lucene.IKAnalyzer;

@Service
@Slf4j
public class AtomicTermSegmentServiceImpl implements AtomicTermSegmentService {
  private final AtomicTermRepository atomicTermRepository;
  private final HashMap<String, String> wordTypes = new HashMap<>(10000);
  private IKAnalyzer analyzer;

  public AtomicTermSegmentServiceImpl(final AtomicTermRepository atomicTermRepository) {
    this.atomicTermRepository = atomicTermRepository;
  }

  @PostConstruct
  public void init() {
    final List<String> words = new ArrayList<>();

    for (final AtomicTerm atomicTerm : atomicTermRepository.findAll()) {
      if (atomicTerm.getAnnotationType() == AnnotationTypeEnum.disease) {
        words.add(atomicTerm.getTerm());
        wordTypes.put(atomicTerm.getTerm(), atomicTerm.getAnType());
      }
    }

    Dictionary.initial(DefaultConfig.getInstance());
    Dictionary dict = Dictionary.getSingleton();
    dict.addWords(words);

    this.analyzer = new IKAnalyzer(true);
  }

  @Override
  public List<Entity> seg(final AnnotationTypeEnum annotationType, final String text) {
    if (annotationType != AnnotationTypeEnum.disease) {
      log.warn("unsupported annotation type {}", annotationType);
      return new ArrayList<>();
    }

    try {
      final List<Entity> result = new ArrayList<>();

      final TokenStream ts = analyzer.tokenStream("title", text);
      final CharTermAttribute cta = ts.addAttribute(CharTermAttribute.class);
      ts.reset();

      int i = 0, index = 0;
      while (ts.incrementToken()) {
        i++;

        final String term = cta.toString();
        final int start = text.indexOf(term, index);

        if (wordTypes.containsKey(term)) {
          result.add(new Entity("T" + i, start, start + term.length(), term, wordTypes.get(term)));
        }

        index = start + term.length();
      }

      return result;
    } catch (IOException e) {
      log.warn("segment failed", e);
      return new ArrayList<>();
    }
  }

  @Override
  public void addAtomicTerms(
      final AnnotationTypeEnum annotationType, final List<AtomicTerm> newTerms) {
    if (annotationType == AnnotationTypeEnum.disease) {
      Dictionary.getSingleton()
          .addWords(newTerms.stream().map(AtomicTerm::getTerm).collect(Collectors.toList()));
      newTerms.forEach(term -> wordTypes.put(term.getTerm(), term.getAnType()));
    }
  }
}
