package com.course.registration.dto;

import com.course.registration.entity.Enrollment;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EnrollmentDTO {

    private Long id;

    // ── ISSUE 2 FIX: both fields are required for enrollment creation ──
    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    private LocalDateTime enrollmentDate;

    // Updated to include ENROLLED status
    private Enrollment.EnrollmentStatus status;

    private String studentName;
    private String courseName;
}
