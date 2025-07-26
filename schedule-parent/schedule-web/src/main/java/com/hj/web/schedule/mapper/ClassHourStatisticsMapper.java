package com.hj.web.schedule.mapper;

import com.hj.web.schedule.entity.ClassHourStatistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ClassHourStatisticsMapper {
    List<ClassHourStatistics> findStatistics(
        @Param("teacherId") Long teacherId,
        @Param("courseId") Long courseId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    int updateStatistics(ClassHourStatistics statistics);
    
    int insertStatistics(ClassHourStatistics statistics);
    
    ClassHourStatistics findById(@Param("id") Long id);
} 