package com.hj.web.role_menu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hj.web.role_menu.entity.RoleMenu;
import com.hj.web.role_menu.entity.SaveAssign;

public interface RoleMenuService extends IService<RoleMenu> {
    void assignSave(SaveAssign saveAssign);
}
