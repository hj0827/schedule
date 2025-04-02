package com.hj.web.login.entity;

import lombok.Data;

import java.util.List;

@Data
public class MenuResult {
//    菜单数据
    private List<RouterVO> menuList;
//    权限字段
    private Object[] authList;
 }
