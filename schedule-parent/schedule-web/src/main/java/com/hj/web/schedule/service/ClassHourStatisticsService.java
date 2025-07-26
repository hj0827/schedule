package com.hj.web.schedule.service;

import com.hj.web.schedule.entity.ClassHourStatistics;
import java.time.LocalDateTime;
import java.util.List;

public interface ClassHourStatisticsService {
    List<ClassHourStatistics> findStatistics(Long teacherId, Long courseId, LocalDateTime startDate, LocalDateTime endDate);
    
    ClassHourStatistics findById(Long id);
    
    void updateStatistics(ClassHourStatistics statistics);
    
    void createStatistics(ClassHourStatistics statistics);
} 