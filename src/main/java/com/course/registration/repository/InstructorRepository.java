package com.course.registration.repository;

import com.course.registration.entity.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    Optional<Instructor> findByEmail(String email);
    List<Instructor> findByDepartment(String department);
    boolean existsByEmail(String email);
}
