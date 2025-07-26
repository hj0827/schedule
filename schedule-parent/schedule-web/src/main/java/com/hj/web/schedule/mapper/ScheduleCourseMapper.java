package com.hj.web.schedule.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.hj.web.schedule.entity.ListParm;
import com.hj.web.schedule.entity.ScheduleCourse;
import com.hj.web.schedule.entity.ScheduleCourseListVo;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.toolkit.Constants;

import java.util.List;

public interface ScheduleCourseMapper extends BaseMapper<ScheduleCourse> {
    List<ScheduleCourseListVo> getScheduleList(@Param("ew") QueryWrapper<?> queryWrapper);
    //    排课查询教室、日期是否被占用
    List<ScheduleCourseListVo> getScheduleList(@Param(Constants.WRAPPER) Wrapper<?> queryWrapper);
    //    查询排课列表
    List<ScheduleCourse> selectCourseSchedulingList(@Param("query") ListParm query);
    //    查询排课列表（分页）
    IPage<ScheduleCourse> selectCourseSchedulingListWithPage(IPage<ScheduleCourse> page, @Param("query") ListParm query);
    List<ScheduleCourse> selectClassRoomlingList(@Param("query") ListParm query);
    List<ScheduleCourse> selectTeaList(@Param("query") ListParm query);

    List<ScheduleCourse> getScheduleCourseList(@Param(Constants.WRAPPER) Wrapper<?> queryWrapper);

    // 分页查询已排课程信息
    IPage<ScheduleCourse> getScheduleCourseListWithPage(IPage<ScheduleCourse> page, @Param(Constants.WRAPPER) Wrapper<?> queryWrapper);
    // 根据 course_id 查询 course_type
    String getCourseTypeByCourseId(@Param("courseId") Long courseId);
    // 根据 delId 删除记录
    void deleteByDelId(@Param("delId") Long delId);
}
