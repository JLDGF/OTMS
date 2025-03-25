package com.zjz.onlinetutoringmanagementsystem.controller.admin;

import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.onlinetutoringmanagementsystem.service.IUsersService;
import com.zjz.pojo.Result;
import com.zjz.pojo.Users;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/user")
public class adminUserController {

    @Autowired
    IUsersService usersService ;

    @PostMapping("/addUser")
    public Result addUser(@RequestBody Users user) {
        return usersService.adminAddUser(user);
    }

    @DeleteMapping("/deleteUser")
    public Result deleteUser(Integer userId){
        return usersService.adminDeletedUser(userId);
    }

    @PutMapping("/updateUser")
    public Result updateUser(@RequestBody Map<String, Object> userMap) {
        return usersService.adminUpdateUser(userMap);
    }

    @GetMapping("/getUserDetalis")
    public Result getUserDetalis(Integer userId){
        return usersService.getUserDetalisById(userId);
    }

    @PostMapping("/getAllUser")
    public Result getAllUser(@RequestBody Map<String, Object> request){
        return usersService.adminGetAllUser(request);
    }

}
