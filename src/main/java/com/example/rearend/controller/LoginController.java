package com.example.rearend.controller;


import com.example.rearend.model.User;
import com.example.rearend.utils.ResultUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @PostMapping("/login")
    public ResultUtil<String> login(@RequestBody User user) {
        if (user.getUsername().equals("deepreads")&&user.getPassword().equals("sxjy8888")) {
            return ResultUtil.success();
        } else {
            return ResultUtil.error("登录失败！");
        }
    }


}