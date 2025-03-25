package com.zjz.onlinetutoringmanagementsystem;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjz.enums.UserRole;
import com.zjz.onlinetutoringmanagementsystem.mapper.AssignmentMapper;
import com.zjz.onlinetutoringmanagementsystem.mapper.TeachersMapper;
import com.zjz.pojo.Course;
import com.zjz.pojo.Parents;
import com.zjz.pojo.Users;
import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.onlinetutoringmanagementsystem.service.*;
import com.zjz.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;



@Slf4j
@SpringBootTest
class OnlineTutoringManagementSystemApplicationTests {
    @Autowired
    IUsersService usersService;

    @Autowired
    IParentsService parentsService;

    @Autowired
    IStudentsService studentsService;

    @Autowired
    ITeachersService teachersService;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    ICourseService courseService;

    @Autowired
    TeachersMapper teachersMapper;

    @Autowired
    AssignmentMapper assignmentMapper;

    @Autowired
    IFileInfoService fileInfoService;

    @Autowired
    private DataSource dataSource;



    @Test
    void contextLoads() {

        Users user = new Users();
        user.setRole(UserRole.teacher);
        System.out.println(user.getRole().getDesc());

    }

    @Test
    //更新测试
    void UPDATEP(){
        Parents parent = new Parents();
        parent.setParentUserId(3);
        parent.setStudentUserId(10);
        parentsService.updateById(parent);
    }



    @Test
    public void TestEncode(){

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        System.out.println(passwordEncoder.encode("123456"));
        System.out.println(passwordEncoder.matches("123456","$2a$10$K6QoWoXdiIoUlUgOIYRpRuxwO3GcmOfeh/OnIa7e5YDWk2rSuDyF6"));

    }

    @Test
    public void TestJwt(){
        Map<String , Object> claims = new HashMap<>();
        claims.put("id",2);
        claims.put("username","Username");
        claims.put("role","Role");

        String jwt = JwtUtils.generateJwt(claims);//jwt包含当前登录用户或管理员信息

        Claims claims1 = JwtUtils.parseJWT(jwt);

        System.out.println( claims1.get("username"));
    }

    @Test
    void testPageQuery() {
        PageQuery query = new PageQuery();
        query.setPageNo(2L);
        query.setPageSize(2L);
        Page<Course> page = query.toMpPage();
        // 1.分页查询，new Page()的两个参数分别是：页码、每页大小
        Page<Course> c = courseService.page(page);
        // 2.总条数
        System.out.println("total = " + c.getTotal());
        // 3.总页数
        System.out.println("pages = " + c.getPages());
        // 4.数据
        List<Course> records = c.getRecords();
        records.forEach(System.out::println);
    }


}

