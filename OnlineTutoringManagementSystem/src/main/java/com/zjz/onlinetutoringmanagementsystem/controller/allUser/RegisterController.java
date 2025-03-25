package com.zjz.onlinetutoringmanagementsystem.controller.allUser;

import com.zjz.enums.UserRole;
import com.zjz.onlinetutoringmanagementsystem.service.IParentsService;
import com.zjz.onlinetutoringmanagementsystem.service.IStudentsService;
import com.zjz.onlinetutoringmanagementsystem.service.ITeachersService;
import com.zjz.onlinetutoringmanagementsystem.service.IUsersService;
import com.zjz.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    IUsersService usersService;

    @Autowired
    IParentsService parentsService;

    @Autowired
    IStudentsService studentsService;

    @Autowired
    ITeachersService teachersService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /*
        家长注册
        验证后更新两张表
     */
    @PostMapping("/parent")
    Result parentRegister(@RequestBody Users user) {
        log.info("parentRegister");
        log.info("user:{}",user);
        if (user.getRole() != UserRole.parent){
            throw new IllegalArgumentException("非法输入");
        }
        //调用注册方法并返回注册方法的返回值
        return UsersRegister(user);
    }

    /*
        学生注册时需要提交已存在的家长ID以及用户名
        验证后更新两张表
    */
    @PostMapping("/student")
    Result studentRegister(@RequestBody Users user, @RequestParam(required = false) String parentUserName, @RequestParam(required = false) Integer parentId) {
        /*
        验证请求是否为空,
        验证请求是否完整,
        验证家长信息是否为空,
        以及家长信息是否正确
         */
        log.info("studentRegister");
        if (parentId == null || parentUserName == null || parentUserName.isEmpty()
        ) {
            return Result.error("家长信息不完整");
        } else if (!ParentCheck(parentUserName,parentId)) {
            return Result.error("家长信息不正确");
        }else if (user.getRole() != UserRole.student){
            return Result.error("非法输入");
        }

        /*
        调用注册方法并返回注册方法的返回值
         */
        return UsersRegister(user , parentUserName , parentId);
    }

    /*
        家长注册
        验证后更新两张表
     */
    @PostMapping("/teacher")
    Result teacherRegister(@RequestBody Users user) {
        log.info("teacherRegister");

        if (user.getRole() != UserRole.teacher){
            //return Result.error("非法输入");
            throw new IllegalArgumentException("非法输入");
        }
        /*
        调用注册方法并返回注册方法的返回值
         */
        return UsersRegister(user);
    }


    /*
        教师和家长更新user注册方法
    */
    public Result UsersRegister(Users user) {
        log.info("UsersRegister");
        //校验用户名与密码
        user = validateAndPrepareUser(user);
        usersService.register(user);
        user.setUserId(usersService.selectIdByUserName(user.getUsername()));
        RoleRegister(ChangeToRole(user, user.getRole()), user.getRole());
        return Result.success("注册成功");
    }

    /*
        学生更新user注册方法
    */
    public Result UsersRegister(Users user , String parentName, Integer parentId) {
        log.info("UsersRegister(student)");

        if (!ParentCheck(parentName,parentId)){
            return Result.error("家长信息错误");
        }
        //校验用户名与密码
        user = validateAndPrepareUser(user);
        //插入到user表
        usersService.register(user);

        //将注册后自增生成的ID加入到原user中，以免插入角色表时无用户ID
        user.setUserId(usersService.selectIdByUserName(user.getUsername()));

        //先转换user为学生对象,然后更新角色表,将家长基本信息存入，同时更新两张表完成绑定
        Students student = (Students) ChangeToRole(user,user.getRole());
        student.setParentUserId(parentId);

        if (RoleRegister(student,user.getRole())){
            return Result.success("注册成功");
        }else {
            return Result.error("未知错误，请联系管理员");
        }
    }
    /*
    更新角色注册方法
     */
    public Boolean RoleRegister(Object obj, UserRole userRole) {

        log.info("RoleRegister");
        //根据角色插入角色表
        switch (userRole) {
            case student:
                //插入学生表的同时更新家长表
                studentsService.register((Students) obj);
                Parents parent = new Parents();
                parent.setStudentUserId(((Students) obj).getStudentUserId());
                parent.setParentUserId(((Students) obj).getParentUserId());
                parentsService.updateById(parent);

                break;
            case parent:
                parentsService.register((Parents) obj);
                break;
            case teacher:
                teachersService.register((Teachers) obj);
                break;
            default:
                return false;
        }
        return true;
    }

    /*
    根据角色将User对象内属性,转换至角色对象对应属性,返回角色对象
     */
    public Object ChangeToRole(Users user, UserRole userRole){
        log.info("ChangeToRole");
        //转换为家长
        if (userRole == UserRole.parent){
            log.info("转换为家长");
            Parents parent = new Parents();
            parent.setParentUsername(user.getUsername());
            parent.setParentUserId(user.getUserId());
            return parent;
        }
        //转换为学生
        else if (userRole == UserRole.student){
            log.info("转换为学生");
            Students student = new Students();
            student.setStudentUsername(user.getUsername());
            student.setStudentUserId(user.getUserId());
            return student;
        }
        //转换为老师
        else if (userRole == UserRole.teacher){
            log.info("转换为老师");
            Teachers teachers = new Teachers();
            teachers.setTeacherUsername(user.getUsername());
            teachers.setTeacherUserId(user.getUserId());
            return teachers;
        }else{
            log.info("转换失败");
            return user;
        }
    }

    //检查学生注册时发送的家长信息是否无误
    public Boolean ParentCheck(String parentUserName, Integer parentId){
        log.info("ParentCheck");

        Parents parent = parentsService.getById(parentId);
        if (parent==null){
            return false;
        }
        //家长已有学生ID时也是返回错误
        if (parent.getStudentUserId()!=null){
            return false;
        }
        return parent.getParentUsername().equals(parentUserName);
    }

    //用户名检查和密码加密
    private Users validateAndPrepareUser(Users user) {
        if (usersService.selectIdByUserName(user.getUsername()) != null) {
            throw new IllegalArgumentException("用户名被占用");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return user;
    }

}
