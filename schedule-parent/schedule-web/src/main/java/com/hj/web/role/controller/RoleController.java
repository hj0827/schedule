package com.hj.web.role.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hj.utils.ResultUtils;
import com.hj.utils.ResultVo;
import com.hj.web.role.entity.AssignParm;
import com.hj.web.role.entity.AssignVo;
import com.hj.web.role.entity.ListParm;
import com.hj.web.role.entity.Role;
import com.hj.web.role.service.RoleService;
import com.hj.web.role_menu.entity.SaveAssign;
import com.hj.web.role_menu.service.RoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleMenuService roleMenuService;

//    新增
    @PostMapping
    public ResultVo add(@RequestBody Role role){
        boolean save = roleService.save(role);
        if(save){
            return ResultUtils.success("添加成功！");
        }
        return ResultUtils.error("添加失败");
    }
//    编辑
    @PutMapping
    public ResultVo edit(@RequestBody Role role){
        boolean update = roleService.updateById(role);
        if(update){
            return ResultUtils.success("编辑成功！");
        }
        return ResultUtils.error("编辑失败");
    }
//    删除
    @DeleteMapping("/{roleId}")
    public ResultVo delete(@PathVariable("roleId") Long roleId){
        boolean remove = roleService.removeById(roleId);
        if(remove){
            return ResultUtils.success("删除成功！");
        }
        return ResultUtils.error("删除失败");
    }
//    查询列表
    @GetMapping("/list")
    public ResultVo getList(ListParm listParm){
        IPage<Role> list = roleService.getList(listParm);
        return ResultUtils.success("查询成功！",list);
    }

//    查询分配权限回显
    @GetMapping("/getAssingShow")
    public ResultVo getAssignShow(AssignParm parm){
        AssignVo vo = roleService.getAssignShow(parm);
        return ResultUtils.success("查询成功！",vo);
    }

//    分配权限保存
    @PostMapping("/assignSave")
    public ResultVo assignSave(@RequestBody SaveAssign saveAssign){
    roleMenuService.assignSave(saveAssign);
    return ResultUtils.success("分配权限成功！");
    }

}
