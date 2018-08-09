package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.PersonalAnnotatedTotalWordNumRecord;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PersonalAnnotatedEstimatePriceRepository
    extends JpaRepository<PersonalAnnotatedTotalWordNumRecord, Long>,
        JpaSpecificationExecutor<PersonalAnnotatedTotalWordNumRecord> {

  PersonalAnnotatedTotalWordNumRecord findByTaskIdEqualsAndAssigneeIdEquals(
      long taskId, long assigneeId);

  List<PersonalAnnotatedTotalWordNumRecord> findAllByTaskIdEquals(long taskId);

  List<PersonalAnnotatedTotalWordNumRecord> findAllByAssigneeIdEquals(long assigneeId);
}
