package com.hj.web.course.mapper;


import com.hj.web.course.entity.Stage;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface StageMapper {
    @Select("SELECT * FROM stage_table")
    List<Stage> selectAllStages();
    void updateStage(Stage stage);
    void deleteStage(Long id);
    void insertStage(Stage stage);
}
