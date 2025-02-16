package org.tix.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tix.backend.model.UserMarkup;

import java.util.List;

@Repository
public interface UserMarkupRepository extends JpaRepository<UserMarkup, Long> {
    List<UserMarkup> findByTaskId(Long taskId);
}
