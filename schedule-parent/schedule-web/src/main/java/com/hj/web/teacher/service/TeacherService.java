package com.hj.web.teacher.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hj.web.teacher.entity.Teacher;
import com.hj.web.teacher.entity.TeacherParm;

public interface TeacherService extends IService<Teacher> {
    IPage<Teacher> getList(TeacherParm parm);
    boolean isTeacherNameExists(String teacherName);

}
