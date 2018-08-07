package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.PersonalAnnotatedTotalWordNumRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PersonalAnnotatedEstimatePriceRepository
    extends JpaRepository<PersonalAnnotatedTotalWordNumRecord, Long>,
        JpaSpecificationExecutor<PersonalAnnotatedTotalWordNumRecord> {

  PersonalAnnotatedTotalWordNumRecord findByTaskIdEqualsAndAssigneeIdEquals(
      long taskId, long assigneeId);
}
