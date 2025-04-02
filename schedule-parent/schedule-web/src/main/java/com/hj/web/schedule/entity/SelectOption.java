package com.hj.web.schedule.entity;

import lombok.Data;

@Data
public class SelectOption {
    private Long value;
    private String label;
//    判断是否合并上课
    private Long isMergeClasses;
//    父级id
    private Long parentCourseId;
    private String courseType;
}
