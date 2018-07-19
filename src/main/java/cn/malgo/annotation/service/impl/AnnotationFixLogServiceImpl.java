package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationFixLogRepository;
import cn.malgo.annotation.entity.AnnotationFixLog;
import cn.malgo.annotation.enums.AnnotationFixLogStateEnum;
import cn.malgo.annotation.service.AnnotationFixLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AnnotationFixLogServiceImpl implements AnnotationFixLogService {
  private final AnnotationFixLogRepository repository;

  @Autowired
  public AnnotationFixLogServiceImpl(AnnotationFixLogRepository repository) {
    this.repository = repository;
  }

  @Override
  public AnnotationFixLog insertOrUpdate(
      long annotationId, int start, int end, AnnotationFixLogStateEnum state) {
    AnnotationFixLog fixLog = new AnnotationFixLog();
    fixLog.setAnnotationId(annotationId);
    fixLog.setStart(start);
    fixLog.setEnd(end);
    fixLog.setState(state.name());

    try {
      fixLog = repository.save(fixLog);
    } catch (DataIntegrityViolationException ex) {
      log.warn(
          "save annotation fix log unique key error: "
              + annotationId
              + ", start: "
              + start
              + ", end: "
              + end
              + ", state: "
              + state,
          ex);

      fixLog = repository.findByAnnotationIdAndStartAndEnd(annotationId, start, end);
      if (!state.name().equals(fixLog.getState())) {
        fixLog.setState(state.name());
        fixLog = repository.save(fixLog);
      }
    }

    return fixLog;
  }
}
