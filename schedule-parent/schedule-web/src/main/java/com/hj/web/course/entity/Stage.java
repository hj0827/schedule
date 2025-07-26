package com.hj.web.course.entity;

import lombok.Data;
// 使用 jakarta 替代 javax（适用于 Spring Boot 3+）
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class Stage {
    private Integer id;

    private String stageName;

    private String description;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @Override
    public String toString() {
        return "Stage{" +
                "id=" + id +
                ", stageName='" + stageName + '\'' +
                ", description='" + description + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }

}

