package com.hj.web.teacher.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hj.web.teacher.entity.Teacher;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface TeacherMapper extends BaseMapper<Teacher> {
    int exists(@Param("teacherName") String teacherName);
}
