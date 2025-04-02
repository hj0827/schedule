package com.hj.web.teacher.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hj.utils.ResultUtils;
import com.hj.utils.ResultVo;
import com.hj.web.teacher.entity.Teacher;
import com.hj.web.teacher.entity.TeacherParm;
import com.hj.web.teacher.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {
    @Autowired
    private TeacherService teacherService;

    //    新增
    @PostMapping
    public ResultVo add(@RequestBody Teacher teacher) {
        // 检查教师名称是否已经存在
        if (teacherService.isTeacherNameExists(teacher.getTeacherName())) {
            return ResultUtils.error("教师名称已存在，不能添加！");
        }

        boolean save = teacherService.save(teacher);
        if (save) {
            return ResultUtils.success("添加成功！");
        }
        return ResultUtils.error("添加失败");
    }

    //    编辑
    @PutMapping
    public ResultVo edit(@RequestBody Teacher teacher) {
        // 检查 teacher 是否包含主键
        if (teacher.getTeacherId() == null) {
            return ResultUtils.error("缺少主键ID，无法更新");
        }

        boolean updateSuccess = teacherService.updateById(teacher);
        if (updateSuccess) {
            return ResultUtils.success("修改成功！");
        } else {
            return ResultUtils.error("修改失败");
        }
    }

    //    删除
    @DeleteMapping("/{teacherId}")
    public ResultVo delete(@PathVariable("teacherId") Long teacherId) {
        try {
            boolean remove = teacherService.removeById(teacherId);
            if (remove) {
                return ResultUtils.success("删除成功！");
            }
            return ResultUtils.error("删除失败");
        } catch (DataIntegrityViolationException e) {
            return ResultUtils.error("删除失败，该教师还有课程，无法删除。");
        }
    }

    //    列表查询
    @GetMapping("/list")
    public ResultVo getList(TeacherParm parm) {
        IPage<Teacher> list = teacherService.getList(parm);
        return ResultUtils.success("查询成功", list);
    }

}
