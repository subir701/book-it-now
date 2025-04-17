package com.bookItNow.event.repository;

import com.bookItNow.event.exception.EventNotFoundException;
import com.bookItNow.event.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Integer> {

    Optional<Section> findByIdAndEventId(int sectionId, int eventId);
    // This custom method lets you find a section by both its ID and the associated event ID.
    // It's helpful when you want to ensure that the section truly belongs to a specific event.
    // Using `Optional` helps to avoid null checks and makes the code cleaner when dealing with results.

    Integer countByEventId(int eventId)throws EventNotFoundException;

}
