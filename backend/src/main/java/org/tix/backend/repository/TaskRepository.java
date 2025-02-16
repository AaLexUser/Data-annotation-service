package org.tix.backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tix.backend.model.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {


}
