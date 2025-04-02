package com.hj.web.user_role.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hj.web.user_role.entity.UserRole;
import com.hj.web.user_role.mapper.UserRoleMapper;
import com.hj.web.user_role.service.UserRoleService;
import org.springframework.stereotype.Service;

@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {
}
