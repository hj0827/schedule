package com.hj.web.schedule.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@TableName("schedule_course")
public class ScheduleCourse implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long roomId;
    private Long courseId;
    private String courseType;
    private String majorName;
    private String courseYear;
    private Long teacherId;
    //日期
    private LocalDate dateTime;//上课时间
    private LocalTime beginTime;
    private Long duration;
    //下课时间
    private LocalTime endTime;

    @TableField(exist = false)
    private String teacherName;
    @TableField(exist = false)
    private String courseName;
    @TableField(exist = false)
    private String roomName;
    @TableField(exist = false)
    private String courseColor;
    @TableField(exist = false)
    private String roomAddress;


}
