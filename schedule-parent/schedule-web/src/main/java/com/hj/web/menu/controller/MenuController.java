package com.hj.web.menu.controller;

import com.hj.utils.ResultUtils;
import com.hj.utils.ResultVo;
import com.hj.web.menu.entity.Menu;
import com.hj.web.menu.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;

//    新增
    @PostMapping
    public ResultVo add(@RequestBody Menu menu){
       menu.setCreateTime(new Date());
       menu.setUpdateTime(new Date());
        boolean save = menuService.save(menu);
       if(save){
           return ResultUtils.success("添加成功！");
       }
       return ResultUtils.error("添加失败");
    }

// 编辑
    @PutMapping
    public ResultVo edit(@RequestBody Menu menu){
        menu.setUpdateTime(new Date());
        boolean b = menuService.updateById(menu);
        if(b){
            return ResultUtils.success("编辑成功！");
        }
        return ResultUtils.error("编辑失败");
    }

//    删除
    @DeleteMapping("/{menuId}")
    public ResultVo deleteMenu(@PathVariable("menuId") Long menuId){
        menuService.deleteMenu(menuId);
        return ResultUtils.success("删除成功");
    }

//    列表
    @GetMapping("/list")
    public ResultVo getList(){
        List<Menu> list = menuService.getList();
        return ResultUtils.success("查询成功",list);
    }

//    上级菜单树数据
    @GetMapping("/parent")
    public ResultVo getParentList(){
        List<Menu> list = menuService.parentList();
        return ResultUtils.success("查询成功",list);
    }

}
