package com.hj.web.login.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hj.jwt.JwtUtils;
import com.hj.utils.AesUtil;
import com.hj.utils.ResultUtils;
import com.hj.utils.ResultVo;
import com.hj.web.login.entity.LoginParm;
import com.hj.web.login.entity.LoginResult;

import com.hj.web.login.entity.MenuResult;
import com.hj.web.login.entity.RouterVO;
import com.hj.web.menu.entity.MakeTree;
import com.hj.web.menu.entity.Menu;
import com.hj.web.menu.service.MenuService;
import com.hj.web.user.entity.User;
import com.hj.web.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/login")
public class LoginController {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserService userService;
    @Autowired
    private MenuService menuService;

    @PostMapping("/login")
    public ResultVo login(@RequestBody LoginParm loginParm) {
        if (StringUtils.isEmpty(loginParm.getUsername()) || StringUtils.isEmpty(loginParm.getPassword())) {
            return ResultUtils.error("用户名或密码不能为空");
        }

        try {
            // 对输入的密码进行AES加密
            String encryptedPassword = AesUtil.encrypt(loginParm.getPassword());
            System.out.println("加密后的密码：" + encryptedPassword);
            // 查询用户
            QueryWrapper<User> query = new QueryWrapper<>();
            query.lambda()
                    .eq(User::getUsername, loginParm.getUsername())
                    .eq(User::getPassword, encryptedPassword);
            User user = userService.getOne(query);

            if (user == null) {
                return ResultUtils.error("用户名或密码错误");
            }

            // 生成token
            Map<String, String> map = new HashMap<>();
            map.put("username", user.getUsername());
            map.put("userId", Long.toString(user.getUserId()));
            String token = jwtUtils.generateToken(map);

            // 构造返回值
            LoginResult result = new LoginResult();
            result.setUserId(user.getUserId());
            result.setToken(token);
            result.setUsername(user.getUsername());
            result.setName(user.getName());

            return ResultUtils.success("登录成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error("系统错误");
        }
    }

    @GetMapping("/getMenuList")
    public ResultVo getMenuList(Long userId){
        List<Menu> menuList = menuService.getMenuByUserId(userId);
        if(menuList.size() < 1){
            return ResultUtils.error("您暂无菜单权限，请联系系统管理员");
        }
        List<Menu> collect = menuList.stream().filter(item -> item != null && !item.getType().equals("2"))
                .collect(Collectors.toList());
//        组装路由数据
        List<RouterVO> router = MakeTree.makeRouter(collect,0L);
//        权限字段
        Object[] array = menuList.stream().filter(item -> item.getCode() != null).map(item -> item.getCode()).toArray();
//      返回
        MenuResult result = new MenuResult();
        result.setAuthList(array);
        result.setMenuList(router);
        return ResultUtils.success("查询成功",result);
    }
}
