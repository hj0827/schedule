package com.hj.web.user.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hj.utils.AesUtil;
import com.hj.web.user.entity.PageParm;
import com.hj.web.user.entity.User;
import com.hj.web.user.mapper.UserMapper;
import com.hj.web.user.service.UserService;
import com.hj.web.user_role.entity.UserRole;
import com.hj.web.user_role.service.UserRoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public void addUser(User user) {
//    新增用户
        int insert = this.baseMapper.insert(user);
//    设置用户的角色
        if (insert > 0) {
            UserRole userRole = new UserRole();
            userRole.setUserId(user.getUserId());
            userRole.setRoleId(user.getRoleId());
            userRoleService.save(userRole);
        }
    }

    @Override
    @Transactional
    public void editUser(User user) {
        int i = this.baseMapper.updateById(user);
//        如果有修改，才修改，否则就是不变
//        if(i > 0){
//            UserRole userRole = new UserRole();
//            userRole.setUserId(user.getUserId());
//            userRole.setRoleId(user.getRoleId());
//            userRoleService.updateById(userRole);
//        }
//        先删除，再添加
        if (i > 0) {
            QueryWrapper<UserRole> query = new QueryWrapper<>();
            query.lambda().eq(UserRole::getUserId, user.getUserId());
            userRoleService.remove(query);
            UserRole userRole = new UserRole();
            userRole.setUserId(user.getUserId());
            userRole.setRoleId(user.getRoleId());
            userRoleService.save(userRole);
        }

    }

    @Override
    @Transactional
    public void updatePassword(Long userId, String newPassword) {
        // 根据 userId 查询用户
        User user = this.baseMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 加密新密码
        try {
            newPassword = AesUtil.encrypt(newPassword);
        } catch (Exception e) {
            throw new RuntimeException("密码加密失败: " + e.getMessage(), e);
        }

        // 更新用户密码
        user.setPassword(newPassword);
        user.setUpdateTime(new Date());
        this.baseMapper.updateById(user);
    }


    @Override
    public void deleteUser(Long userId) {
//    删除角色
        int i = this.baseMapper.deleteById(userId);
        if (i > 0) {
//    删除用户
            QueryWrapper<UserRole> query = new QueryWrapper<>();
            query.lambda().eq(UserRole::getUserId, userId);
            userRoleService.remove(query);
        }
    }


    @Override
    public IPage<User> getUserList(PageParm parm) {
//        构造分页对象
        IPage page = new Page(parm.getCurrentPage(), parm.getPageSize());
//        构造查询条件
        QueryWrapper<User> query = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(parm.getName())) {
            query.lambda().like(User::getName, parm.getName());
        }
        if (StringUtils.isNotEmpty(parm.getPhone())) {
            query.lambda().like(User::getPhone, parm.getPhone());
        }
        return this.baseMapper.selectPage(page, query);
    }

    //    导入
    @Override
    @Transactional
    public List<User> importUsers(List<User> users) {
        System.out.println("Importing users: " + users);
        // 检查所有用户是否都不存在
        List<String> usernames = new ArrayList<>();
        for (User user : users) {
            usernames.add(user.getUsername());
        }

        QueryWrapper<User> query = new QueryWrapper<>();
        query.lambda().in(User::getUsername, usernames);
        List<User> existingUsers = this.list(query);

        if (!existingUsers.isEmpty()) {
            // 存在重复的用户，返回已存在的用户列表
            System.out.println("Existing users: " + existingUsers);
            return existingUsers;
        }

        // 所有用户都不存在，进行批量导入
        for (User user : users) {
            // 密码加密
            try {
                user.setPassword(AesUtil.encrypt(user.getPassword()));
            } catch (Exception e) {
                System.out.println("Password encryption failed: " + e.getMessage());
                throw new RuntimeException("密码加密失败: " + e.getMessage(), e);
            }
            user.setCreateTime(new Date());
            this.save(user);

            // 保存用户角色关联
            UserRole userRole = new UserRole();
            userRole.setUserId(user.getUserId());
            userRole.setRoleId(user.getRoleId());
            userRoleService.save(userRole);
        }

        System.out.println("Users imported successfully.");
        return new ArrayList<>(); // 返回空列表表示没有已存在的用户
    }


//    @Override
//    @Transactional
//    public List<User> importUsers(List<User> users) {
//        // 检查所有用户是否都不存在
//        List<String> usernames = new ArrayList<>();
//        for (User user : users) {
//            usernames.add(user.getUsername());
//        }
//
//        QueryWrapper<User> query = new QueryWrapper<>();
//        query.lambda().in(User::getUsername, usernames);
//        List<User> existingUsers = this.list(query);
//
//        if (!existingUsers.isEmpty()) {
//            // 存在重复的用户，返回已存在的用户列表
//            return existingUsers;
//        }
//
//        // 所有用户都不存在，进行批量导入
//        for (User user : users) {
//            // 密码加密
//            try {
//                user.setPassword(AesUtil.encrypt(user.getPassword()));
//            } catch (Exception e) {
//                throw new RuntimeException("密码加密失败: " + e.getMessage(), e);
//            }
//            user.setCreateTime(new Date());
//            this.save(user);
//        }
//
//        return new ArrayList<>(); // 返回空列表表示没有已存在的用户
//    }
}
