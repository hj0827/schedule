package com.hj.web.schedule.service.impl;

import com.hj.web.schedule.entity.ClassHourStatistics;
import com.hj.web.schedule.mapper.ClassHourStatisticsMapper;
import com.hj.web.schedule.service.ClassHourStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClassHourStatisticsServiceImpl implements ClassHourStatisticsService {

    @Autowired
    private ClassHourStatisticsMapper classHourStatisticsMapper;

    @Override
    public List<ClassHourStatistics> findStatistics(Long teacherId, Long courseId, LocalDateTime startDate, LocalDateTime endDate) {
        return classHourStatisticsMapper.findStatistics(teacherId, courseId, startDate, endDate);
    }

    @Override
    public ClassHourStatistics findById(Long id) {
        return classHourStatisticsMapper.findById(id);
    }

    @Override
    @Transactional
    public void updateStatistics(ClassHourStatistics statistics) {
        classHourStatisticsMapper.updateStatistics(statistics);
    }

    @Override
    @Transactional
    public void createStatistics(ClassHourStatistics statistics) {
        classHourStatisticsMapper.insertStatistics(statistics);
    }
} 