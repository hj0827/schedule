package com.hj.web.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hj.web.course.entity.Course;

public interface CourseService extends IService<Course> {
    boolean isCourseExists(String courseName, String majorName, String courseType, String courseYear);
//    判断是否已经添加了相应的课程

}
