package org.tix.backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tix.backend.model.Batch;
import org.tix.backend.model.Task;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {


    List<Task> getAllByBatchId(Batch batch);

    Optional<Task> findByBatchId(Batch batchId);
}
