package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AtomicTermRepository;
import cn.malgo.annotation.entity.AtomicTerm;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.service.AtomicTermSegmentService;
import cn.malgo.core.definition.Entity;
import com.hankcs.hanlp.collection.trie.bintrie.BinTrie;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.dictionary.CoreDictionary.Attribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AtomicTermSegmentServiceImpl implements AtomicTermSegmentService {
  private final AtomicTermRepository atomicTermRepository;
  private HashMap<AnnotationTypeEnum, BinTrie<Attribute>> tries = new HashMap<>();

  public AtomicTermSegmentServiceImpl(final AtomicTermRepository atomicTermRepository) {
    this.atomicTermRepository = atomicTermRepository;
  }

  @PostConstruct
  public void init() {
    for (final AtomicTerm atomicTerm : atomicTermRepository.findAll()) {
      tries
          .computeIfAbsent(atomicTerm.getAnnotationType(), (ann) -> new BinTrie<>())
          .put(atomicTerm.getTerm(), new Attribute(Nature.create(atomicTerm.getAnType()), 1));
    }

    tries.forEach(
        (key, value) -> {
          log.info("atomic term loaded: {}, size: {}", key, value.size());
        });
  }

  @Override
  public List<Entity> seg(final AnnotationTypeEnum annotationType, final String text) {
    if (tries.containsKey(annotationType)) {
      final List<Entity> entities = new ArrayList<>();
      tries
          .get(annotationType)
          .parseText(
              text,
              (begin, end, value) -> {
                if (value != null && value.nature.length > 0) {
                  entities.add(
                      new Entity(
                          "T" + (entities.size() + 1),
                          begin,
                          end,
                          value.nature[0].toString(),
                          text.substring(begin, end)));
                }
              });
      return entities;
    }

    return null;
  }

  @Override
  public void addAtomicTerms(
      final AnnotationTypeEnum annotationType, final List<AtomicTerm> newTerms) {
    final BinTrie<Attribute> trie = tries.computeIfAbsent(annotationType, (ann) -> new BinTrie<>());
    newTerms.forEach(
        atomicTerm ->
            trie.put(
                atomicTerm.getTerm(), new Attribute(Nature.create(atomicTerm.getAnType()), 1)));
  }
}
