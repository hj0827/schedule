package com.hj.web.course.service.impl;

import com.hj.web.course.entity.Stage;
import com.hj.web.course.mapper.StageMapper;
import com.hj.web.course.service.StageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class StageServiceImpl implements StageService {

    private static final Logger logger = Logger.getLogger(StageServiceImpl.class.getName());

    @Autowired
    private StageMapper stageMapper;

    @Override
    public List<Stage> getAllStages() {
        List<Stage> stages = stageMapper.selectAllStages();
        if (stages != null && !stages.isEmpty()) {
            logger.info("查询到 " + stages.size() + " 条阶段数据：");
            for (Stage stage : stages) {
                logger.info(stage.toString()); // 确保 Stage 类重写了 toString()
            }
        } else {
            logger.info("未查询到任何阶段数据。");
        }
        return stages;
    }

    @Override
    public void updateStage(Stage stage) {
        stageMapper.updateStage(stage);
    }

    @Override
    public void deleteStage(Long id) {
        stageMapper.deleteStage(id);
    }

    @Override
    public void addStage(Stage stage) {
        stageMapper.insertStage(stage);
    }
}
