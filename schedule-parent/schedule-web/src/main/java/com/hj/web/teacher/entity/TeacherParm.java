package com.hj.web.teacher.entity;

import lombok.Data;

//分页
@Data
public class TeacherParm {
    private Long currentPage;
    private Long pageSize;
    private String teacherName;
}
