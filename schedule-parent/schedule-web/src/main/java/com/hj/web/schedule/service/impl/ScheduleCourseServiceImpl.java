package com.hj.web.schedule.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hj.config.uuid.UUIDUtils;
import com.hj.exception_advice.BusinessException;
import com.hj.exception_advice.BusinessExceptionEnum;
import com.hj.web.course.entity.Course;
import com.hj.web.course.mapper.CourseMapper;
import com.hj.web.schedule.entity.ListParm;
import com.hj.web.schedule.entity.ScheduleCourse;
import com.hj.web.schedule.entity.ScheduleCourseListVo;
import com.hj.web.schedule.entity.ScheduleParm;
import com.hj.web.schedule.entity.ScheduleQueryDTO;
import com.hj.web.schedule.mapper.ScheduleCourseMapper;
import com.hj.web.schedule.service.ScheduleCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class ScheduleCourseServiceImpl extends ServiceImpl<ScheduleCourseMapper, ScheduleCourse>
        implements ScheduleCourseService {
    private final CourseMapper courseMapper;
    // 初始化日志记录器
    private static final Logger log = LoggerFactory.getLogger(ScheduleCourseServiceImpl.class);

    @Autowired
    private ScheduleCourseMapper baseMapper;

    @Autowired
    private ScheduleCourseMapper scheduleCourseMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public ScheduleCourseServiceImpl(CourseMapper courseMapper) {
        this.courseMapper = courseMapper;
    }

    @Override
    @Transactional
    public void saveScheduleInBatches(ScheduleParm scheduleParm, int initialBatchSize) {
        // 获取星期
        List<Integer> weekList = scheduleParm.getWeeks();
        LocalDate startDate = scheduleParm.getStartDate();
        LocalDate endDate = scheduleParm.getEndDate();
        List<LocalDate> dateList = new ArrayList<>();
        while (startDate.compareTo(endDate) <= 0) {
            // 获取所有的时间
            if (weekList.contains(startDate.getDayOfWeek().getValue())) {
                dateList.add(startDate);
            }
            startDate = startDate.plusDays(1);
        }

        // 查询教室时间是否冲突
        List<ScheduleCourseListVo> roomList = this.baseMapper.getScheduleList(Wrappers.query()
                .eq("cs.room_id", scheduleParm.getRoomId())
                .in("cs.date_time", dateList)
                .orderByAsc("cs.date_time,cs.begin_time"));

        // 处理教室占用信息
        Set<String> roomErrorSet = new HashSet<>();
        for (ScheduleCourseListVo vo : roomList) {
            if (timeHasUsed(scheduleParm.getBeginTime(), scheduleParm.getEndTime(), vo.getBeginTime(),
                    vo.getEndTime())) {
                roomErrorSet.add(String.format("教室冲突: %s %s - %s，教室: %s，与课程: %s 冲突",
                        vo.getDateTime(), vo.getBeginTime(), vo.getEndTime(), vo.getRoomName(), vo.getCourseName()));
            }
        }

        // 查询教师时间是否冲突
        List<ScheduleCourseListVo> teacherVoList = this.baseMapper.getScheduleList(Wrappers.query()
                .eq("cs.teacher_id", scheduleParm.getTeacherId())
                .in("cs.date_time", dateList)
                .orderByAsc("cs.date_time,cs.begin_time"));

        // 处理教师占用信息
        Set<String> teacherErrorSet = new HashSet<>();
        for (ScheduleCourseListVo vo : teacherVoList) {
            if (timeHasUsed(scheduleParm.getBeginTime(), scheduleParm.getEndTime(), vo.getBeginTime(),
                    vo.getEndTime())) {
                teacherErrorSet.add(String.format("教师冲突: %s %s - %s，教师: %s，与课程: %s 冲突",
                        vo.getDateTime(), vo.getBeginTime(), vo.getEndTime(), vo.getTeacherName(), vo.getCourseName()));
            }
        }

        // 查询考期时间是否冲突
        // 获取 course_type
        String courseType = baseMapper.getCourseTypeByCourseId(scheduleParm.getCourseId());

        // 构建查询条件，包含 course_type
        QueryWrapper<ScheduleCourse> queryWrapper = Wrappers.<ScheduleCourse>query()
                // .eq("cs.course_id", scheduleParm.getCourseId())
                .eq("course.course_type", courseType) // 添加 course_type 条件
                .in("cs.date_time", dateList)
                .orderByAsc("cs.date_time, cs.begin_time");

        List<ScheduleCourseListVo> courseVoList = this.baseMapper.getScheduleList(queryWrapper);

        // 处理考期占用信息
        Set<String> courseErrorSet = new HashSet<>();
        for (ScheduleCourseListVo vo : courseVoList) {
            if (timeHasUsed(scheduleParm.getBeginTime(), scheduleParm.getEndTime(), vo.getBeginTime(),
                    vo.getEndTime())) {
                courseErrorSet.add(String.format("考期冲突: %s %s - %s，考期类型: %s，与课程: %s 冲突",
                        vo.getDateTime(), vo.getBeginTime(), vo.getEndTime(), vo.getCourseType(), vo.getCourseName()));
            }
        }

        // 获取子级课程ID
        List<Long> childCourseIds = getChildCourseIds(scheduleParm.getCourseId());

        // 对每个子级课程进行考期冲突检查
        for (Long childCourseId : childCourseIds) {
            queryWrapper = Wrappers.<ScheduleCourse>query()
                    .eq("course.course_type", baseMapper.getCourseTypeByCourseId(childCourseId)) // 获取子级课程的 course_type
                    .in("cs.date_time", dateList)
                    .orderByAsc("cs.date_time, cs.begin_time");

            List<ScheduleCourseListVo> childCourseVoList = this.baseMapper.getScheduleList(queryWrapper);

            for (ScheduleCourseListVo vo : childCourseVoList) {
                if (timeHasUsed(scheduleParm.getBeginTime(), scheduleParm.getEndTime(), vo.getBeginTime(),
                        vo.getEndTime())) {
                    courseErrorSet.add(String.format("考期冲突: %s %s - %s，考期类型: %s，与课程: %s 冲突",
                            vo.getDateTime(), vo.getBeginTime(), vo.getEndTime(), vo.getCourseType(),
                            vo.getCourseName()));
                }
            }
        }

        // 构建最终的错误信息
        StringBuilder errorMessage = new StringBuilder();
        if (!roomErrorSet.isEmpty()) {
            errorMessage.append(String.join(",", roomErrorSet));
        }
        if (!teacherErrorSet.isEmpty()) {
            errorMessage.append(String.join(",", teacherErrorSet));
        }
        if (!courseErrorSet.isEmpty()) {
            errorMessage.append(String.join(",", courseErrorSet));
        }

        // 如果有时间冲突
        if (errorMessage.length() > 0) {
            throw new BusinessException(BusinessExceptionEnum.SCHEDULE_ERROR.getCode(), errorMessage.toString());
        }

        // 排课入库处理
        List<ScheduleCourse> list = new ArrayList<>();
        for (LocalDate date : dateList) {
            // 生成一个唯一的UUID
            String uniqueDelId = UUIDUtils.generateNumericUUID();
            // 添加主课程
            ScheduleCourse schedule = new ScheduleCourse();
            schedule.setDateTime(date);
            schedule.setDuration(scheduleParm.getDuration());
            schedule.setTeacherId(scheduleParm.getTeacherId());
            schedule.setCourseId(scheduleParm.getCourseId());
            schedule.setRoomId(scheduleParm.getRoomId());
            schedule.setBeginTime(scheduleParm.getBeginTime());
            schedule.setEndTime(scheduleParm.getEndTime());
            schedule.setDelId(uniqueDelId);
            schedule.setCourseType(courseType);
            schedule.setLessonName(scheduleParm.getLessonName());
            schedule.setStageId(scheduleParm.getStageId());
            System.out.println("=== 主课程调试信息 ===");
            System.out.println("参数中的阶段ID: " + scheduleParm.getStageId());
            System.out.println("设置后的阶段ID: " + schedule.getStageId());
            System.out.println("主课程完整对象: " + schedule);
            System.out.println("========================");
            list.add(schedule);

            // 添加子课程
            for (Long childCourseId : childCourseIds) {
                ScheduleCourse childSchedule = new ScheduleCourse();
                childSchedule.setDateTime(date);
                childSchedule.setDuration(scheduleParm.getDuration());
                childSchedule.setTeacherId(scheduleParm.getTeacherId());
                childSchedule.setCourseId(childCourseId);
                childSchedule.setRoomId(scheduleParm.getRoomId());
                childSchedule.setBeginTime(scheduleParm.getBeginTime());
                childSchedule.setEndTime(scheduleParm.getEndTime());
                childSchedule.setDelId(uniqueDelId);
                childSchedule.setCourseType(baseMapper.getCourseTypeByCourseId(childCourseId));
                childSchedule.setLessonName(scheduleParm.getLessonName());
                childSchedule.setStageId(scheduleParm.getStageId());
                System.out.println("设置子课程阶段ID: " + scheduleParm.getStageId() + ", 子课程ID: " + childCourseId);
                list.add(childSchedule);
            }
        }

        // 动态调整批次大小
        int maxConnections = getMaxDatabaseConnections(); // 假设这是一个方法，返回数据库的最大连接数
        double currentLoad = getCurrentDatabaseLoad(); // 假设这是一个方法，返回当前数据库的负载百分比

        // 计算新的批次大小
        int dynamicBatchSize = calculateDynamicBatchSize(initialBatchSize, maxConnections, currentLoad);

        // 分批保存
        System.out.println("=== 批量保存调试信息 ===");
        System.out.println("总共要保存的记录数: " + list.size());
        for (int i = 0; i < list.size(); i += dynamicBatchSize) {
            int end = Math.min(i + dynamicBatchSize, list.size());
            List<ScheduleCourse> batch = list.subList(i, end);
            System.out.println("保存批次 " + (i/dynamicBatchSize + 1) + ", 记录数: " + batch.size());
            for (int j = 0; j < batch.size(); j++) {
                ScheduleCourse sc = batch.get(j);
                System.out.println("  记录" + (j+1) + ": 课时=" + sc.getLessonName() + ", 阶段ID=" + sc.getStageId());
            }
            boolean result = this.saveBatch(batch);
            System.out.println("批次保存结果: " + result);
        }
        System.out.println("========================");

        // 添加课时后重新排序 - 需要对所有相关考期进行排序
        reorderLessonNamesForAllRelatedCourseTypes(scheduleParm.getCourseId(), courseType, childCourseIds);

    }

    private int getMaxDatabaseConnections() {
        // 这里可以实现获取数据库最大连接数的逻辑
        // 例如从配置文件或数据库元数据中获取
        return 100; // 示例值
    }

    private double getCurrentDatabaseLoad() {
        // 这里可以实现获取当前数据库负载的逻辑
        // 例如通过监控工具或数据库查询获取
        return 50.0; // 示例值，表示当前负载为50%
    }

    private int calculateDynamicBatchSize(int initialBatchSize, int maxConnections, double currentLoad) {
        // 根据最大连接数和当前负载计算新的批次大小
        // 这里简单地按照比例调整批次大小
        int adjustedBatchSize = (int) (initialBatchSize * ((double) maxConnections / 100)
                * ((100 - currentLoad) / 100));
        return Math.max(adjustedBatchSize, 1); // 确保批次大小至少为1
    }

    private List<Long> getChildCourseIds(Long courseId) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_course_id", courseId);
        List<Course> childCourses = courseMapper.selectList(queryWrapper);
        List<Long> childCourseIds = new ArrayList<>();
        for (Course course : childCourses) {
            childCourseIds.add(course.getCourseId());
        }
        return childCourseIds;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleCourse> selectCourseSchedulingList(ListParm query) {
        log.info("Query parameters: {}", query);
        // System.out.println("参数implimplimplimplimplimplimpl在这在这在这在这在这在这在这在这"+query);
        try {
            List<ScheduleCourse> result = this.baseMapper.selectCourseSchedulingList(query);
            log.info("Query result: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Error in selectCourseSchedulingList with query: {}", query, e);
            throw new BusinessException(BusinessExceptionEnum.SCHEDULE_ERROR.getCode(), "查询排课列表时发生错误");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public IPage<ScheduleCourse> selectCourseSchedulingListWithPage(ListParm query) {
        log.info("Query parameters with pagination: {}", query);
        try {
            // 构造分页对象
            IPage<ScheduleCourse> page = new Page<>();
            page.setCurrent(query.getCurrentPage() != null ? query.getCurrentPage() : 1);
            page.setSize(query.getPageSize() != null ? query.getPageSize() : 10);

            // 使用Mapper的分页查询方法
            IPage<ScheduleCourse> result = this.baseMapper.selectCourseSchedulingListWithPage(page, query);
            log.info("Query result with pagination: total={}, records={}", result.getTotal(), result.getRecords().size());
            return result;
        } catch (Exception e) {
            log.error("Error in selectCourseSchedulingListWithPage with query: {}", query, e);
            throw new BusinessException(BusinessExceptionEnum.SCHEDULE_ERROR.getCode(), "查询排课列表时发生错误");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleCourse> selectClassRoomlingList(ListParm query) {
        log.info("Query parameters: {}", query);
        System.out.println("教室参数：" + query);
        try {
            List<ScheduleCourse> result = this.baseMapper.selectClassRoomlingList(query);
            log.info("Query result: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Error in selectCourseSchedulingList with query: {}", query, e);
            throw new BusinessException(BusinessExceptionEnum.SCHEDULE_ERROR.getCode(), "查询排课列表时发生错误");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleCourse> selectTeaList(ListParm query) {
        log.info("Query parameters: {}", query);
        System.out.println("教师参数：" + query);
        try {
            List<ScheduleCourse> result = this.baseMapper.selectTeaList(query);
            log.info("Query result: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Error in selectCourseSchedulingList with query: {}", query, e);
            throw new BusinessException(BusinessExceptionEnum.SCHEDULE_ERROR.getCode(), "查询排课列表时发生错误");
        }
    }

    // 时间转为分钟
    private Integer toMinute(LocalTime time) {
        return time.getHour() * 60 + time.getMinute();
    }

    private boolean isBetween(LocalTime time, LocalTime desTime1, LocalTime destTime2) {
        Integer minute = toMinute(time);
        return toMinute(desTime1) < minute && minute < toMinute(destTime2);
    }

    // 判断时间是否冲突
    private boolean timeHasUsed(LocalTime beginTime, LocalTime endTime, LocalTime dbBeginTime, LocalTime dbEndTime) {
        return isBetween(beginTime, dbBeginTime, dbEndTime)
                || isBetween(endTime, dbBeginTime, dbEndTime)
                || isBetween(dbBeginTime, beginTime, endTime)
                || isBetween(dbEndTime, beginTime, endTime)
                || (beginTime.equals(dbBeginTime) && endTime.equals(dbEndTime));
    }

    @Override
    @Transactional
    @CacheEvict(value = { "scheduleCourseList", "scheduleCourseListByParams" }, allEntries = true)
    public boolean removeByCourseId(Long courseId) {
        return this.remove(new QueryWrapper<ScheduleCourse>().eq("course_id", courseId));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasScheduleDataByCourseId(Long courseId) {
        return this.count(new QueryWrapper<ScheduleCourse>().eq("course_id", courseId)) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "scheduleCourseListByParams", key = "#queryDTO.toString()")
    public List<ScheduleCourse> getScheduleCourseList(ScheduleQueryDTO queryDTO) {
        QueryWrapper<ScheduleCourse> wrapper = new QueryWrapper<>();

        // 添加查询条件
        if (StringUtils.isNotBlank(queryDTO.getCourseName())) {
            wrapper.like("course.course_name", queryDTO.getCourseName());
        }
        if (StringUtils.isNotBlank(queryDTO.getCourseType())) {
            wrapper.eq("course.course_type", queryDTO.getCourseType());
        }
        if (StringUtils.isNotBlank(queryDTO.getTeacherName())) {
            wrapper.eq("teacher.teacher_name", queryDTO.getTeacherName());
        }
        // 处理教师ID，支持多个教师ID
        if (StringUtils.isNotBlank(queryDTO.getTeacherId())) {
            String[] teacherIds = queryDTO.getTeacherId().split(",");
            if (teacherIds.length > 0) {
                wrapper.in("cs.teacher_id", (Object[]) teacherIds);
            }
        }
        if (StringUtils.isNotBlank(queryDTO.getCourseId())) {
            wrapper.eq("cs.course_id", queryDTO.getCourseId());
        }

        // 如果日期范围都不为空，则添加日期范围条件
        // 如果日期范围为空，则不添加条件，查询所有数据
        if (StringUtils.isNotBlank(queryDTO.getStartDate()) && StringUtils.isNotBlank(queryDTO.getEndDate())) {
            wrapper.between("cs.date_time", queryDTO.getStartDate(), queryDTO.getEndDate());
        }

        // 按日期、时间和课程类型排序
        wrapper.orderByAsc("cs.date_time", "cs.begin_time", "course.course_type");

        return baseMapper.getScheduleCourseList(wrapper);
    }

    @Override
    @Transactional(readOnly = true)
    public IPage<ScheduleCourse> getScheduleCourseListWithPage(ScheduleQueryDTO queryDTO) {
        log.info("Query parameters with pagination: {}", queryDTO);
        try {
            // 构造分页对象
            IPage<ScheduleCourse> page = new Page<>();
            page.setCurrent(queryDTO.getCurrentPage() != null ? queryDTO.getCurrentPage() : 1);
            page.setSize(queryDTO.getPageSize() != null ? queryDTO.getPageSize() : 10);

            QueryWrapper<ScheduleCourse> wrapper = new QueryWrapper<>();

            // 添加查询条件
            if (StringUtils.isNotBlank(queryDTO.getCourseName())) {
                wrapper.like("course.course_name", queryDTO.getCourseName());
            }
            if (StringUtils.isNotBlank(queryDTO.getCourseType())) {
                wrapper.eq("course.course_type", queryDTO.getCourseType());
            }
            if (StringUtils.isNotBlank(queryDTO.getTeacherName())) {
                wrapper.eq("teacher.teacher_name", queryDTO.getTeacherName());
            }
            // 处理教师ID，支持多个教师ID
            if (StringUtils.isNotBlank(queryDTO.getTeacherId())) {
                String[] teacherIds = queryDTO.getTeacherId().split(",");
                if (teacherIds.length > 0) {
                    wrapper.in("cs.teacher_id", (Object[]) teacherIds);
                }
            }
            if (StringUtils.isNotBlank(queryDTO.getCourseId())) {
                wrapper.eq("cs.course_id", queryDTO.getCourseId());
            }

            // 如果日期范围都不为空，则添加日期范围条件
            // 如果日期范围为空，则不添加条件，查询所有数据
            if (StringUtils.isNotBlank(queryDTO.getStartDate()) && StringUtils.isNotBlank(queryDTO.getEndDate())) {
                wrapper.between("cs.date_time", queryDTO.getStartDate(), queryDTO.getEndDate());
            }

            // 按日期、时间和课程类型排序
            wrapper.orderByAsc("cs.date_time", "cs.begin_time", "course.course_type");

            // 使用分页查询
            IPage<ScheduleCourse> result = baseMapper.getScheduleCourseListWithPage(page, wrapper);
            log.info("Query result with pagination: total={}, records={}", result.getTotal(), result.getRecords().size());
            return result;
        } catch (Exception e) {
            log.error("Error in getScheduleCourseListWithPage with query: {}", queryDTO, e);
            throw new BusinessException(BusinessExceptionEnum.SCHEDULE_ERROR.getCode(), "查询排课列表时发生错误");
        }
    }

    // @Override
    // @Transactional(readOnly = true)
    // public List<ScheduleCourse> getScheduleCourseList() {
    // // 查询所有排课信息
    // return baseMapper.getScheduleCourseList();
    // }

    @Override
    @Transactional
    public void updateScheduleAndCourse(ScheduleCourse scheduleCourse) {
        // System.out.println("impl11111111111111111" + scheduleCourse);

        // 构建查询条件以检查教师冲突
        QueryWrapper<ScheduleCourse> teacherQueryWrapper = Wrappers.<ScheduleCourse>query()
                .ne("id", scheduleCourse.getId()) // 排除自身记录
                .eq("teacher_id", scheduleCourse.getTeacherId())
                .eq("date_time", scheduleCourse.getDateTime())
                .and(wrapper -> wrapper
                        .between("begin_time", scheduleCourse.getBeginTime(), scheduleCourse.getEndTime())
                        .or().between("end_time", scheduleCourse.getBeginTime(), scheduleCourse.getEndTime())
                        .or().nested(nested -> nested.le("begin_time", scheduleCourse.getBeginTime()).ge("end_time",
                                scheduleCourse.getEndTime())));

        // 查询是否存在教师冲突
        int teacherConflictCount = this.count(teacherQueryWrapper);
        if (teacherConflictCount > 0) {
            // 查询冲突的课程信息
            List<ScheduleCourseListVo> conflictList = baseMapper.getScheduleList(teacherQueryWrapper);
            StringBuilder conflictInfo = new StringBuilder("已存在相同时间段的教师安排，冲突课程：");
            for (ScheduleCourseListVo vo : conflictList) {
                conflictInfo.append(vo.getCourseName()).append("(").append(vo.getDateTime()).append(" ")
                        .append(vo.getBeginTime()).append("-").append(vo.getEndTime()).append(") ");
            }
            throw new BusinessException(BusinessExceptionEnum.SCHEDULE_ERROR.getCode(), conflictInfo.toString());
        }

        // 构建查询条件以检查教室冲突
        QueryWrapper<ScheduleCourse> roomQueryWrapper = Wrappers.<ScheduleCourse>query()
                .ne("id", scheduleCourse.getId()) // 排除自身记录
                .eq("room_id", scheduleCourse.getRoomId())
                .eq("date_time", scheduleCourse.getDateTime())
                .and(wrapper -> wrapper
                        .between("begin_time", scheduleCourse.getBeginTime(), scheduleCourse.getEndTime())
                        .or().between("end_time", scheduleCourse.getBeginTime(), scheduleCourse.getEndTime())
                        .or().nested(nested -> nested.le("begin_time", scheduleCourse.getBeginTime()).ge("end_time",
                                scheduleCourse.getEndTime())));

        // 查询是否存在教室冲突
        int roomConflictCount = this.count(roomQueryWrapper);
        if (roomConflictCount > 0) {
            // 查询冲突的课程信息
            List<ScheduleCourseListVo> conflictList = baseMapper.getScheduleList(roomQueryWrapper);
            StringBuilder conflictInfo = new StringBuilder("已存在相同时间段的教室安排，冲突课程：");
            for (ScheduleCourseListVo vo : conflictList) {
                conflictInfo.append(vo.getCourseName()).append("(").append(vo.getDateTime()).append(" ")
                        .append(vo.getBeginTime()).append("-").append(vo.getEndTime()).append(") ");
            }
            throw new BusinessException(BusinessExceptionEnum.SCHEDULE_ERROR.getCode(), conflictInfo.toString());
        }

        Course course = new Course();
        course.setCourseId(scheduleCourse.getCourseId());
        course.setCourseType(scheduleCourse.getCourseType());
        course.setMajorName(scheduleCourse.getMajorName());
        course.setCourseYear(scheduleCourse.getCourseYear());
        course.setCourseName(scheduleCourse.getCourseName());
        course.setCourseColor(scheduleCourse.getCourseColor());
        courseMapper.updateById(course);

        // 更新 schedule_course 表
        ScheduleCourse scheduleCourseEntity = new ScheduleCourse();
        scheduleCourseEntity.setId(scheduleCourse.getId());
        scheduleCourseEntity.setRoomId(scheduleCourse.getRoomId());
        scheduleCourseEntity.setTeacherId(scheduleCourse.getTeacherId());
        scheduleCourseEntity.setDateTime(scheduleCourse.getDateTime());
        scheduleCourseEntity.setBeginTime(scheduleCourse.getBeginTime());
        scheduleCourseEntity.setDuration(scheduleCourse.getDuration());
        scheduleCourseEntity.setEndTime(scheduleCourse.getEndTime());
        scheduleCourseEntity.setCourseId(scheduleCourse.getCourseId());
        scheduleCourseEntity.setTeacherId(scheduleCourse.getTeacherId());
        scheduleCourseEntity.setRoomId(scheduleCourse.getRoomId());
        this.updateById(scheduleCourseEntity);
    }

    @Override
    @Transactional
    public void deleteCalendarListById(Long delId) {
        // 首先获取要删除的所有课程信息（可能包含多个考期），用于后续重新排序
        List<ScheduleCourse> deletedSchedules = this.list(new QueryWrapper<ScheduleCourse>().eq("del_id", delId));
        if (deletedSchedules.isEmpty()) {
            throw new RuntimeException("要删除的课程不存在");
        }

        // 删除课程（会删除所有具有相同delId的记录）
        baseMapper.deleteByDelId(delId);

        // 对所有受影响的课程和考期进行重新排序
        reorderLessonNamesForDeletedSchedules(deletedSchedules);
    }

    /**
     * 重新排序课程的课时名称
     * 
     * @param courseId   课程ID
     * @param courseType 课程类型
     */
    private void reorderLessonNames(Long courseId, String courseType) {
        // 查询同一课程的所有剩余课时，按日期和时间排序
        QueryWrapper<ScheduleCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId)
                .eq("course_type", courseType)
                .orderByAsc("date_time", "begin_time");

        List<ScheduleCourse> remainingSchedules = this.list(queryWrapper);

        if (remainingSchedules.isEmpty()) {
            return;
        }

        // 按阶段分组重新排序
        Map<String, List<ScheduleCourse>> stageGroups = new HashMap<>();

        for (ScheduleCourse schedule : remainingSchedules) {
            String lessonName = schedule.getLessonName();
            if (lessonName == null || lessonName.trim().isEmpty()) {
                continue;
            }

            // 提取阶段名称（精讲、密训、真题、其他）
            String stageName = extractStageName(lessonName);

            stageGroups.computeIfAbsent(stageName, k -> new ArrayList<>()).add(schedule);
        }

        // 为每个阶段重新编号
        List<ScheduleCourse> schedulesToUpdate = new ArrayList<>();

        for (Map.Entry<String, List<ScheduleCourse>> entry : stageGroups.entrySet()) {
            String stageName = entry.getKey();
            List<ScheduleCourse> stageSchedules = entry.getValue();

            // 按日期和时间排序
            stageSchedules.sort((a, b) -> {
                int dateCompare = a.getDateTime().compareTo(b.getDateTime());
                if (dateCompare != 0)
                    return dateCompare;
                return a.getBeginTime().compareTo(b.getBeginTime());
            });

            // 重新编号
            for (int i = 0; i < stageSchedules.size(); i++) {
                ScheduleCourse schedule = stageSchedules.get(i);
                String newLessonName = generateNewLessonName(schedule.getLessonName(), stageName, i + 1);
                schedule.setLessonName(newLessonName);
                schedulesToUpdate.add(schedule);
            }
        }

        // 批量更新
        if (!schedulesToUpdate.isEmpty()) {
            this.updateBatchById(schedulesToUpdate);
        }
    }

    /**
     * 从课时名称中提取阶段名称
     * 
     * @param lessonName 课时名称
     * @return 阶段名称
     */
    private String extractStageName(String lessonName) {
        String[] stageNames = { "精讲", "密训", "真题", "考点","其他" };

        for (String stage : stageNames) {
            if (lessonName.contains(stage)) {
                return stage;
            }
        }

        // 如果没有找到已知的阶段名称，返回"其他"
        return "其他";
    }

    /**
     * 生成新的课时名称
     * 
     * @param originalLessonName 原始课时名称
     * @param stageName          阶段名称
     * @param newIndex           新的序号
     * @return 新的课时名称
     */
    private String generateNewLessonName(String originalLessonName, String stageName, int newIndex) {
        // 提取课程基础名称（去掉阶段和序号部分）
        String baseName = originalLessonName;

        // 找到阶段名称的位置
        int stageIndex = baseName.indexOf(stageName);
        if (stageIndex != -1) {
            baseName = baseName.substring(0, stageIndex);
        }

        // 生成新的课时名称：基础名称 + 阶段名称 + 格式化的序号
        String paddedIndex = String.format("%02d", newIndex);
        return baseName + stageName + paddedIndex;
    }

    @Override
    public List<ScheduleCourse> selectCorseTypeList(ListParm listParm) {
        return Collections.emptyList();
    }

    @Override
    public void reorderLessonNamesAfterAdd(Long courseId, String courseType) {
        // 调用已有的重新排序方法
        reorderLessonNames(courseId, courseType);
    }

    /**
     * 为所有相关的课程和考期重新排序课时名称
     *
     * @param mainCourseId   主课程ID
     * @param mainCourseType 主课程考期
     * @param childCourseIds 子课程ID列表
     */
    private void reorderLessonNamesForAllRelatedCourseTypes(Long mainCourseId, String mainCourseType,
            List<Long> childCourseIds) {
        // 首先对主课程的考期进行排序
        reorderLessonNames(mainCourseId, mainCourseType);

        // 然后对每个子课程的考期进行排序
        for (Long childCourseId : childCourseIds) {
            String childCourseType = baseMapper.getCourseTypeByCourseId(childCourseId);
            if (childCourseType != null) {
                reorderLessonNames(childCourseId, childCourseType);
            }
        }
    }

    /**
     * 为删除操作后的所有受影响课程和考期重新排序课时名称
     * 
     * @param deletedSchedules 被删除的课程列表
     */
    private void reorderLessonNamesForDeletedSchedules(List<ScheduleCourse> deletedSchedules) {
        // 收集所有受影响的课程ID和考期类型的组合，避免重复排序
        Set<String> processedCombinations = new HashSet<>();

        for (ScheduleCourse deletedSchedule : deletedSchedules) {
            String combination = deletedSchedule.getCourseId() + "-" + deletedSchedule.getCourseType();
            if (!processedCombinations.contains(combination)) {
                reorderLessonNames(deletedSchedule.getCourseId(), deletedSchedule.getCourseType());
                processedCombinations.add(combination);
            }
        }
    }

}
