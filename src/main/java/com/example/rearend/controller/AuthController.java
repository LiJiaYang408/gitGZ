package com.example.rearend.controller;


import com.example.rearend.model.User;
import com.example.rearend.service.UserService;
import com.example.rearend.utils.JwtUtil;
import com.example.rearend.utils.ResultUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResultUtil login(@RequestBody User user, HttpServletResponse response) {
        if (userService.validateUser(user.getUsername(), user.getPassword())) {
            String token = jwtUtil.generateToken(user.getUsername());
            response.setHeader("Authorization","Bearer"+token);
            return ResultUtil.success();
        } else {
            return ResultUtil.error("登录失败！");
        }
    }

    @PostMapping("/Sign")
    public ResultUtil login(@RequestBody User user){
        User flag=userService.findByUsername(user.getUsername());
        if (flag!=null){
            userService.validateUser(user.getUsername(), user.getPassword());
            return ResultUtil.success();
        }else {
            return ResultUtil.error("用户已存在");
        }
    }

}