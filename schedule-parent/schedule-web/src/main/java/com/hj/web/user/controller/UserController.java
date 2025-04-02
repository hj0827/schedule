package com.hj.web.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hj.utils.AesUtil;
import com.hj.utils.ResultUtils;
import com.hj.utils.ResultVo;
import com.hj.web.role.entity.Role;
import com.hj.web.schedule.entity.SelectOption;
import com.hj.web.user.entity.PageParm;
import com.hj.web.user.entity.User;
import com.hj.web.user.service.UserService;
import com.hj.web.role.service.RoleService; // 导入 RoleService
import com.hj.web.user_role.entity.UserRole;
import com.hj.web.user_role.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;


@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserRoleService userRoleService;

    //    新增
    @PostMapping
    public ResultVo add(@RequestBody User user) {
        System.out.println("adddata"+user);
        QueryWrapper<User> query = new QueryWrapper<>();
        query.lambda().eq(User::getUsername, user.getUsername());

        try {
            // 密码加密
            user.setPassword(AesUtil.encrypt(user.getPassword()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error("密码加密失败");
        }

        user.setCreateTime(new Date());
        User one = userService.getOne(query);
        if (one != null) {
            return ResultUtils.error("用户名被占用");
        }
        userService.addUser(user);
        return ResultUtils.success("新增成功");
    }

    //    编辑
    @PutMapping
    public ResultVo edit(@RequestBody User user) {
        System.out.println("editdata"+user);
        QueryWrapper<User> query = new QueryWrapper<>();
        query.lambda().eq(User::getUsername, user.getUsername());
        user.setUpdateTime(new Date());

        // 检查用户名是否被占用
        User one = userService.getOne(query);
        if (one != null && one.getUserId() != user.getUserId()) {
            return ResultUtils.error("用户名被占用");
        }

        // 如果用户提供了新密码，则进行加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            try {
                user.setPassword(AesUtil.encrypt(user.getPassword()));
            } catch (Exception e) {
                e.printStackTrace();
                return ResultUtils.error("密码加密失败");
            }
        }

        userService.editUser(user);
        return ResultUtils.success("编辑成功");
    }


    private String encryptPassword(String password) {
        // 实现你的加密逻辑
        return password; // 示例加密逻辑
    }

    private Long getUserId() {
        // 实现获取当前用户ID的逻辑
        return 1L; // 示例用户ID
    }

    @PutMapping("/updateUser")
    public ResultVo updatePassword(@RequestBody User user) {
        System.out.println("普通用户修改密码: " + user);
        Long userId = user.getUserId();
        String newPassword = encryptPassword(user.getPassword());
        try {
            userService.updatePassword(userId, newPassword);
            return ResultUtils.success("密码更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error("密码更新失败: " + e.getMessage());
        }
    }



    //    删除
    @DeleteMapping("/{userId}")
    public ResultVo delete(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
        return ResultUtils.success("删除成功");
    }

    //    列表查询
    @GetMapping("/list")
    public ResultVo getList(PageParm parm) {
        IPage<User> list = userService.getUserList(parm);
        return ResultUtils.success("查询成功", list);
    }

    // 角色列表查询
    @GetMapping("/role")
    public ResultVo getRole() {
        List<Role> list = roleService.list();
        List<SelectOption> selectOptions = new ArrayList<>();
        Optional.ofNullable(list).orElse(new ArrayList<>())
                .stream()
                .forEach(item -> {
                    SelectOption option = new SelectOption();
                    option.setValue(item.getRoleId()); // 直接设置 Long 类型
                    option.setLabel(item.getRoleName());
                    selectOptions.add(option);
                });
        return ResultUtils.success("查询成功", selectOptions);
    }

    //    根据id查询用户信息
    @GetMapping("/getUser")
    public ResultVo getUserById(Long userId) {
        User user = userService.getById(userId);

        // 查角色id
        QueryWrapper<UserRole> query = new QueryWrapper<>();
        query.lambda().eq(UserRole::getUserId, userId);
        UserRole one = userRoleService.getOne(query);
        user.setRoleId(one.getRoleId());

        // 解密密码
        try {
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                user.setPassword(AesUtil.decrypt(user.getPassword()));
            } else {
                user.setPassword(null); // 或者设置一个默认值
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error("密码解密失败");
        }

        return ResultUtils.success("查询成功", user);
    }


    @PostMapping("/importUser")
    public ResultVo importUser(@RequestBody List<User> users) {
        System.out.println("data");
        System.out.println(users);
        try {
            List<User> existingUsers = userService.importUsers(users);
            if (existingUsers.isEmpty()) {
                // 所有用户都成功导入
                return ResultUtils.success("用户导入成功");
            } else {
                // 部分用户已存在
                String message = "已存在" + existingUsers.size()+"个用户";
                return ResultUtils.error(message, existingUsers);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error("用户导入失败: " + e.getMessage());
        }
    }


}
