package com.zjz.onlinetutoringmanagementsystem.controller.allUser;

import com.zjz.onlinetutoringmanagementsystem.service.IUsersService;
import com.zjz.pojo.Result;
import com.zjz.pojo.Users;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
public class LoginController {

    @Autowired
    IUsersService usersService;

    @PostMapping("/user/login")
    public Result login(@RequestBody Users user){
        log.info("登录：{}",user);
        return usersService.login(user);
    }


}
