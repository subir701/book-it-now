package com.bookItNow.repository;

import com.bookItNow.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Integer> {
    Optional<Section> findByIdAndEventId(int sectionId, int eventId);
}
