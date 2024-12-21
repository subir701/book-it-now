package com.bookItNow.service;

import com.bookItNow.entity.Section;
import com.bookItNow.exception.SectionNotFoundException;

public interface SectionService {

    public Section findSectionById(int id)throws SectionNotFoundException;
    public Section createSection(int eventId, Section section)throws RuntimeException;
    public Section updateSectionName(int id,String name)throws SectionNotFoundException;
    public Section updateSectionCapacity(int id,int updateCapacity)throws SectionNotFoundException;
    public String deleteSection(Integer Id) throws SectionNotFoundException;
}
