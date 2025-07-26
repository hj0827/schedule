package com.hj.web.course.controller;

import com.hj.utils.ResultUtils;
import com.hj.utils.ResultVo;
import com.hj.web.course.entity.Stage;
import com.hj.web.course.service.StageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stages")
public class StageController {

    @Autowired
    private StageService stageService;

    // 获取阶段列表
    @GetMapping
    public ResultVo<List<Stage>> getStageList() {
        System.out.println("接收到获取阶段列表的请求");
        List<Stage> stages = stageService.getAllStages();
        return ResultUtils.success("获取阶段列表成功", stages);
    }

    // 编辑阶段
    @PutMapping
    public ResultVo<Void> updateStage(@RequestBody Stage stage) {
        stageService.updateStage(stage);
        return ResultUtils.success("阶段更新成功");
    }

    // 删除阶段
    @DeleteMapping("/{id}")
    public ResultVo<Void> deleteStage(@PathVariable Long id) {
        stageService.deleteStage(id);
        return ResultUtils.success("阶段删除成功");
    }

    @PostMapping
    public ResultVo<Void> addStage(@RequestBody Stage stage) {
        stageService.addStage(stage);
        return ResultUtils.success("阶段添加成功");
    }

}
