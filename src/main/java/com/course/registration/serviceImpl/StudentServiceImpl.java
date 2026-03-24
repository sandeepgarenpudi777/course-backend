package com.course.registration.serviceImpl;

import com.course.registration.dto.StudentDTO;
import com.course.registration.entity.Student;
import com.course.registration.exception.ResourceNotFoundException;
import com.course.registration.repository.StudentRepository;
import com.course.registration.repository.UserRepository;
import com.course.registration.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDTO getStudentById(Long id) {
        return toDTO(studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id)));
    }

    @Override
    public StudentDTO createStudent(StudentDTO dto) {
        if (studentRepository.existsByEmail(dto.getEmail()))
            throw new IllegalArgumentException("Email already registered: " + dto.getEmail());
        if (studentRepository.existsByStudentId(dto.getStudentId()))
            throw new IllegalArgumentException("Student ID already exists: " + dto.getStudentId());

        Student student = Student.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .studentId(dto.getStudentId())
                .phone(dto.getPhone())
                .build();

        // Auto-link User account if same email exists
        userRepository.findByEmail(dto.getEmail()).ifPresent(student::setUser);

        return toDTO(studentRepository.save(student));
    }

    @Override
    public StudentDTO updateStudent(Long id, StudentDTO dto) {
        Student existing = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
        if (!existing.getEmail().equals(dto.getEmail()) && studentRepository.existsByEmail(dto.getEmail()))
            throw new IllegalArgumentException("Email already in use: " + dto.getEmail());

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setEmail(dto.getEmail());
        existing.setPhone(dto.getPhone());
        return toDTO(studentRepository.save(existing));
    }

    @Override
    public void deleteStudent(Long id) {
        studentRepository.delete(studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDTO getStudentByEmail(String email) {
        return toDTO(studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "email", email)));
    }

    private StudentDTO toDTO(Student s) {
        return StudentDTO.builder()
                .id(s.getId())
                .firstName(s.getFirstName())
                .lastName(s.getLastName())
                .email(s.getEmail())
                .studentId(s.getStudentId())
                .phone(s.getPhone())
                .build();
    }
}
