package com.hj.web.classroom.entity;
//查询
import lombok.Data;

@Data
public class ListParm {
    private Long currentPage;
    private Long pageSize;
    private String roomName;
}
