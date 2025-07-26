package com.hj.web.schedule.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
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
import com.hj.web.schedule.entity.ScheduleQueryDTO;
import com.hj.web.schedule.service.ScheduleCourseService;
import com.hj.web.teacher.entity.Teacher;
import com.hj.web.teacher.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;
import java.util.HashSet;
import java.util.Set;

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
        list.forEach(course -> System.out
                .println("父亲id" + course.getParentCourseId() + ", Course Name: " + course.getCourseName()));
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

    // 保存排课
    @PostMapping("/saveSchedule")
    public ResultVo saveSchedule(@RequestBody ScheduleParm scheduleParm) {
        System.out.println("排课参数：" + scheduleParm);
        System.out.println("阶段ID：" + scheduleParm.getStageId());
        int batchSize = 100; // 根据实际情况调整批次大小
        scheduleCourseService.saveScheduleInBatches(scheduleParm, batchSize);
        return ResultUtils.success("排课成功");
    }

    // 测试方法：检查最新的排课记录
    @GetMapping("/testStageId")
    public ResultVo testStageId() {
        List<ScheduleCourse> list = scheduleCourseService.list(
            new QueryWrapper<ScheduleCourse>()
                .orderByDesc("id")
                .last("LIMIT 5")
        );
        System.out.println("最新5条排课记录：");
        for (ScheduleCourse sc : list) {
            System.out.println("ID: " + sc.getId() + ", 课时名称: " + sc.getLessonName() + ", 阶段ID: " + sc.getStageId());
        }
        return ResultUtils.success("测试完成", list);
    }

    // 测试方法：直接保存带有stage_id的记录
    @PostMapping("/testSaveWithStageId")
    public ResultVo testSaveWithStageId() {
        ScheduleCourse sc = new ScheduleCourse();
        sc.setCourseId(452L);
        sc.setTeacherId(22L);
        sc.setRoomId(1L);
        sc.setDateTime(java.time.LocalDate.of(2025, 8, 15));
        sc.setBeginTime(java.time.LocalTime.of(9, 0));
        sc.setEndTime(java.time.LocalTime.of(10, 0));
        sc.setDuration(60L);
        sc.setLessonName("MyBatis-Plus测试阶段ID");
        sc.setStageId(1);
        sc.setCourseType("测试");
        sc.setDelId("TEST" + System.currentTimeMillis());

        System.out.println("准备保存的对象: " + sc);
        System.out.println("对象的stageId: " + sc.getStageId());

        boolean result = scheduleCourseService.save(sc);
        System.out.println("保存结果: " + result);
        System.out.println("保存后的ID: " + sc.getId());

        // 立即查询验证
        ScheduleCourse saved = scheduleCourseService.getById(sc.getId());
        System.out.println("查询到的记录: " + saved);
        System.out.println("查询到的stageId: " + (saved != null ? saved.getStageId() : "null"));

        return ResultUtils.success("测试保存结果: " + result, saved);
    }

    // 简单测试：验证字段映射
    @GetMapping("/testFieldMapping")
    public ResultVo testFieldMapping() {
        // 创建一个简单的对象测试字段映射
        ScheduleCourse test = new ScheduleCourse();
        test.setStageId(999);
        System.out.println("测试对象的stageId: " + test.getStageId());

        // 查询一条现有记录并打印其stageId
        ScheduleCourse existing = scheduleCourseService.list(
            new QueryWrapper<ScheduleCourse>().last("LIMIT 1")
        ).stream().findFirst().orElse(null);

        if (existing != null) {
            System.out.println("现有记录ID: " + existing.getId());
            System.out.println("现有记录stageId: " + existing.getStageId());
        }

        return ResultUtils.success("字段映射测试完成");
    }

    // 排课列表查询
    @GetMapping("/getScheduleList")
    public ResultVo getScheduleList(ListParm listParm) {
        System.out.println("参数在这在这在这在这在这在这在这在这在这：" + listParm);
        try {
            // 如果没有分页参数，使用非分页查询获取全部数据
            if (listParm.getPageSize() == null || listParm.getCurrentPage() == null) {
                List<ScheduleCourse> list = scheduleCourseService.selectCourseSchedulingList(listParm);
                System.out.println("查询结果（非分页）：" + list.size() + " 条记录");
                return ResultUtils.success("查询成功", list);
            } else {
                // 使用分页查询
                IPage<ScheduleCourse> page = scheduleCourseService.selectCourseSchedulingListWithPage(listParm);
                System.out.println("查询结果（分页）：" + page);
                return ResultUtils.success("查询成功", page);
            }
        } catch (Exception e) {
            e.printStackTrace(); // 打印完整的堆栈跟踪信息
            return ResultUtils.error("查询失败", e.getMessage());
        }
    }

    // 日历专用查询接口 - 获取全部数据（非分页）
    @GetMapping("/getScheduleListForCalendar")
    public ResultVo getScheduleListForCalendar(ListParm listParm) {
        System.out.println("日历查询参数：" + listParm);
        try {
            // 直接使用非分页查询获取全部数据
            List<ScheduleCourse> list = scheduleCourseService.selectCourseSchedulingList(listParm);
            System.out.println("日历查询结果：" + list.size() + " 条记录");
            return ResultUtils.success("查询成功", list);
        } catch (Exception e) {
            e.printStackTrace(); // 打印完整的堆栈跟踪信息
            return ResultUtils.error("查询失败", e.getMessage());
        }
    }

    // 查询教室列表
    @GetMapping("/getClassRoomListApi")
    public ResultVo getClassRoomListApi(ListParm listParm) {
        System.out.println("教室列表：" + listParm);
        try {
            List<ScheduleCourse> list = scheduleCourseService.selectClassRoomlingList(listParm);
            System.out.println("查询结果：" + list);
            return ResultUtils.success("查询成功", list);
        } catch (Exception e) {
            e.printStackTrace(); // 打印完整的堆栈跟踪信息
            return ResultUtils.error("查询失败", e.getMessage());
        }
    }

    // 查询教师列表
    @GetMapping("/getTeaListApi")
    public ResultVo getTeaListApi(ListParm listParm) {
        System.out.println("教师列表：" + listParm);
        try {
            List<ScheduleCourse> list = scheduleCourseService.selectTeaList(listParm);
            System.out.println("查询结果：" + list);
            return ResultUtils.success("查询成功", list);
        } catch (Exception e) {
            e.printStackTrace(); // 打印完整的堆栈跟踪信息
            return ResultUtils.error("查询失败", e.getMessage());
        }
    }

    // 查询考期列表
    @GetMapping("/getCourseTypeListApi")
    public ResultVo getCourseTypeListApi(ListParm listParm) {
        System.out.println("考期列表：" + listParm);
        try {
            List<ScheduleCourse> list = scheduleCourseService.selectCorseTypeList(listParm);
            System.out.println("查询结果：" + list);
            return ResultUtils.success("查询成功", list);
        } catch (Exception e) {
            e.printStackTrace(); // 打印完整的堆栈跟踪信息
            return ResultUtils.error("查询失败", e.getMessage());
        }
    }

    @GetMapping("/getMergedCourseTerms")
    public ResultVo getMergedCourseTerms(@RequestParam("courseId") Long courseId) {
        System.out.println("获取合并课程的所有考期, courseId: " + courseId);
        try {
            // 查询当前课程
            Course course = courseService.getById(courseId);
            if (course == null) {
                return ResultUtils.error("课程不存在");
            }

            List<Map<String, Object>> relatedTerms = new ArrayList<>();

            // 添加当前课程的考期信息
            Map<String, Object> currentTermInfo = new HashMap<>();
            currentTermInfo.put("courseId", course.getCourseId());
            currentTermInfo.put("courseName", course.getCourseName());
            currentTermInfo.put("courseType", course.getCourseType() + "(当前)");
            relatedTerms.add(currentTermInfo);

            // 判断是否为合并课程，获取相关考期
            if ("1".equals(course.getIsMergeClasses())) {
                // 如果是父级课程，获取所有子课程的考期
                if (course.getParentCourseId() == null) {
                    // 查询所有以该课程为父级的子课程
                    QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("parent_course_id", courseId);
                    List<Course> childCourses = courseService.list(queryWrapper);

                    System.out.println("查询到的子课程数量: " + childCourses.size());

                    // 添加每个子课程的考期信息
                    for (Course childCourse : childCourses) {
                        Map<String, Object> termInfo = new HashMap<>();
                        termInfo.put("courseId", childCourse.getCourseId());
                        termInfo.put("courseName", childCourse.getCourseName());
                        termInfo.put("courseType", childCourse.getCourseType());
                        relatedTerms.add(termInfo);
                    }
                } else {
                    // 如果是子课程，先获取父课程
                    Course parentCourse = courseService.getById(course.getParentCourseId());
                    if (parentCourse != null) {
                        // 添加父课程信息
                        Map<String, Object> parentTermInfo = new HashMap<>();
                        parentTermInfo.put("courseId", parentCourse.getCourseId());
                        parentTermInfo.put("courseName", parentCourse.getCourseName());
                        parentTermInfo.put("courseType", parentCourse.getCourseType() + "(父课程)");
                        relatedTerms.add(parentTermInfo);

                        // 查询该父课程的所有子课程
                        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
                        queryWrapper.eq("parent_course_id", parentCourse.getCourseId());
                        List<Course> siblingCourses = courseService.list(queryWrapper);

                        System.out.println("查询到的兄弟课程数量: " + siblingCourses.size());

                        // 添加每个子课程的考期信息
                        for (Course siblingCourse : siblingCourses) {
                            // 排除当前课程自己
                            if (!siblingCourse.getCourseId().equals(courseId)) {
                                Map<String, Object> termInfo = new HashMap<>();
                                termInfo.put("courseId", siblingCourse.getCourseId());
                                termInfo.put("courseName", siblingCourse.getCourseName());
                                termInfo.put("courseType", siblingCourse.getCourseType());
                                relatedTerms.add(termInfo);
                            }
                        }
                    }
                }
            } else {
                // 不是合并课程，尝试查询是否有父课程
                if (course.getParentCourseId() != null) {
                    Course parentCourse = courseService.getById(course.getParentCourseId());
                    if (parentCourse != null) {
                        Map<String, Object> parentTermInfo = new HashMap<>();
                        parentTermInfo.put("courseId", parentCourse.getCourseId());
                        parentTermInfo.put("courseName", parentCourse.getCourseName());
                        parentTermInfo.put("courseType", parentCourse.getCourseType() + "(父课程)");
                        relatedTerms.add(parentTermInfo);

                        // 查询同父级的其他课程
                        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
                        queryWrapper.eq("parent_course_id", parentCourse.getCourseId());
                        queryWrapper.ne("course_id", courseId); // 排除当前课程
                        List<Course> siblingCourses = courseService.list(queryWrapper);

                        for (Course siblingCourse : siblingCourses) {
                            Map<String, Object> termInfo = new HashMap<>();
                            termInfo.put("courseId", siblingCourse.getCourseId());
                            termInfo.put("courseName", siblingCourse.getCourseName());
                            termInfo.put("courseType", siblingCourse.getCourseType());
                            relatedTerms.add(termInfo);
                        }
                    }
                }
            }

            // 对数据进行去重处理（根据courseType字段）
            Map<String, Map<String, Object>> uniqueTerms = new HashMap<>();
            for (Map<String, Object> term : relatedTerms) {
                String courseType = (String) term.get("courseType");
                if (!uniqueTerms.containsKey(courseType)) {
                    uniqueTerms.put(courseType, term);
                }
            }

            // 将去重后的数据转换为列表
            List<Map<String, Object>> uniqueTermsList = new ArrayList<>(uniqueTerms.values());

            System.out.println("返回的考期数据: " + uniqueTermsList);

            return ResultUtils.success("查询成功", uniqueTermsList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error("查询失败", e.getMessage());
        }
    }

    // 新增排课
    @PostMapping("/addSchedule")
    public ResultVo addSchedule(@RequestBody ScheduleCourse scheduleCourse) {
        try {
            boolean save = scheduleCourseService.save(scheduleCourse);
            if (save) {
                // 添加课时后重新排序
                scheduleCourseService.reorderLessonNamesAfterAdd(scheduleCourse.getCourseId(),
                        scheduleCourse.getCourseType());
                return ResultUtils.success("排课成功！");
            }
            return ResultUtils.error("排课失败");
        } catch (Exception e) {
            return ResultUtils.error("排课失败", e.getMessage());
        }
    }

    // 编辑排课
    @PutMapping("/updateScheduleById")
    public ResultVo updateScheduleById(@RequestBody ScheduleCourse scheduleCourse) {
        System.out.println("参数：" + scheduleCourse);
        boolean update = scheduleCourseService.updateById(scheduleCourse);
        if (update) {
            return ResultUtils.success("修改成功！");
        }
        return ResultUtils.error("修改失败");
    }

    // 编辑排课
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

    // 获取已排课程
    @PostMapping("/getScheduleInfo")
    public ResultVo getScheduleCourseList(@RequestBody ScheduleQueryDTO queryDTO) {
        System.out.println("获取已经排好的课程，查询参数：" + queryDTO);
        // 如果有分页参数，使用分页查询
        if (queryDTO.getCurrentPage() != null && queryDTO.getPageSize() != null) {
            IPage<ScheduleCourse> page = scheduleCourseService.getScheduleCourseListWithPage(queryDTO);
            return ResultUtils.success("查询成功", page);
        } else {
            // 否则使用原来的查询方式
            List<ScheduleCourse> list = scheduleCourseService.getScheduleCourseList(queryDTO);
            return ResultUtils.success("查询成功", list);
        }
    }

    // 排课列表删除已经排好的课程，只删除相应课程
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

    // 根据课时表的id查询课程表，删除主课程&子课程
    @DeleteMapping("/deleteCalendarListById/{delId}")
    public ResultVo deleteCalendarListById(@PathVariable("delId") Long delId) {
        System.out.println("删除课时id：" + delId);
        try {
            scheduleCourseService.deleteCalendarListById(delId);
            return ResultUtils.success("删除成功！");
        } catch (Exception e) {
            return ResultUtils.error("删除失败", e.getMessage());
        }
    }

    // 批量删除课程排程
    @DeleteMapping("/batchDeleteCalendarListByIds")
    public ResultVo batchDeleteCalendarListByIds(@RequestBody List<String> delIds) {
        System.out.println("批量删除课时ids：" + delIds);
        try {
            if (delIds == null || delIds.isEmpty()) {
                return ResultUtils.error("删除列表不能为空");
            }

            int successCount = 0;
            int failCount = 0;
            StringBuilder errorMessages = new StringBuilder();

            for (String delIdStr : delIds) {
                try {
                    Long delId = Long.parseLong(delIdStr);
                    scheduleCourseService.deleteCalendarListById(delId);
                    successCount++;
                } catch (NumberFormatException e) {
                    failCount++;
                    errorMessages.append("无效的ID格式: ").append(delIdStr).append("; ");
                } catch (Exception e) {
                    failCount++;
                    errorMessages.append("删除ID ").append(delIdStr).append(" 失败: ").append(e.getMessage()).append("; ");
                }
            }

            if (failCount == 0) {
                return ResultUtils.success("批量删除成功！共删除 " + successCount + " 条记录");
            } else {
                return ResultUtils.error("批量删除完成，成功 " + successCount + " 条，失败 " + failCount + " 条。错误信息：" + errorMessages.toString());
            }
        } catch (Exception e) {
            return ResultUtils.error("批量删除失败", e.getMessage());
        }
    }

    // 获取考期已排时间
    @GetMapping("/getAllTermSchedules")
    public ResultVo getAllTermSchedules(@RequestParam("courseId") Long courseId) {
        System.out.println("获取所有考期已排时间, courseId: " + courseId);
        try {
            // 获取课程信息
            Course course = courseService.getById(courseId);
            if (course == null) {
                return ResultUtils.error("课程不存在");
            }

            // 创建一个集合存储所有需要查询的课程ID
            Set<Long> relatedCourseIds = new HashSet<>();
            relatedCourseIds.add(courseId); // 添加当前课程ID

            // 存储所有课程ID与考期名称的映射
            Map<Long, String> courseTypeMap = new HashMap<>();
            courseTypeMap.put(courseId, course.getCourseType() + "(当前)");

            // 存储所有需要查找的考期
            Set<String> relatedCourseTypes = new HashSet<>();
            if (course.getCourseType() != null && !course.getCourseType().isEmpty()) {
                relatedCourseTypes.add(course.getCourseType());
            }

            // 处理合并课程关系
            processCourseRelationships(course, relatedCourseIds, courseTypeMap, relatedCourseTypes);

            // 查找所有相关考期的课程
            for (String courseType : relatedCourseTypes) {
                findCoursesWithSameTerm(courseType, course.getCourseId(), relatedCourseIds, courseTypeMap);
            }

            System.out.println("关联的所有课程ID: " + relatedCourseIds);
            System.out.println("考期映射: " + courseTypeMap);

            // 查询所有关联课程的排课信息
            List<ScheduleCourse> allSchedules = new ArrayList<>();
            for (Long id : relatedCourseIds) {
                ListParm parm = new ListParm();
                parm.setCourseId(id);
                List<ScheduleCourse> schedules = scheduleCourseService.selectCourseSchedulingList(parm);

                // 设置考期名称
                String courseType = courseTypeMap.get(id);
                for (ScheduleCourse schedule : schedules) {
                    schedule.setCourseType(courseType);
                }

                allSchedules.addAll(schedules);
            }

            // 对数据进行去重处理（根据dateTime, beginTime, endTime组合）
            Map<String, ScheduleCourse> uniqueSchedules = new HashMap<>();
            for (ScheduleCourse schedule : allSchedules) {
                String key = schedule.getDateTime() + "-" + schedule.getBeginTime() + "-" + schedule.getEndTime();
                if (!uniqueSchedules.containsKey(key)) {
                    uniqueSchedules.put(key, schedule);
                }
            }

            // 将去重后的数据转换为列表
            List<ScheduleCourse> uniqueSchedulesList = new ArrayList<>(uniqueSchedules.values());

            // 按日期排序
            uniqueSchedulesList.sort((a, b) -> {
                int dateCompare = a.getDateTime().compareTo(b.getDateTime());
                if (dateCompare != 0)
                    return dateCompare;

                int beginTimeCompare = a.getBeginTime().compareTo(b.getBeginTime());
                if (beginTimeCompare != 0)
                    return beginTimeCompare;

                return a.getEndTime().compareTo(b.getEndTime());
            });

            return ResultUtils.success("查询成功", uniqueSchedulesList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error("查询失败", e.getMessage());
        }
    }

    /**
     * 处理课程关系，查找所有关联课程
     * 
     * @param course             当前课程
     * @param relatedCourseIds   存储关联课程ID的集合
     * @param courseTypeMap      课程ID与考期名称的映射
     * @param relatedCourseTypes 存储相关考期的集合
     */
    private void processCourseRelationships(Course course, Set<Long> relatedCourseIds, Map<Long, String> courseTypeMap,
            Set<String> relatedCourseTypes) {
        // 处理合并课程关系
        if ("1".equals(course.getIsMergeClasses())) {
            // 如果是父级课程，获取所有子课程
            if (course.getParentCourseId() == null) {
                QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("parent_course_id", course.getCourseId());
                List<Course> childCourses = courseService.list(queryWrapper);

                System.out.println("父课程 " + course.getCourseName() + " 的子课程数量: " + childCourses.size());

                // 添加所有子课程
                for (Course childCourse : childCourses) {
                    relatedCourseIds.add(childCourse.getCourseId());
                    courseTypeMap.put(childCourse.getCourseId(), childCourse.getCourseType());

                    // 添加子课程的考期
                    if (childCourse.getCourseType() != null && !childCourse.getCourseType().isEmpty()) {
                        relatedCourseTypes.add(childCourse.getCourseType());
                    }
                }
            } else {
                // 如果是子课程，先获取父课程
                Course parentCourse = courseService.getById(course.getParentCourseId());
                if (parentCourse != null) {
                    // 添加父课程
                    relatedCourseIds.add(parentCourse.getCourseId());
                    courseTypeMap.put(parentCourse.getCourseId(), parentCourse.getCourseType() + "(父课程)");

                    // 获取所有兄弟课程
                    QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("parent_course_id", parentCourse.getCourseId());
                    List<Course> siblingCourses = courseService.list(queryWrapper);

                    System.out.println("子课程 " + course.getCourseName() + " 的兄弟课程数量: " + siblingCourses.size());

                    // 添加所有兄弟课程
                    for (Course siblingCourse : siblingCourses) {
                        if (!course.getCourseId().equals(siblingCourse.getCourseId())) {
                            relatedCourseIds.add(siblingCourse.getCourseId());
                            courseTypeMap.put(siblingCourse.getCourseId(), siblingCourse.getCourseType());

                            // 添加兄弟课程的考期
                            if (siblingCourse.getCourseType() != null && !siblingCourse.getCourseType().isEmpty()) {
                                relatedCourseTypes.add(siblingCourse.getCourseType());
                            }
                        }
                    }
                }
            }
        } else {
            // 不是合并课程，检查是否有父课程关系
            if (course.getParentCourseId() != null) {
                Course parentCourse = courseService.getById(course.getParentCourseId());
                if (parentCourse != null) {
                    // 添加父课程
                    relatedCourseIds.add(parentCourse.getCourseId());
                    courseTypeMap.put(parentCourse.getCourseId(), parentCourse.getCourseType() + "(父课程)");

                    // 添加父课程的考期
                    if (parentCourse.getCourseType() != null && !parentCourse.getCourseType().isEmpty()) {
                        relatedCourseTypes.add(parentCourse.getCourseType());
                    }

                    // 获取所有兄弟课程
                    QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("parent_course_id", parentCourse.getCourseId());
                    List<Course> siblingCourses = courseService.list(queryWrapper);

                    // 添加所有兄弟课程
                    for (Course siblingCourse : siblingCourses) {
                        if (!course.getCourseId().equals(siblingCourse.getCourseId())) {
                            relatedCourseIds.add(siblingCourse.getCourseId());
                            courseTypeMap.put(siblingCourse.getCourseId(), siblingCourse.getCourseType());

                            // 添加兄弟课程的考期
                            if (siblingCourse.getCourseType() != null && !siblingCourse.getCourseType().isEmpty()) {
                                relatedCourseTypes.add(siblingCourse.getCourseType());
                            }
                        }
                    }
                }
            }

            // 查找以当前课程为父课程的课程
            QueryWrapper<Course> childQueryWrapper = new QueryWrapper<>();
            childQueryWrapper.eq("parent_course_id", course.getCourseId());
            List<Course> childCourses = courseService.list(childQueryWrapper);

            // 添加所有子课程
            for (Course childCourse : childCourses) {
                relatedCourseIds.add(childCourse.getCourseId());
                courseTypeMap.put(childCourse.getCourseId(), childCourse.getCourseType() + "(子课程)");

                // 添加子课程的考期
                if (childCourse.getCourseType() != null && !childCourse.getCourseType().isEmpty()) {
                    relatedCourseTypes.add(childCourse.getCourseType());
                }
            }
        }
    }

    /**
     * 查找共享相同考期的课程
     * 
     * @param courseType       考期
     * @param excludeCourseId  排除的课程ID
     * @param relatedCourseIds 存储关联课程ID的集合
     * @param courseTypeMap    课程ID与考期名称的映射
     */
    private void findCoursesWithSameTerm(String courseType, Long excludeCourseId, Set<Long> relatedCourseIds,
            Map<Long, String> courseTypeMap) {
        if (courseType == null || courseType.isEmpty()) {
            return;
        }

        System.out.println("查找共享考期 " + courseType + " 的其他课程");

        QueryWrapper<Course> courseTypeQuery = new QueryWrapper<>();
        courseTypeQuery.eq("course_type", courseType);
        courseTypeQuery.ne("course_id", excludeCourseId); // 排除指定课程
        List<Course> sameTermCourses = courseService.list(courseTypeQuery);

        System.out.println("找到共享考期 " + courseType + " 的课程数量: " + sameTermCourses.size());

        // 添加共享相同考期的课程
        for (Course sameTermCourse : sameTermCourses) {
            relatedCourseIds.add(sameTermCourse.getCourseId());
            courseTypeMap.put(sameTermCourse.getCourseId(), sameTermCourse.getCourseType() + "(同考期)");
        }
    }

}
