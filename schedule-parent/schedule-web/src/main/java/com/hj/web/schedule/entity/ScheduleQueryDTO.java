package com.hj.web.schedule.entity;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleQueryDTO {
    private String courseName;
    private String courseType;
    private String teacherName;
    private String startDate;
    private String endDate;
    private String teacherId;
    private String courseId;
    private String lessonName;
    private Integer currentPage;
    private Integer pageSize;
}