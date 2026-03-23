package com.course.registration.dto;

import com.course.registration.entity.Course;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseSummaryDTO {
    private Long id;
    private String title;
    private String description;
    private Integer credits;
    private Integer capacity;
    private BigDecimal fee;
    private Course.CourseStatus status;
}
