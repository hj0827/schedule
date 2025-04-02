package com.hj.web.course.entity;

import lombok.Data;

@Data
public class CourseParm {
//    当前页码
    private Long currentPage;
//    记录数
    private Long pageSize;
    private String courseName;
    private String courseType;

}
