package com.course.registration.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstructorDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String department;
    private String qualification;
    private List<CourseSummaryDTO> courses;
}
