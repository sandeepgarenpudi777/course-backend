package com.course.registration.service;

import com.course.registration.dto.InstructorDTO;
import java.util.List;

public interface InstructorService {
    List<InstructorDTO> getAllInstructors();
    InstructorDTO getInstructorById(Long id);
    InstructorDTO createInstructor(InstructorDTO dto);
    InstructorDTO updateInstructor(Long id, InstructorDTO dto);
    void deleteInstructor(Long id);
}
