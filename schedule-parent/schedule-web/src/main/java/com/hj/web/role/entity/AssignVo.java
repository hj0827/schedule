package com.hj.web.role.entity;
import com.hj.web.menu.entity.Menu;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AssignVo {
//    当前用户拥有的菜单
    private List<Menu> menuList = new ArrayList<>();
//    被分配的角色拥有的菜单
    private Object[] checkList;
}
