package com.hj.web.course.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hj.web.course.entity.Course;
import com.hj.web.course.mapper.CourseMapper;
import com.hj.web.course.service.CourseService;
import org.springframework.stereotype.Service;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
    @Override
    public boolean isCourseExists(String courseName, String majorName, String courseType, String courseYear) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_name", courseName)
                .eq("major_name", majorName)
                .eq("course_type", courseType)
                .eq("course_year", courseYear);
        return this.count(queryWrapper) > 0;
    }

}
