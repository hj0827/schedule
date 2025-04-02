package com.hj.web.schedule.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
//解释前端排课参数
@Data
public class ScheduleParm {
    //教室id
    private Long roomId;
    //课程id
    private Long courseId;
    //教师id
    private Long teacherId;
    //上课时间
    private LocalTime beginTime;
    //下课时间
    private LocalTime endTime;
    //开课日期
    private LocalDate startDate;
    //结课日期
    private LocalDate endDate;
    //周几上课列表
    private List<Integer> weeks;
    private Long duration;
    private List<ScheduleCourse> scheduleCourses;
    private String courseType;
}
