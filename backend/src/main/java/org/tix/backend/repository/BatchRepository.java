package org.tix.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.tix.backend.model.Batch;

import java.util.List;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {
    @Query("SELECT b FROM Batch b JOIN b.availableUsers u WHERE u.id = :userId")
    List<Batch> findAllByUserIdInAvailableUsers(@Param("userId") Long userId);
}
