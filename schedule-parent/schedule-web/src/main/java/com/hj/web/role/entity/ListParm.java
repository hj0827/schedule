package com.hj.web.role.entity;

import lombok.Data;

@Data
public class ListParm {
    private int pageSize;
    private int currentPage;
    private String roleName;
}
