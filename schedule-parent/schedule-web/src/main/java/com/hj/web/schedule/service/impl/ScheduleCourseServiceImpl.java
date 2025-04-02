package com.hj.web.schedule.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hj.exception_advice.BusinessException;
import com.hj.exception_advice.BusinessExceptionEnum;
import com.hj.web.course.entity.Course;
import com.hj.web.course.mapper.CourseMapper;
import com.hj.web.schedule.entity.ListParm;
import com.hj.web.schedule.entity.ScheduleCourse;
import com.hj.web.schedule.entity.ScheduleCourseListVo;
import com.hj.web.schedule.entity.ScheduleParm;
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
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ScheduleCourseServiceImpl extends ServiceImpl<ScheduleCourseMapper, ScheduleCourse> implements ScheduleCourseService {
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
            if (timeHasUsed(scheduleParm.getBeginTime(), scheduleParm.getEndTime(), vo.getBeginTime(), vo.getEndTime())) {
                roomErrorSet.add(String.format("教 室 冲 突 : %s %s - %s %s ...... ", vo.getDateTime(), vo.getBeginTime(), vo.getEndTime(), vo.getRoomName()));
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
            if (timeHasUsed(scheduleParm.getBeginTime(), scheduleParm.getEndTime(), vo.getBeginTime(), vo.getEndTime())) {
                teacherErrorSet.add(String.format("教 师 冲 突 : %s %s - %s %s ......... ", vo.getDateTime(), vo.getBeginTime(), vo.getEndTime(), vo.getTeacherName()));
            }
        }

        // 查询考期时间是否冲突
        // 获取 course_type
        String courseType = baseMapper.getCourseTypeByCourseId(scheduleParm.getCourseId());

        // 构建查询条件，包含 course_type
        QueryWrapper<ScheduleCourse> queryWrapper = Wrappers.<ScheduleCourse>query()
//                .eq("cs.course_id", scheduleParm.getCourseId())
                .eq("course.course_type", courseType) // 添加 course_type 条件
                .in("cs.date_time", dateList)
                .orderByAsc("cs.date_time, cs.begin_time");

        List<ScheduleCourseListVo> courseVoList = this.baseMapper.getScheduleList(queryWrapper);

        // 处理考期占用信息
        Set<String> courseErrorSet = new HashSet<>();
        for (ScheduleCourseListVo vo : courseVoList) {
            if (timeHasUsed(scheduleParm.getBeginTime(), scheduleParm.getEndTime(), vo.getBeginTime(), vo.getEndTime())) {
                courseErrorSet.add(String.format("考 期 冲 突 : %s %s - %s %s ...... ", vo.getDateTime(), vo.getBeginTime(), vo.getEndTime(), vo.getCourseType()));
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
                if (timeHasUsed(scheduleParm.getBeginTime(), scheduleParm.getEndTime(), vo.getBeginTime(), vo.getEndTime())) {
                    courseErrorSet.add(String.format("考 期 冲 突 : %s %s - %s %s ...... ", vo.getDateTime(), vo.getBeginTime(), vo.getEndTime(), vo.getCourseType()));
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
            // 添加主课程
            ScheduleCourse schedule = new ScheduleCourse();
            schedule.setDateTime(date);
            schedule.setDuration(scheduleParm.getDuration());
            schedule.setTeacherId(scheduleParm.getTeacherId());
            schedule.setCourseId(scheduleParm.getCourseId());
            schedule.setRoomId(scheduleParm.getRoomId());
            schedule.setBeginTime(scheduleParm.getBeginTime());
            schedule.setEndTime(scheduleParm.getEndTime());
            schedule.setCourseType(courseType);
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
                childSchedule.setCourseType(baseMapper.getCourseTypeByCourseId(childCourseId));
                list.add(childSchedule);
            }
        }

        // 动态调整批次大小
        int maxConnections = getMaxDatabaseConnections(); // 假设这是一个方法，返回数据库的最大连接数
        double currentLoad = getCurrentDatabaseLoad(); // 假设这是一个方法，返回当前数据库的负载百分比

        // 计算新的批次大小
        int dynamicBatchSize = calculateDynamicBatchSize(initialBatchSize, maxConnections, currentLoad);

        // 分批保存
        for (int i = 0; i < list.size(); i += dynamicBatchSize) {
            int end = Math.min(i + dynamicBatchSize, list.size());
            this.saveBatch(list.subList(i, end));
        }
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
        int adjustedBatchSize = (int) (initialBatchSize * ((double) maxConnections / 100) * ((100 - currentLoad) / 100));
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



//    @Override
//    @Transactional
//    public List<ScheduleCourse> selectCourseSchedulingList(ListParm query) {
////        条件构造器
//        QueryWrapper<ScheduleCourse> queryWrapper = new QueryWrapper<>();
//        queryWrapper.lambda().in(query.getRoomIdList() != null && query.getRoomIdList().size() > 0, ScheduleCourse::getRoomId)
//                .in(query.getCourseIdList() != null && query.getCourseIdList().size() > 0, ScheduleCourse::getCourseId)
//                .in(query.getTeacherIdList() != null && query.getTeacherIdList().size() > 0, ScheduleCourse::getTeacherId)
//                .ge(StringUtils.isNotColumnName(query.getStartDate()), ScheduleCourse::getDateTime, query.getStartDate())
//                .le(StringUtils.isNotColumnName(query.getEndDate()), ScheduleCourse::getDateTime, query.getEndDate())
//                .eq(StringUtils.isNotColumnName(query.getBeginTime()), ScheduleCourse::getBeginTime, query.getBeginTime())
//                .eq(StringUtils.isNotColumnName(query.getEndTime()), ScheduleCourse::getEndTime, query.getEndTime());
//        return this.baseMapper.selectCourseSchedulingList(queryWrapper);
//    }

//    @Override
//    @Transactional
//    public List<ScheduleCourse> selectCourseSchedulingList(ListParm query) {
//        try {
//            List<ScheduleCourse> result = this.baseMapper.selectCourseSchedulingList(query);
////            log.info("Query result for selectCourseSchedulingList with query {}: {}", query, result);
//            System.out.println( "数据"+query+"数据result"+result);
//            return result;
//        } catch (Exception e) {
////            log.error("Error in selectCourseSchedulingList with query: {}", query, e);
//            throw new BusinessException(BusinessExceptionEnum.SCHEDULE_ERROR.getCode(), "查询排课列表时发生错误");
//        }
//    }


    @Override
    @Transactional(readOnly = true)
    public List<ScheduleCourse> selectCourseSchedulingList(ListParm query) {
        log.info("Query parameters: {}", query);
//        System.out.println("参数implimplimplimplimplimplimpl在这在这在这在这在这在这在这在这"+query);
        try {
            List<ScheduleCourse> result = this.baseMapper.selectCourseSchedulingList(query);
            log.info("Query result: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Error in selectCourseSchedulingList with query: {}", query, e);
            throw new BusinessException(BusinessExceptionEnum.SCHEDULE_ERROR.getCode(), "查询排课列表时发生错误");
        }
    }

    //时间转为分钟
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
    @CacheEvict(value = {"scheduleCourseList", "scheduleCourseListByParams"}, allEntries = true)
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
    @Cacheable(value = "scheduleCourseListByParams", key = "#courseName + '_' + #courseType + '_' + #teacherName")
    public List<ScheduleCourse> getScheduleCourseList(String courseName, String courseType, String teacherName) {
//        System.out.println("获取已经排好的课程impl:" + courseName + courseType + teacherName);
        return baseMapper.getScheduleCourseList(courseName, courseType, teacherName);
    }


//    @Override
//    @Transactional(readOnly = true)
//    public List<ScheduleCourse> getScheduleCourseList() {
//        // 查询所有排课信息
//        return baseMapper.getScheduleCourseList();
//    }

    @Override
    @Transactional
    public void updateScheduleAndCourse(ScheduleCourse scheduleCourse) {
//        System.out.println("impl11111111111111111" + scheduleCourse);

        // 构建查询条件以检查教师冲突
        QueryWrapper<ScheduleCourse> teacherQueryWrapper = Wrappers.<ScheduleCourse>query()
                .ne("id", scheduleCourse.getId()) // 排除自身记录
                .eq("teacher_id", scheduleCourse.getTeacherId())
                .eq("date_time", scheduleCourse.getDateTime())
                .and(wrapper -> wrapper.between("begin_time", scheduleCourse.getBeginTime(), scheduleCourse.getEndTime())
                        .or().between("end_time", scheduleCourse.getBeginTime(), scheduleCourse.getEndTime())
                        .or().nested(nested -> nested.le("begin_time", scheduleCourse.getBeginTime()).ge("end_time", scheduleCourse.getEndTime())));

        // 查询是否存在教师冲突
        int teacherConflictCount = this.count(teacherQueryWrapper);
        if (teacherConflictCount > 0) {
            throw new BusinessException(BusinessExceptionEnum.SCHEDULE_ERROR.getCode(), "已存在相同时间段的教师安排");
        }

        // 构建查询条件以检查教室冲突
        QueryWrapper<ScheduleCourse> roomQueryWrapper = Wrappers.<ScheduleCourse>query()
                .ne("id", scheduleCourse.getId()) // 排除自身记录
                .eq("room_id", scheduleCourse.getRoomId())
                .eq("date_time", scheduleCourse.getDateTime())
                .and(wrapper -> wrapper.between("begin_time", scheduleCourse.getBeginTime(), scheduleCourse.getEndTime())
                        .or().between("end_time", scheduleCourse.getBeginTime(), scheduleCourse.getEndTime())
                        .or().nested(nested -> nested.le("begin_time", scheduleCourse.getBeginTime()).ge("end_time", scheduleCourse.getEndTime())));

        // 查询是否存在教室冲突
        int roomConflictCount = this.count(roomQueryWrapper);
        if (roomConflictCount > 0) {
            throw new BusinessException(BusinessExceptionEnum.SCHEDULE_ERROR.getCode(), "已存在相同时间段的教室安排");
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
    public void deleteCalendarListById(Long id) {
        
    }


}
