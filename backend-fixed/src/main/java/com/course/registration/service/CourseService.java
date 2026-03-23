package com.course.registration.service;

import com.course.registration.dto.CourseDTO;
import java.util.List;

public interface CourseService {
    List<CourseDTO> getAllCourses();
    CourseDTO getCourseById(Long id);
    // ── ISSUE 3 FIX: accept callerUsername to auto-assign instructor ──
    CourseDTO createCourse(CourseDTO courseDTO, String callerUsername);
    CourseDTO updateCourse(Long id, CourseDTO courseDTO, String callerUsername);
    void deleteCourse(Long id);
    List<CourseDTO> getCoursesByInstructor(Long instructorId);
}
