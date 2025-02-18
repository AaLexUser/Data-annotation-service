package org.tix.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tix.backend.model.HoneypotTask;
import java.util.List;
import java.util.Optional;

@Repository
public interface HoneypotTaskRepository extends JpaRepository<HoneypotTask, Long> {
    List<HoneypotTask> findByBatchId(Long batchId);
    Optional<HoneypotTask> findByBatchIdAndTaskId(Long batchId, Long taskId);
} 