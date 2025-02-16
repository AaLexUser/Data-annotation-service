package org.tix.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tix.backend.model.Markup;

@Repository
public interface MarkupRepository extends JpaRepository<Markup, Long> {

}
