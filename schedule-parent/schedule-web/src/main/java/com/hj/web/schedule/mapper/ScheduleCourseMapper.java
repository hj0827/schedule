package com.hj.web.schedule.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.hj.web.schedule.entity.ListParm;
import com.hj.web.schedule.entity.ScheduleCourse;
import com.hj.web.schedule.entity.ScheduleCourseListVo;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.toolkit.Constants;

import java.util.List;

public interface ScheduleCourseMapper extends BaseMapper<ScheduleCourse> {

    //    排课查询教室、日期是否被占用
    List<ScheduleCourseListVo> getScheduleList(@Param(Constants.WRAPPER) Wrapper<?> queryWrapper);
    //    查询排课列表
//    List<ScheduleCourse> selectCourseSchedulingList(@Param(Constants.WRAPPER) Wrapper<?> queryWrapper);
    List<ScheduleCourse> selectCourseSchedulingList(@Param("query") ListParm query);
    List<ScheduleCourse> getScheduleCourseList(@Param("courseName") String courseName, @Param("courseType") String courseType, @Param("teacherName") String teacherName);
    // 根据 course_id 查询 course_type
    String getCourseTypeByCourseId(@Param("courseId") Long courseId);
}
