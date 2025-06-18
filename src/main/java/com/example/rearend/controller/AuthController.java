package com.example.rearend.controller;


import com.example.rearend.model.User;
import com.example.rearend.service.UserService;
import com.example.rearend.utils.ResultUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    // 构造函数注入
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResultUtil<String> login(@RequestBody User user) {
        if (userService.validateUser(user.getUsername(), user.getPassword())) {
            return ResultUtil.success();
        } else {
            return ResultUtil.error("登录失败！");
        }
    }

    @PostMapping("/Sign")
    public ResultUtil<String> Sign(@RequestBody User user){
        User flag=userService.findByUsername(user.getUsername());
        if (flag!=null){
            userService.validateUser(user.getUsername(), user.getPassword());
            return ResultUtil.success();
        }else {
            return ResultUtil.error("用户已存在");
        }
    }

}