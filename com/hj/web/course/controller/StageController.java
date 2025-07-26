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

    @GetMapping
    public ResultVo<List<Stage>> getStageList() {
        System.out.println("接收到获取阶段列表的请求");
        List<Stage> stages = stageService.getAllStages();
        return ResultUtils.success(stages);
    }
} 