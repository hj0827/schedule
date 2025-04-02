package com.hj.web.role_menu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("role_menu")
public class RoleMenu {
    @TableId(type = IdType.AUTO)
    private Long roleMenuId;
    private Long roleId;
    private Long menuId;
}
