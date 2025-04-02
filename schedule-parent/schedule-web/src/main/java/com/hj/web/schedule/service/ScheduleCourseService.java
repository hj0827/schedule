package com.hj.web.schedule.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hj.web.schedule.entity.ListParm;
import com.hj.web.schedule.entity.ScheduleCourse;
import com.hj.web.schedule.entity.ScheduleParm;

import java.util.List;

public interface ScheduleCourseService extends IService<ScheduleCourse> {
//    void saveSchedule(ScheduleParm scheduleParm);
    void saveScheduleInBatches(ScheduleParm scheduleParm, int batchSize);

    //    查询排课列表
    List<ScheduleCourse> selectCourseSchedulingList(ListParm listParm);

    //    删除相应的课程
    boolean removeByCourseId(Long courseId);

    //    判断是否删除的课程，是否有排课
    boolean hasScheduleDataByCourseId(Long courseId);

    // 获取已排好课程的信息
//    List<ScheduleCourse> getScheduleCourseList();
    List<ScheduleCourse> getScheduleCourseList(String courseName, String courseType,String teacherName);
    //    更新排课和课程信息
    void updateScheduleAndCourse(ScheduleCourse scheduleCourse);


    void deleteCalendarListById(Long id);
}
