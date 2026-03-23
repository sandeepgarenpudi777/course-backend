package com.course.registration.serviceImpl;

import com.course.registration.dto.CourseDTO;
import com.course.registration.entity.Course;
import com.course.registration.entity.Instructor;
import com.course.registration.exception.ResourceNotFoundException;
import com.course.registration.repository.CourseRepository;
import com.course.registration.repository.InstructorRepository;
import com.course.registration.repository.UserRepository;
import com.course.registration.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDTO getCourseById(Long id) {
        return toDTO(courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id)));
    }

    @Override
    public CourseDTO createCourse(CourseDTO dto, String callerUsername) {
        // ── ISSUE 3 FIX: if instructor doesn't supply instructorId,
        //    auto-resolve from the logged-in instructor's email ──
        Instructor instructor = resolveInstructor(dto.getInstructorId(), callerUsername);

        Course course = Course.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .credits(dto.getCredits())
                .capacity(dto.getCapacity())
                .fee(dto.getFee())
                .status(dto.getStatus() != null ? dto.getStatus() : Course.CourseStatus.ACTIVE)
                .instructor(instructor)
                .build();

        return toDTO(courseRepository.save(course));
    }

    @Override
    public CourseDTO updateCourse(Long id, CourseDTO dto, String callerUsername) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));

        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setCredits(dto.getCredits());
        course.setCapacity(dto.getCapacity());
        course.setFee(dto.getFee());
        if (dto.getStatus() != null) course.setStatus(dto.getStatus());

        // Only change instructor if explicitly provided
        if (dto.getInstructorId() != null) {
            Instructor instructor = instructorRepository.findById(dto.getInstructorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Instructor", "id", dto.getInstructorId()));
            course.setInstructor(instructor);
        }

        return toDTO(courseRepository.save(course));
    }

    @Override
    public void deleteCourse(Long id) {
        courseRepository.delete(courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> getCoursesByInstructor(Long instructorId) {
        return courseRepository.findByInstructorId(instructorId).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    // ── ISSUE 3 FIX: resolve instructor by ID, or fall back to caller's profile ──
    private Instructor resolveInstructor(Long instructorId, String callerUsername) {
        if (instructorId != null) {
            return instructorRepository.findById(instructorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Instructor", "id", instructorId));
        }
        // Auto-resolve: find the instructor whose user has this username
        return userRepository.findByUsername(callerUsername)
                .flatMap(user -> instructorRepository.findByEmail(user.getEmail()))
                .orElseThrow(() -> new IllegalArgumentException(
                        "No instructor profile found for user '" + callerUsername +
                        "'. Please provide instructorId explicitly."));
    }

    private CourseDTO toDTO(Course c) {
        CourseDTO dto = new CourseDTO();
        dto.setId(c.getId());
        dto.setTitle(c.getTitle());
        dto.setDescription(c.getDescription());
        dto.setCredits(c.getCredits());
        dto.setCapacity(c.getCapacity());
        dto.setFee(c.getFee());
        dto.setStatus(c.getStatus());
        if (c.getInstructor() != null) {
            dto.setInstructorId(c.getInstructor().getId());
            dto.setInstructorName(c.getInstructor().getFirstName() + " " + c.getInstructor().getLastName());
        }
        return dto;
    }
}
