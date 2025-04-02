package com.hj.web.course.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hj.utils.ResultUtils;
import com.hj.utils.ResultVo;
import com.hj.web.course.entity.Course;
import com.hj.web.course.entity.CourseParm;
import com.hj.web.course.service.CourseService;
import com.hj.web.schedule.service.ScheduleCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/course")
public class CourseController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private ScheduleCourseService scheduleCourseService;

    //    新增课程
    @PostMapping
    public ResultVo add(@RequestBody List<Course> courses) {
        if (courses == null || courses.isEmpty()) {
            return ResultUtils.error("课程列表不能为空");
        }

        // 处理第一条数据
        Course firstCourse = courses.get(0);
        System.out.println("adddata");
        System.out.println("Course Name: " + firstCourse.getCourseName());
        System.out.println("Major Name: " + firstCourse.getMajorName());
        System.out.println("Course Type: " + firstCourse.getCourseType());
        System.out.println("Course Year: " + firstCourse.getCourseYear());

        // 检查课程名称、专业名称、课程类型和学年是否已经存在
        if (courseService.isCourseExists(firstCourse.getCourseName(), firstCourse.getMajorName(), firstCourse.getCourseType(), firstCourse.getCourseYear())) {
            return ResultUtils.error("该课程名称、专业名称、课程类型和学年已存在，不允许添加");
        }

        firstCourse.setCourseTime(new Date());
        boolean saveFirst = courseService.save(firstCourse);
        if (!saveFirst) {
            return ResultUtils.error("第一条数据添加失败");
        }

        // 获取第一条数据的 course_id
        Course savedFirstCourse = courseService.getById(firstCourse.getCourseId());
        if (savedFirstCourse == null) {
            return ResultUtils.error("无法获取第一条数据的 course_id");
        }

        System.out.println("First Course ID: " + savedFirstCourse.getCourseId()); // 打印 course_id

        // 处理剩余的数据
        for (int i = 1; i < courses.size(); i++) {
            Course course = courses.get(i);
            System.out.println("adddata");
            System.out.println("Course Name: " + course.getCourseName());
            System.out.println("Major Name: " + course.getMajorName());
            System.out.println("Course Type: " + course.getCourseType());
            System.out.println("Course Year: " + course.getCourseYear());

            // 检查课程名称、专业名称、课程类型和学年是否已经存在
            if (courseService.isCourseExists(course.getCourseName(), course.getMajorName(), course.getCourseType(), course.getCourseYear())) {
                return ResultUtils.error("该课程名称、专业名称、课程类型和学年已存在，不允许添加");
            }

            course.setCourseTime(new Date());
            course.setParentCourseId(savedFirstCourse.getCourseId()); // 设置 parent_course_id
            boolean save = courseService.save(course);
            if (!save) {
                return ResultUtils.error("数据添加失败");
            }
        }

        return ResultUtils.success("新增成功！First Course ID: " + savedFirstCourse.getCourseId());
    }


    //    编辑课程
    @Transactional
    @PutMapping
    public ResultVo edit(@RequestBody List<Course> courses) {
        if (courses == null || courses.isEmpty()) {
            return ResultUtils.error("课程列表不能为空");
        }

        for (Course course : courses) {
            course.setCourseTime(new Date());

            // 更新当前课程
            boolean update = courseService.updateById(course);
            if (!update) {
                return ResultUtils.error("编辑课程 " + course.getCourseName() + " 失败");
            }

            // 如果 course_color 被修改，则需要更新相关课程的颜色
            if (course.getCourseColor() != null) {
                // 根据 course_id 查询 parent_course_id
                Course updatedCourse = courseService.getById(course.getCourseId());
                if (updatedCourse != null) {
                    System.out.println("Updated Course ID: " + updatedCourse.getCourseId());
                    System.out.println("Updated Course Color: " + updatedCourse.getCourseColor());

                    // 更新父课程的颜色（如果有）
                    if (updatedCourse.getParentCourseId() != null) {
                        Long parentCourseId = updatedCourse.getParentCourseId();
                        System.out.println("Parent Course ID to be updated: " + parentCourseId);

                        // 根据 parent_course_id 查询父课程
                        Course parentCourse = courseService.getById(parentCourseId);
                        if (parentCourse != null) {
                            System.out.println("Parent Course Name: " + parentCourse.getCourseName());
                            System.out.println("Updating Parent Course Color to: " + updatedCourse.getCourseColor());

                            // 更新父课程的相关信息
                            parentCourse.setCourseColor(updatedCourse.getCourseColor());
                            parentCourse.setCourseTime(new Date());

                            boolean parentUpdate = courseService.updateById(parentCourse);
                            if (!parentUpdate) {
                                return ResultUtils.error("更新父课程 " + parentCourse.getCourseName() + " 的颜色失败");
                            }
                        }
                    }

                    // 更新所有子课程的颜色
                    updateAllChildCoursesColor(updatedCourse.getCourseId(), updatedCourse.getCourseColor());
                }
            }
        }

        return ResultUtils.success("所有课程编辑成功！");
    }

    private void updateAllChildCoursesColor(Long parentId, String newColor) {
        // 构造查询条件，查找所有 parent_course_id 为 parentId 的子课程
        QueryWrapper<Course> query = new QueryWrapper<>();
        query.lambda().eq(Course::getParentCourseId, parentId);

        // 获取所有子课程
        List<Course> childCourses = courseService.list(query);

        for (Course childCourse : childCourses) {
            System.out.println("Child Course ID: " + childCourse.getCourseId());
            System.out.println("Updating Child Course Color to: " + newColor);

            // 更新子课程的颜色和 course_time
            childCourse.setCourseColor(newColor);
            childCourse.setCourseTime(new Date());

            boolean update = courseService.updateById(childCourse);
            if (!update) {
                throw new RuntimeException("更新子课程 " + childCourse.getCourseName() + " 的颜色失败");
            }
        }
    }

    //    删除课程
    @DeleteMapping("/{courseId}")
    public ResultVo delete(@PathVariable("courseId") Long courseId) {
        // 检查 schedule_course 表中是否有相应的数据
//        boolean hasScheduleData = scheduleCourseService.hasScheduleDataByCourseId(courseId);
//        if (hasScheduleData) {
//            // 先删除 schedule_course 表中的相关记录
//            boolean scheduleRemoved = scheduleCourseService.removeByCourseId(courseId);
//            if (!scheduleRemoved) {
//                return ResultUtils.error("删除失败，该课程已建课时，无法删除");
//            }
//        }
        try {
            // 再删除 course 表中的记录
            boolean courseRemoved = courseService.removeById(courseId);
            if (courseRemoved) {
                return ResultUtils.success("删除成功！");
            }
            return ResultUtils.error("删除课程信息失败");
        } catch (DataIntegrityViolationException e) {
            return ResultUtils.error("删除失败，该课程已建课时，无法删除。");
        }

    }

    @GetMapping("/list")
    public ResultVo getList(CourseParm courseParm) {
//       构造分页对象
        IPage<Course> page = new Page<>();
        page.setCurrent(courseParm.getCurrentPage());
        page.setSize(courseParm.getPageSize());
//        构造查询条件
        QueryWrapper<Course> query = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(courseParm.getCourseName())) {
            query.lambda().like(Course::getCourseName, courseParm.getCourseName());
        }
        if (StringUtils.isNotEmpty(courseParm.getCourseType())) {
            query.lambda().eq(Course::getCourseType, courseParm.getCourseType());

        }
//        查询
        IPage<Course> list = courseService.page(page, query);
        return ResultUtils.success("查询成功！", list);

    }
}
