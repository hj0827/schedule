package com.hj.web.role_menu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hj.web.role_menu.entity.RoleMenu;
import com.hj.web.role_menu.entity.SaveAssign;
import com.hj.web.role_menu.mapper.RoleMenuMapper;
import com.hj.web.role_menu.service.RoleMenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {
    @Transactional
    @Override
    public void assignSave(SaveAssign saveAssign) {
//        先删除角色原来的权限
        QueryWrapper<RoleMenu> query = new QueryWrapper<>();
        query.lambda().eq(RoleMenu::getRoleId,saveAssign.getRoleId());
        int delete = this.baseMapper.delete(query);
//        if(delete > 0){
//            新的插入
            this.baseMapper.assignSave(saveAssign.getRoleId(),saveAssign.getList());

//        }
    }
}
