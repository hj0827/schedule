package com.hj.web.schedule.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ClassHourStatistics {
    private Long id;
    private Long teacherId;
    private String teacherName;
    private Long courseId;
    private String courseName;
    private Integer totalHours;
    private Integer completedHours;
    private Integer remainingHours;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer courseCount;
} 