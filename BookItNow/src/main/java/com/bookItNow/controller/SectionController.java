package com.bookItNow.controller;

import com.bookItNow.entity.Section;
import com.bookItNow.service.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/section")
public class SectionController {

    @Autowired
    private SectionService sectionService;

    @PostMapping("/{eventId}/add")
    public ResponseEntity<Section> addSection(@PathVariable Integer eventId, @RequestBody Section section) {

        return new ResponseEntity<>(sectionService.createSection(eventId, section), HttpStatus.CREATED);
    }

    @PutMapping("/{sectionId}/{capacity}")
    public ResponseEntity<Section> updateSectionCapacity(@PathVariable Integer sectionId, @PathVariable Integer capacity) {
        return new ResponseEntity<>(sectionService.updateSectionCapacity(sectionId, capacity), HttpStatus.ACCEPTED);
    }

    @GetMapping("/{sectionId}")
    public ResponseEntity<Section> getSection(@PathVariable Integer sectionId) {
        return new ResponseEntity<>(sectionService.findSectionById(sectionId), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{sectionId}")
    public ResponseEntity<String> deleteSection(@PathVariable Integer sectionId) {
        return new ResponseEntity<>(sectionService.deleteSection(sectionId),HttpStatus.OK);
    }
}
