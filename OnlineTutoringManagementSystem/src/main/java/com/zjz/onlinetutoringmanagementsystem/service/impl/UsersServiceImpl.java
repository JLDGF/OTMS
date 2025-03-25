package com.zjz.onlinetutoringmanagementsystem.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjz.enums.UserRole;
import com.zjz.onlinetutoringmanagementsystem.mapper.ParentsMapper;
import com.zjz.onlinetutoringmanagementsystem.mapper.StudentsMapper;
import com.zjz.onlinetutoringmanagementsystem.mapper.TeachersMapper;
import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.onlinetutoringmanagementsystem.service.IParentsService;
import com.zjz.onlinetutoringmanagementsystem.service.IStudentsService;
import com.zjz.onlinetutoringmanagementsystem.service.ITeachersService;
import com.zjz.pojo.*;
import com.zjz.onlinetutoringmanagementsystem.mapper.UsersMapper;
import com.zjz.onlinetutoringmanagementsystem.service.IUsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjz.utils.JwtUtils;

import com.zjz.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zjz
 * @since 2024-12-08
 */
@Slf4j
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements IUsersService {

    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private StudentsMapper studentsMapper;
    @Autowired
    private ParentsMapper parentsMapper;
    @Autowired
    private TeachersMapper teachersMapper;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private MyUserDetailsServiceImpl userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private IParentsService parentsService;
    @Autowired
    private IStudentsService studentsService;
    @Autowired
    private ITeachersService teachersService;


    @Override
    public Integer selectIdByUserName(String userName) {
        return usersMapper.selectIdByUserName(userName);
    }

    @Override
    public Users selectByUsername(String userName) {
        return usersMapper.selectByUsername(userName);
    }

    @Override
    public Users getUserCurrent() {
        log.info("GetUserCurrent Now");
        // 获取当前的安全上下文
        SecurityContext securityContext = SecurityContextHolder.getContext();
        // 从安全上下文中获取认证信息
        Authentication authentication = securityContext.getAuthentication();

        // 获取用户详情（实现了 UserDetails 接口的对象）
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            log.info("Current Get");

            CustomerUserDetails userDetails = (CustomerUserDetails) principal;

            // 获取用户名
            String username = userDetails.getUsername();

            // 获取用户角色
            String role = null;
            if (!userDetails.getAuthorities().isEmpty()) {
                // 假设角色是单一的，只取第一个角色
                role = userDetails.getAuthorities().iterator().next().getAuthority();
            }

            // 创建一个 Users 对象 将用户名和角色注入
            Users u = new Users();
            u.setUsername(username);
            switch (role) {
                case "ROLE_student":
                    u.setRole(UserRole.student);
                    break;
                case "ROLE_parent":
                    u.setRole(UserRole.parent);
                    break;
                case "ROLE_teacher":
                    u.setRole(UserRole.teacher);
                    break;
                case "ROLE_admin":
                    u.setRole(UserRole.admin);
                    break;
            }

            log.info("user:{}", u);
            return u;
        } else {
            log.info("Current Fail");
            // 如果 principal 不是 UserDetails 实例，则可能是其他类型的对象（如 String 用户名）
            System.out.println("Principal is not an instance of UserDetails: " + principal);
            return null;
        }

    }


    @Override
    public void register(Users user) {
        /*
        验证请求是否为空
         */
        if (ObjectUtils.isEmpty(user)) {
            //return Result.error("请求为空");
            throw new IllegalArgumentException("请求为空");
        } else if (
                user.getUsername().isEmpty() || user.getUsername() == null ||
                        user.getPassword().isEmpty() || user.getPassword() == null ||
                        user.getRole() == null
        ) {
            //return Result.error("注册信息不完整");
            throw new IllegalArgumentException("注册信息不完整");
        }

        usersMapper.insert(user);
    }

    @Override
    public Result login(Users user) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );
        } catch (AuthenticationException e) {
            //登录失败时返回错误信息
            return Result.error("用户名或密码错误");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

        Map<String, Object> claims = new HashMap<>();

        claims.put("username", userDetails.getUsername());
        claims.put("role", userDetails.getAuthorities());

        String jwt = JwtUtils.generateJwt(claims);//jwt包含当前登录用户或管理员信息

        return Result.success(jwt);
    }

    //管理员用户功能
    @Override
    @Transactional
    public Result adminAddUser(Users user) {

        if (user.getUserId() != null) {
            return Result.error("非法数据");
        }
        // 检查用户名唯一性
        QueryWrapper<Users> query = new QueryWrapper<>();
        query.eq("username", user.getUsername());
        if (usersMapper.exists(query)) {
            return Result.error("用户名已存在");
        }

        // 检查密码是否需要加密（假设前端可能传递明文）
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        log.info("user :{}", user);

        try {
            usersMapper.insert(user);
            updateRoleTable(user);
            return Result.success();
        } catch (Exception e) {
            log.error("添加用户失败: {}", e.getMessage());
            return Result.error("操作失败，请检查数据合法性");
        }
    }

    @Override
    public Result adminDeletedUser(Integer userId) {

        log.info("userId :{}", userId);
        if (userId == null) {
            return Result.error("非法数据");
        }

        if (userId.equals(usersMapper.selectIdByUserName(SecurityUtils.getCurrentUsername()))) {
            return Result.error("不能删除自己的账户");
        }
        Users users = usersMapper.selectById(userId);
        UserRole role = users.getRole();


        try {
            usersMapper.deleteById(userId);
            if (role.equals(UserRole.student)){
                studentsMapper.deleteById(userId);
            }else if (role.equals(UserRole.parent)){
                parentsMapper.deleteById(userId);
            }else if (role.equals(UserRole.teacher)){
                teachersMapper.deleteById(userId);
            }

        } catch (Exception e) {
            return Result.error("error:" + e);
        }
        return Result.success();
    }

    @Override
    public Result adminUpdateUser(Map<String, Object> userMap) {
        try {
            // 1. 提取基础用户信息
            Users user = new Users();
            user.setUserId(Integer.parseInt(userMap.get("userId").toString()));
            user.setUsername((String) userMap.get("username"));
            user.setEnabled(Boolean.parseBoolean(userMap.get("enabled").toString()));

            // 2. 密码处理
            if (userMap.containsKey("password") && !((String) userMap.get("password")).isEmpty()) {
                String password = (String) userMap.get("password");
                user.setPassword(passwordEncoder.encode(password));
            }

            // 3. 获取原始用户数据
            Users existingUser = usersMapper.selectById(user.getUserId());
            user.setRole(existingUser.getRole()); // 保持原角色不变

            // 4. 更新用户表
            usersMapper.updateById(user);

            // 5. 更新角色扩展表
            updateRoleSpecificTables(user.getUserId(), userMap);

            return Result.success();
        } catch (Exception e) {
            log.error("更新用户失败: {}", e.getMessage());
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    @Override
    public Result adminGetAllUser(Map<String, Object> request) {
        // 提取分页信息
        Object queryObject = request.getOrDefault("query", new HashMap<>());
        Map<String, Object> queryMap;
        if (queryObject instanceof Map<?, ?>) {
            queryMap = (Map<String, Object>) queryObject;
        } else {
            queryMap = new HashMap<>();
        }

        PageQuery query = new PageQuery();

        // 获取分页信息，确保处理为 Long 类型
        query.setPageNo(convertToLong(queryMap.getOrDefault("pageNo", 1L)));
        query.setPageSize(convertToLong(queryMap.getOrDefault("pageSize", 10L)));
        Page<Users> page = query.toMpPage();

        log.info("admin Get All User");

        // 提取其他查询条件
        String userName = (String) request.getOrDefault("userName", null);
        Long userId = convertToLong(request.get("userId"));
        UserRole userRole = (UserRole.fromDesc((String) request.getOrDefault("userRole", null)));
        Boolean enabled = request.get("enabled") instanceof Boolean ? (Boolean) request.get("enabled") : null;

        // 构建查询条件
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        if (userName != null && !userName.isEmpty()) {
            queryWrapper.like("username", userName);
        }
        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }
        if (userRole != null) {
            queryWrapper.eq("Role", userRole);
        }
        if (enabled != null) {
            queryWrapper.eq("enabled", enabled ? 1 : 0);
        }


        // 执行分页查询
        page(page, queryWrapper);

        return Result.success(ToPage.of(page, Users.class));
    }

    @Override
    public Result getUserDetalisById(Integer userId) {
        UserRole role = usersMapper.selectById(userId).getRole();
        Object obj = new Object();

        if (role.equals(UserRole.student)) {
            obj = studentsService.getById(userId);
        } else if (role.equals(UserRole.teacher)) {
            obj = teachersService.getById(userId);
        } else if (role.equals(UserRole.parent)) {
            obj = parentsService.getById(userId);
        }

        if (obj != null) {
            return Result.success(obj);
        } else {
            return Result.error("用户信息不存在");
        }
    }

    @Override
    public Result adminGetUserRole(LocalDateTime startTime, LocalDateTime endTime) {


        List<Map<String, Object>> dataList = new ArrayList<>();

        QueryWrapper<Students> queryWrapperS = new QueryWrapper<>();
        QueryWrapper<Teachers> queryWrapperT = new QueryWrapper<>();
        QueryWrapper<Parents> queryWrapperP = new QueryWrapper<>();

        queryWrapperS
                .gt("registration_date", startTime)
                .lt("registration_date", endTime);
        queryWrapperT
                .gt("registration_date", startTime)
                .lt("registration_date", endTime);
        queryWrapperP
                .gt("registration_date", startTime)
                .lt("registration_date", endTime);

        Double countS = (double) studentsService.count(queryWrapperS);
        Double countP = (double) parentsService.count(queryWrapperP);
        Double countT = (double) teachersService.count(queryWrapperT);

        Double countAll = countS + countP + countT;

        // 创建第一个 Map 对象
        Map<String, Object> studentMap = new HashMap<>();
        studentMap.put("type", "学生");
        studentMap.put("count", countS);
        studentMap.put("proportion", countS/countAll);
        dataList.add(studentMap);

        // 创建第二个 Map 对象
        Map<String, Object> teacherMap = new HashMap<>();
        teacherMap.put("type", "教师");
        teacherMap.put("count", countT);
        teacherMap.put("proportion", countT/countAll);
        dataList.add(teacherMap);

        // 创建第三个 Map 对象
        Map<String, Object> parentMap = new HashMap<>();
        parentMap.put("type", "家长");
        parentMap.put("count", countP);
        parentMap.put("proportion", countP/countAll);
        dataList.add(parentMap);

        return Result.success(dataList);

    }

    @Override
    public Result adminGetNewUser(LocalDateTime startTime, LocalDateTime endTime) {

        List<Map<String, Object>> dataList = new ArrayList<>();

        // 计算开始时间和结束时间之间的天数
        long daysBetween = ChronoUnit.DAYS.between(startTime.toLocalDate(), endTime.toLocalDate());

        // 遍历每一天
        for (int i = 0; i < daysBetween; i++) {
            // 当前遍历的日期
            LocalDate currentDate = startTime.toLocalDate().plusDays(i);
            LocalDateTime currentStart = LocalDateTime.of(currentDate, LocalTime.MIDNIGHT);
            LocalDateTime currentEnd = currentStart.plusDays(1);

            // 查询条件
            QueryWrapper<Students> queryWrapperS = new QueryWrapper<>();
            QueryWrapper<Teachers> queryWrapperT = new QueryWrapper<>();
            QueryWrapper<Parents> queryWrapperP = new QueryWrapper<>();

            queryWrapperS
                    .ge("registration_date", currentStart)
                    .lt("registration_date", currentEnd);
            queryWrapperT
                    .ge("registration_date", currentStart)
                    .lt("registration_date", currentEnd);
            queryWrapperP
                    .ge("registration_date", currentStart)
                    .lt("registration_date", currentEnd);

            // 统计数量
            long countS = studentsService.count(queryWrapperS);
            long countT = teachersService.count(queryWrapperT);
            long countP = parentsService.count(queryWrapperP);

            long countAll = countS + countT + countP;

            // 创建一个 Map 存储当前日期的数据
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("date", currentDate.format(DateTimeFormatter.ISO_DATE)); // 日期格式为 "yyyy-MM-dd"
            dataMap.put("count", countAll); // 当天新增用户总数

            // 将当前日期的数据添加到 dataList
            dataList.add(dataMap);
        }

        return Result.success(dataList);
    }

    // 辅助方法：将参数转换为 Long 类型
    private Long convertToLong(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Integer) {
            return ((Integer) obj).longValue(); // 将 Integer 转换为 Long
        }
        if (obj instanceof Long) {
            return (Long) obj; // 已经是 Long，直接返回
        }
        if (obj instanceof String) {
            try {
                return Long.parseLong((String) obj); // 尝试将 String 转换为 Long
            } catch (NumberFormatException e) {
                // 如果 String 无法解析为数字，返回 null
                return null;
            }
        }
        return null; // 如果是其他类型（例如 Float 或其他非数字类型），返回 null
    }

    private boolean validateUser(Users user) {
        if (user.getUsername() == null || user.getUsername().length() < 2) {
            throw new IllegalArgumentException("用户名长度需大于2");
        }
        if (!user.getRole().toString().matches("admin|teacher|student|parent")) {
            throw new IllegalArgumentException("非法角色类型");
        }
        return true;
    }

    private boolean updateRoleTable(Users user) {
        log.info("updateRoleTable");
        // 根据角色插入角色表
        switch (user.getRole()) {
            case student:
                Students student = new Students();
                student.setStudentUsername(user.getUsername());
                student.setStudentUserId(user.getUserId());
                studentsService.register(student);
                break;
            case parent:
                Parents parent = new Parents();
                parent.setParentUsername(user.getUsername());
                parent.setParentUserId(user.getUserId());
                parentsService.register(parent);
                break;
            case teacher:
                Teachers teacher = new Teachers();
                teacher.setTeacherUsername(user.getUsername());
                teacher.setTeacherUserId(user.getUserId());
                teachersService.register(teacher);
                break;
            default:
                return false;
        }
        return true;
    }

    //根据角色更新信息
    private void updateRoleSpecificTables(Integer userId, Map<String, Object> userMap) {
        Users user = usersMapper.selectById(userId);
        switch (user.getRole()) {
            case student:
                Students student = studentsService.getById(userId);
                student.setGrade((String) userMap.get("grade"));
                student.setRating((Integer) userMap.get("rating"));
                student.setParentUserId((Integer) userMap.get("parentUserId"));
                studentsService.updateById(student);
                break;
            case teacher:
                Teachers teacher = teachersService.getById(userId);
                teacher.setAge((Integer) userMap.get("age"));
                teacher.setRating((Integer) userMap.get("rating"));
                teacher.setEducationInfo((String) userMap.get("educationInfo"));
                Object settlementPriceObj = userMap.get("settlementPrice");
                if (settlementPriceObj != null) {
                    try {
                        BigDecimal settlementPrice = parseToBigDecimal(settlementPriceObj);
                        teacher.setSettlementPrice(settlementPrice);
                    } catch (NumberFormatException e) {
                        log.error("settlementPrice 格式错误: {}", settlementPriceObj);
                    }
                }
                teachersService.updateById(teacher);
                break;
            case parent:
                Parents parent = parentsService.getById(userId);
                parent.setStudentUserId((Integer) userMap.get("studentUserId"));
                Object confirmPriceObj = userMap.get("confirmPrice");
                if (confirmPriceObj != null) {
                    try {
                        BigDecimal confirmPrice = parseToBigDecimal(confirmPriceObj);
                        parent.setConfirmPrice(confirmPrice);
                    } catch (NumberFormatException e) {
                        log.error("settlementPrice 格式错误: {}", confirmPriceObj);
                    }
                }
                parentsService.updateById(parent);
                break;
        }

    }

    private BigDecimal parseToBigDecimal(Object value) {
        if (value == null) return null;
        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        } else if (value instanceof String) {
            return new BigDecimal((String) value);
        }
        throw new NumberFormatException("无法转换类型: " + value.getClass().getName());
    }

    // 安全获取Integer字段
    private Integer getIntegerSafely(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return (value instanceof Number) ? ((Number) value).intValue() : null;
    }

    // 安全获取String字段
    private String getStringSafely(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return (value != null) ? value.toString() : null;
    }
}
