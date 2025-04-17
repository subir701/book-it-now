package com.bookItNow.event.service;

import com.bookItNow.event.model.Section;
import com.bookItNow.event.exception.SectionNotFoundException;

public interface SectionService {

    public Section findSectionById(int id)throws SectionNotFoundException;
    public Section createSection(int eventId, Section section)throws RuntimeException;
    public Section updateSectionName(int id,String name)throws SectionNotFoundException;
    public Section updateSectionCapacity(int id,int updateCapacity)throws SectionNotFoundException;
    public String deleteSection(Integer Id) throws SectionNotFoundException;
}
