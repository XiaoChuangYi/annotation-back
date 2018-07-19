package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.OriginalDoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OriginalDocRepository
    extends JpaRepository<OriginalDoc, Long>, JpaSpecificationExecutor<OriginalDoc> {}
