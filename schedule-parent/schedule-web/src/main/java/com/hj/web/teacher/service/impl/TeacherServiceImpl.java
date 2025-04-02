package com.hj.web.teacher.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hj.web.teacher.entity.Teacher;
import com.hj.web.teacher.entity.TeacherParm;
import com.hj.web.teacher.mapper.TeacherMapper;
import com.hj.web.teacher.service.TeacherService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {
    @Override
    public IPage<Teacher> getList(TeacherParm parm) {
//        构造分页对象
        IPage<Teacher> page = new Page<>();
        page.setCurrent(parm.getCurrentPage());
        page.setSize(parm.getPageSize());
//        查询条件
        QueryWrapper<Teacher> query = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(parm.getTeacherName())) {
            query.lambda().like(Teacher::getTeacherName, parm.getTeacherName());
        }
        return this.baseMapper.selectPage(page, query);
    }

    @Override
    public boolean isTeacherNameExists(String teacherName) {
        return this.baseMapper.exists(teacherName) > 0;
    }


}
