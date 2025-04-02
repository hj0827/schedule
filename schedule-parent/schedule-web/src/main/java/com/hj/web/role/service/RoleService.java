package com.hj.web.role.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hj.web.role.entity.AssignParm;
import com.hj.web.role.entity.AssignVo;
import com.hj.web.role.entity.ListParm;
import com.hj.web.role.entity.Role;

public interface RoleService extends IService<Role> {
    IPage<Role> getList(ListParm listParm);
//    角色权限的回显
    AssignVo getAssignShow(AssignParm parm);
}
