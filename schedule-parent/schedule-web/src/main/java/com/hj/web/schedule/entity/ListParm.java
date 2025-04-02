package com.hj.web.schedule.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class ListParm implements Serializable {
    private List<Long> roomIdList;
    private Long roomId;
    private Long courseId;
    private Long teacherId;
//    课程id列表
    private List<Long> courseIdList;
//    老师id列表
    private List<Long> teacherIdList;
//    开始日期
    private String startDate;
//    结束日期
    private String endDate;
//    上课时间
    private String beginTime;
//    下课时间
    private String endTime;
    private String courseName;
    private String courseType;
    private Integer currentPage;
    private Integer pageSize;
}
