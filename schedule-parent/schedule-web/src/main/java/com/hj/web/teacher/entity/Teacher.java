package com.hj.web.teacher.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
//映射表
@TableName("teacher")
public class Teacher {
    @TableId(type = IdType.AUTO)
    private Long teacherId;
    private String teacherName;
//    教师编号
    private String teacherNum;
//    教师描述
    private String teacherDesc;

}
