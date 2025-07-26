package com.hj.web.schedule.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hj.web.schedule.entity.ListParm;
import com.hj.web.schedule.entity.ScheduleCourse;
import com.hj.web.schedule.entity.ScheduleParm;
import com.hj.web.schedule.entity.ScheduleQueryDTO;

import java.util.List;

public interface ScheduleCourseService extends IService<ScheduleCourse> {
    // void saveSchedule(ScheduleParm scheduleParm);
    void saveScheduleInBatches(ScheduleParm scheduleParm, int batchSize);

    // 查询排课列表
    List<ScheduleCourse> selectCourseSchedulingList(ListParm listParm);

    // 查询排课列表（分页）
    IPage<ScheduleCourse> selectCourseSchedulingListWithPage(ListParm listParm);

    List<ScheduleCourse> selectClassRoomlingList(ListParm listParm);

    List<ScheduleCourse> selectTeaList(ListParm listParm);

    // 删除相应的课程
    boolean removeByCourseId(Long courseId);

    // 判断是否删除的课程，是否有排课
    boolean hasScheduleDataByCourseId(Long courseId);

    // 获取已排好课程的信息
    List<ScheduleCourse> getScheduleCourseList(ScheduleQueryDTO queryDTO);

    // 获取已排好课程的信息（分页）
    IPage<ScheduleCourse> getScheduleCourseListWithPage(ScheduleQueryDTO queryDTO);

    // 更新排课和课程信息
    void updateScheduleAndCourse(ScheduleCourse scheduleCourse);

    void deleteCalendarListById(Long delId);

    List<ScheduleCourse> selectCorseTypeList(ListParm listParm);

    // 添加课时后重新排序
    void reorderLessonNamesAfterAdd(Long courseId, String courseType);
}
