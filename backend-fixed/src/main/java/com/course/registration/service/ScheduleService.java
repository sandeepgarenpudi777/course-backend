package com.course.registration.service;

import com.course.registration.dto.ScheduleDTO;
import java.util.List;

public interface ScheduleService {
    List<ScheduleDTO> getAllSchedules();
    ScheduleDTO getScheduleById(Long id);
    ScheduleDTO createSchedule(ScheduleDTO dto);
    ScheduleDTO updateSchedule(Long id, ScheduleDTO dto);
    void deleteSchedule(Long id);
    List<ScheduleDTO> getSchedulesByCourse(Long courseId);
}
