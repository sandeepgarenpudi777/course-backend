package com.course.registration.serviceImpl;

import com.course.registration.dto.CourseSummaryDTO;
import com.course.registration.dto.InstructorDTO;
import com.course.registration.entity.Instructor;
import com.course.registration.exception.ResourceNotFoundException;
import com.course.registration.repository.InstructorRepository;
import com.course.registration.repository.UserRepository;
import com.course.registration.service.InstructorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InstructorServiceImpl implements InstructorService {

    private final InstructorRepository instructorRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<InstructorDTO> getAllInstructors() {
        return instructorRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InstructorDTO getInstructorById(Long id) {
        return toDTO(instructorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor", "id", id)));
    }

    @Override
    public InstructorDTO createInstructor(InstructorDTO dto) {
        if (instructorRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + dto.getEmail());
        }
        Instructor instructor = Instructor.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .department(dto.getDepartment())
                .qualification(dto.getQualification())
                .build();

        // Auto-link User account if same email exists
        userRepository.findByEmail(dto.getEmail()).ifPresent(instructor::setUser);

        return toDTO(instructorRepository.save(instructor));
    }

    @Override
    public InstructorDTO updateInstructor(Long id, InstructorDTO dto) {
        Instructor existing = instructorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor", "id", id));

        if (!existing.getEmail().equals(dto.getEmail()) &&
                instructorRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + dto.getEmail());
        }
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setEmail(dto.getEmail());
        existing.setDepartment(dto.getDepartment());
        existing.setQualification(dto.getQualification());
        return toDTO(instructorRepository.save(existing));
    }

    @Override
    public void deleteInstructor(Long id) {
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor", "id", id));
        instructorRepository.delete(instructor);
    }

    private InstructorDTO toDTO(Instructor i) {
        List<CourseSummaryDTO> courses = i.getCourses() == null ? List.of() :
                i.getCourses().stream().map(c -> CourseSummaryDTO.builder()
                        .id(c.getId())
                        .title(c.getTitle())
                        .description(c.getDescription())
                        .credits(c.getCredits())
                        .capacity(c.getCapacity())
                        .fee(c.getFee())
                        .status(c.getStatus())
                        .build()).collect(Collectors.toList());

        return InstructorDTO.builder()
                .id(i.getId())
                .firstName(i.getFirstName())
                .lastName(i.getLastName())
                .email(i.getEmail())
                .department(i.getDepartment())
                .qualification(i.getQualification())
                .courses(courses)
                .build();
    }
}
