package org.tix.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tix.backend.model.TaskAnswer;
import org.tix.backend.model.Task;
import org.tix.backend.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskAnswerRepository extends JpaRepository<TaskAnswer, Long> {
    List<TaskAnswer> findByTask(Task task);
    List<TaskAnswer> findByAssessor(User assessor);
    Optional<TaskAnswer> findByTaskAndAssessor(Task task, User assessor);
    List<TaskAnswer> findByTaskInAndAssessor(List<Task> tasks, User assessor);
    List<TaskAnswer> findByTaskInAndIsHoneypotTrue(List<Task> tasks);
    List<TaskAnswer> findByIsHoneypotTrue();
} 