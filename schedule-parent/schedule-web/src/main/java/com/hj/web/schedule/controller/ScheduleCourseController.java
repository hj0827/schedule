package com.hj.web.schedule.controller;

import com.hj.utils.ResultUtils;
import com.hj.utils.ResultVo;
import com.hj.web.classroom.entity.ClassRoom;
import com.hj.web.classroom.service.ClassRoomService;
import com.hj.web.course.entity.Course;
import com.hj.web.course.service.CourseService;
import com.hj.web.schedule.entity.ListParm;
import com.hj.web.schedule.entity.ScheduleCourse;
import com.hj.web.schedule.entity.ScheduleParm;
import com.hj.web.schedule.entity.SelectOption;
import com.hj.web.schedule.service.ScheduleCourseService;
import com.hj.web.teacher.entity.Teacher;
import com.hj.web.teacher.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleCourseController {
    @Autowired
    private ScheduleCourseService scheduleCourseService;
    @Autowired
    private ClassRoomService classRoomService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private TeacherService teacherService;

    // 教室列表
    @GetMapping("/getRoomList")
    public ResultVo getRoomList() {
        // 查询list
        List<ClassRoom> list = classRoomService.list();
        // 组装前端需要的数据格式
        List<SelectOption> selectOptions = new ArrayList<>();
        Optional.ofNullable(list).orElse(new ArrayList<>())
                .stream()
                .forEach(item -> {
                    SelectOption option = new SelectOption();
                    option.setValue(item.getRoomId()); // 直接设置 Long 类型
                    option.setLabel(item.getRoomName());
                    selectOptions.add(option);
                });
        return ResultUtils.success("查询成功", selectOptions);
    }

    // 教师
    @GetMapping("/getTeacher")
    public ResultVo getTeacher() {
        // 查询list
        List<Teacher> list = teacherService.list();
        // 组装前端需要的数据格式
        List<SelectOption> selectOptions = new ArrayList<>();
        Optional.ofNullable(list).orElse(new ArrayList<>())
                .stream()
                .forEach(item -> {
                    SelectOption option = new SelectOption();
                    option.setValue(item.getTeacherId()); // 直接设置 Long 类型
                    option.setLabel(item.getTeacherName());
                    selectOptions.add(option);
                });
        return ResultUtils.success("查询成功", selectOptions);
    }

    // 课程
    @GetMapping("/getCourseList")
    public ResultVo getCourseList() {
        // 查询list
        List<Course> list = courseService.list();

        // 打印查询到的数据
        list.forEach(course -> System.out.println("父亲id" + course.getParentCourseId() + ", Course Name: " + course.getCourseName()));
        // 组装前端需要的数据格式
        List<SelectOption> selectOptions = new ArrayList<>();
        Optional.ofNullable(list).orElse(new ArrayList<>())
                .stream()
                .forEach(item -> {
                    SelectOption option = new SelectOption();
                    option.setValue(item.getCourseId());
                    option.setLabel(item.getCourseName());
                    option.setParentCourseId(item.getParentCourseId());
                    option.setIsMergeClasses(Long.valueOf(item.getIsMergeClasses()));
                    option.setCourseType(item.getCourseType());
                    selectOptions.add(option);
                });
        return ResultUtils.success("课程查询成功", selectOptions);
    }

    //    保存排课
    @PostMapping("/saveSchedule")
    public ResultVo saveSchedule(@RequestBody ScheduleParm scheduleParm) {
        System.out.println("排课参数：" + scheduleParm);
        int batchSize = 100; // 根据实际情况调整批次大小
        scheduleCourseService.saveScheduleInBatches(scheduleParm, batchSize);
        return ResultUtils.success("排课成功");
    }


    //    排课列表查询
    @GetMapping("/getScheduleList")
    public ResultVo getScheduleList(ListParm listParm) {
        System.out.println("参数在这在这在这在这在这在这在这在这在这：" + listParm);
        try {
            List<ScheduleCourse> list = scheduleCourseService.selectCourseSchedulingList(listParm);
            System.out.println("查询结果：" + list);
            return ResultUtils.success("查询成功", list);
        } catch (Exception e) {
            e.printStackTrace(); // 打印完整的堆栈跟踪信息
            return ResultUtils.error("查询失败", e.getMessage());
        }
    }


    //    新增排课
    @PostMapping("/addSchedule")
    public ResultVo addSchedule(@RequestBody ScheduleCourse scheduleCourse) {
        boolean save = scheduleCourseService.save(scheduleCourse);
        if (save) {
            return ResultUtils.success("排课成功！");
        }
        return ResultUtils.error("排课失败");
    }

    //    编辑排课
    @PutMapping("/updateScheduleById")
    public ResultVo updateScheduleById(@RequestBody ScheduleCourse scheduleCourse) {
        System.out.println("参数：" + scheduleCourse);
        boolean update = scheduleCourseService.updateById(scheduleCourse);
        if (update) {
            return ResultUtils.success("修改成功！");
        }
        return ResultUtils.error("修改失败");
    }

    //    编辑排课
    @PutMapping("/updateScheduleListById")
    public ResultVo updateScheduleListById(@RequestBody ScheduleCourse scheduleCourse) {
        System.out.println("参数:" + scheduleCourse);
        scheduleCourseService.updateScheduleAndCourse(scheduleCourse);
        return ResultUtils.success("修改成功！");
    }

    // 根据 courseId 删除课程
    @DeleteMapping("/{courseId}")
    public ResultVo deleteByCourseId(@PathVariable("courseId") Long courseId) {
        // 先删除 schedule_course 表中的相关记录
        boolean scheduleRemoved = scheduleCourseService.removeByCourseId(courseId);
        if (!scheduleRemoved) {
            return ResultUtils.error("删除课程关联的排课信息失败");
        }

        return ResultUtils.success("删除成功！");
    }

    //    获取已排课程
    @GetMapping("/getScheduleInfo")
    public ResultVo getScheduleCourseList(@RequestParam(required = false) String courseName,
                                          @RequestParam(required = false) String courseType,
                                          @RequestParam(required = false) String teacherName
    ) {
        System.out.println("获取已经排好的课程" + courseName + courseType+teacherName);
        List<ScheduleCourse> list = scheduleCourseService.getScheduleCourseList(courseName, courseType,teacherName);
        return ResultUtils.success("查询成功", list);
    }


    //    排课列表删除已经排好的课程，只删除相应课程
    @DeleteMapping("/deleteCalendarById/{id}")
    public ResultVo deleteCalendarById(@PathVariable("id") Long id) {
        // 先删除 schedule_course 表中的相关记录
        System.out.println("删除课程：" + id);
        boolean scheduleRemoved = scheduleCourseService.removeById(id);
        if (!scheduleRemoved) {
            return ResultUtils.error("删除课程关联的排课信息失败");
        }

        return ResultUtils.success("删除成功！");
    }

//    根据课时表的id查询课程表，删除主课程&子课程
@DeleteMapping("/deleteCalendarListById/{id}")
public ResultVo deleteCalendarListById(@PathVariable("id") Long id) {
    System.out.println("删除课时id：" + id);
    try {
        scheduleCourseService.deleteCalendarListById(id);
        return ResultUtils.success("删除成功！");
    } catch (Exception e) {
        return ResultUtils.error("删除失败", e.getMessage());
    }
}


}
   