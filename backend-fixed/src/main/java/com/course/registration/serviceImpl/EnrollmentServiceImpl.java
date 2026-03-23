package com.course.registration.serviceImpl;

import com.course.registration.dto.EnrollmentDTO;
import com.course.registration.entity.Course;
import com.course.registration.entity.Enrollment;
import com.course.registration.entity.Student;
import com.course.registration.exception.ResourceNotFoundException;
import com.course.registration.repository.CourseRepository;
import com.course.registration.repository.EnrollmentRepository;
import com.course.registration.repository.StudentRepository;
import com.course.registration.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getAllEnrollments() {
        return enrollmentRepository.findAll().stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentDTO getEnrollmentById(Long id) {
        return toDTO(enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", id)));
    }

    @Override
    public EnrollmentDTO createEnrollment(EnrollmentDTO dto) {
        // ── ISSUE 2 FIX: validate studentId and courseId are present ──
        if (dto.getStudentId() == null)
            throw new IllegalArgumentException("studentId is required");
        if (dto.getCourseId() == null)
            throw new IllegalArgumentException("courseId is required");

        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", dto.getStudentId()));
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", dto.getCourseId()));

        // Prevent duplicate enrollment
        if (enrollmentRepository.existsByStudentIdAndCourseId(dto.getStudentId(), dto.getCourseId())) {
            throw new IllegalArgumentException("Student is already enrolled in this course");
        }

        // Check capacity
        long enrolled = enrollmentRepository.countByCourseId(dto.getCourseId());
        if (enrolled >= course.getCapacity()) {
            throw new IllegalArgumentException("Course is at full capacity");
        }

        // ── ISSUE 2 FIX: set enrollmentDate explicitly + default to ENROLLED ──
        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .enrollmentDate(LocalDateTime.now())
                .status(Enrollment.EnrollmentStatus.ENROLLED)
                .build();

        return toDTO(enrollmentRepository.save(enrollment));
    }

    @Override
    public EnrollmentDTO updateEnrollment(Long id, EnrollmentDTO dto) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", id));
        if (dto.getStatus() != null) enrollment.setStatus(dto.getStatus());
        return toDTO(enrollmentRepository.save(enrollment));
    }

    @Override
    public void deleteEnrollment(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", id));
        enrollmentRepository.delete(enrollment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getEnrollmentsByCourse(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    private EnrollmentDTO toDTO(Enrollment e) {
        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setId(e.getId());
        dto.setEnrollmentDate(e.getEnrollmentDate());
        dto.setStatus(e.getStatus());
        if (e.getStudent() != null) {
            dto.setStudentId(e.getStudent().getId());
            dto.setStudentName(e.getStudent().getFirstName() + " " + e.getStudent().getLastName());
        }
        if (e.getCourse() != null) {
            dto.setCourseId(e.getCourse().getId());
            dto.setCourseName(e.getCourse().getTitle());
        }
        return dto;
    }
}
