package com.course.registration.serviceImpl;

import com.course.registration.dto.ScheduleDTO;
import com.course.registration.entity.Course;
import com.course.registration.entity.Schedule;
import com.course.registration.exception.ResourceNotFoundException;
import com.course.registration.repository.CourseRepository;
import com.course.registration.repository.ScheduleRepository;
import com.course.registration.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleDTO> getAllSchedules() {
        return scheduleRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ScheduleDTO getScheduleById(Long id) {
        return toDTO(scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", id)));
    }

    @Override
    public ScheduleDTO createSchedule(ScheduleDTO dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", dto.getCourseId()));
        Schedule schedule = Schedule.builder()
                .dayOfWeek(dto.getDayOfWeek())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .room(dto.getRoom())
                .course(course)
                .build();
        return toDTO(scheduleRepository.save(schedule));
    }

    @Override
    public ScheduleDTO updateSchedule(Long id, ScheduleDTO dto) {
        Schedule existing = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", id));
        existing.setDayOfWeek(dto.getDayOfWeek());
        existing.setStartTime(dto.getStartTime());
        existing.setEndTime(dto.getEndTime());
        existing.setRoom(dto.getRoom());
        return toDTO(scheduleRepository.save(existing));
    }

    @Override
    public void deleteSchedule(Long id) {
        scheduleRepository.delete(scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleDTO> getSchedulesByCourse(Long courseId) {
        return scheduleRepository.findByCourseId(courseId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    private ScheduleDTO toDTO(Schedule s) {
        return ScheduleDTO.builder()
                .id(s.getId())
                .dayOfWeek(s.getDayOfWeek())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .room(s.getRoom())
                .courseId(s.getCourse() != null ? s.getCourse().getId() : null)
                .courseTitle(s.getCourse() != null ? s.getCourse().getTitle() : null)
                .build();
    }
}
