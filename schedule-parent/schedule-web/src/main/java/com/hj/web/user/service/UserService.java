package com.hj.web.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hj.web.user.entity.PageParm;
import com.hj.web.user.entity.User;

import java.util.List;

public interface UserService extends IService<User> {
    //    新增用户
    void addUser(User user);

    //    编辑用户
    void editUser(User user);

    //    删除
    void deleteUser(Long userId);
    void updatePassword(Long userId, String newPassword);

    //    列表
    IPage<User> getUserList(PageParm parm);

    //  导入
    List<User> importUsers(List<User> users);

}
