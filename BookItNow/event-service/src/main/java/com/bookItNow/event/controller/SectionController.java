package com.bookItNow.event.controller;

import com.bookItNow.event.model.Section;
import com.bookItNow.event.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookitnow/v1/sections")
@RequiredArgsConstructor
public class SectionController {

    private final SectionService sectionService;

    /**
     * Add a new section to an event.
     *
     * @param eventId The ID of the event to which the section is being added.
     * @param section The section details.
     * @return The created section.
     */
    @PostMapping("/add/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Section> addSectionToEvent(@PathVariable Integer eventId, @RequestBody Section section) {
        System.out.println("We reach the controller layer");
        return new ResponseEntity<>(sectionService.createSection(eventId, section), HttpStatus.CREATED);
    }

    /**
     * Update the capacity of a section.
     *
     * @param sectionId The ID of the section to update.
     * @param capacity  The new capacity of the section.
     * @return The updated section.
     */
    @PutMapping("/{sectionId}/capacity/{capacity}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Section> updateSectionCapacity(
            @PathVariable Integer sectionId,
            @PathVariable Integer capacity
    ) {
        return new ResponseEntity<>(sectionService.updateSectionCapacity(sectionId, capacity), HttpStatus.ACCEPTED);
    }

    /**
     * Retrieve a section by ID.
     *
     * @param sectionId The ID of the section to retrieve.
     * @return The section details.
     */
    @GetMapping("/view/{sectionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Section> getSectionById(@PathVariable Integer sectionId) {
        return new ResponseEntity<>(sectionService.findSectionById(sectionId), HttpStatus.OK);
    }

    /**
     * Delete a section by ID.
     *
     * @param sectionId The ID of the section to delete.
     * @return A success message.
     */
    @DeleteMapping("/delete/{sectionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteSectionById(@PathVariable Integer sectionId) {
        return new ResponseEntity<>(sectionService.deleteSection(sectionId), HttpStatus.OK);
    }
}
