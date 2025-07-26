package com.hj.web.course.service;


import com.hj.web.course.entity.Stage;

import java.util.List;

public interface StageService {
    List<Stage> getAllStages();
    void updateStage(Stage stage);
    void deleteStage(Long id);
    void addStage(Stage stage);
}
