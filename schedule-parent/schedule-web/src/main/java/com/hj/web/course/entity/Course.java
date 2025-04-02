package com.hj.web.course.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data   //自动生成get和set方法
//映射数据库的表
@TableName("course")
public class Course {
//    主键自动递增
    @TableId(type = IdType.AUTO)
//    课程id
    private Long courseId;
//    课程名
    private String courseName;
//    专业名称
    private String majorName;
//    课程类型 0：春季 1：秋季
    private String courseType;
//    课程年份
    private String courseYear;
//    课程时间
    private Date courseTime;
//    课程颜色
    private String courseColor;
//    是否合并上课
    private String isMergeClasses;
//    父级id
    private Long parentCourseId;



}
