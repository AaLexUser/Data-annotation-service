package org.tix.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tix.backend.model.User;
import org.tix.backend.model.UserMarkup;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMarkupRepository extends JpaRepository<UserMarkup, Long> {
    List<UserMarkup> findByTaskId(Long taskId);
    Optional<UserMarkup> findByTaskIdAndAssessorId(Long task_id, Long assessor_id);

    int countByTaskIdAndAssessor(Long task_id, User assessor);
}
