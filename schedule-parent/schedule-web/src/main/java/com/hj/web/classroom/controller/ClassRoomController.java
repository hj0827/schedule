package com.hj.web.classroom.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hj.utils.ResultUtils;
import com.hj.utils.ResultVo;
import com.hj.web.classroom.entity.ClassRoom;
import com.hj.web.classroom.entity.ListParm;
import com.hj.web.classroom.service.ClassRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/classroom")
public class ClassRoomController {
    @Autowired
    private ClassRoomService classRoomService;

    @PostMapping
//    新增
    public ResultVo add(@RequestBody ClassRoom classRoom) {
        if (classRoomService.isRoomNameExists(classRoom.getRoomName())) {
            return ResultUtils.error("教室名称已存在，不能添加！");
        }

        boolean save = classRoomService.save(classRoom);
        if (save) {
            return ResultUtils.success("添加成功！");
        }
        return ResultUtils.error("添加失败");
    }

    //    编辑
    @PutMapping
    public ResultVo edit(@RequestBody ClassRoom classRoom) {
        boolean update = classRoomService.updateById(classRoom);
        if (update) {
            return ResultUtils.success("编辑成功！");
        }
        return ResultUtils.error("编辑失败");
    }

    //    删除
    @DeleteMapping("/{roomId}")
    public ResultVo delete(@PathVariable("roomId") Long roomId) {
        try {
            boolean remove = classRoomService.removeById(roomId);
            if (remove) {
                return ResultUtils.success("删除成功！");
            }
            return ResultUtils.error("删除失败");
        } catch (DataIntegrityViolationException e) {
            return ResultUtils.error("删除失败，该教室还有课程在使用，无法删除。");
        }
    }

    //    列表查询
    @GetMapping("/list")
    public ResultVo getList(ListParm listParm) {
        IPage<ClassRoom> list = classRoomService.getList(listParm);
        return ResultUtils.success("查询成功！", list);
    }
}
