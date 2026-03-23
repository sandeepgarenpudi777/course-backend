package com.course.registration.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"student", "course", "payment"})
@EqualsAndHashCode(exclude = {"student", "course", "payment"})
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime enrollmentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnore
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnore
    private Course course;

    @OneToOne(mappedBy = "enrollment", cascade = CascadeType.ALL)
    @JsonIgnore
    private Payment payment;

    @PrePersist
    public void prePersist() {
        if (enrollmentDate == null) enrollmentDate = LocalDateTime.now();
        // ── ISSUE 2 FIX: default status is ENROLLED (not PENDING) ──
        if (status == null) status = EnrollmentStatus.ENROLLED;
    }

    public enum EnrollmentStatus {
        ENROLLED, PENDING, CONFIRMED, CANCELLED, COMPLETED
    }
}
