package com.zjz.onlinetutoringmanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjz.enums.CourseStatus;
import com.zjz.onlinetutoringmanagementsystem.mapper.*;
import com.zjz.onlinetutoringmanagementsystem.service.*;
import com.zjz.pojo.*;
import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjz.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zjz
 * @since 2025-01-04
 */
@Slf4j
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private StudentcoursemappingMapper scmappingMapper;

    @Autowired
    private TeachercoursemappingMapper tcmappingMapper;

    @Autowired
    private ITeachersService teachersService;

    @Autowired
    private IStudentsService studentsService;

    @Autowired
    private IParentsService parentsService;

    @Autowired
    private LevelRulesMapper levelRulesMapper;

    @Autowired
    private EnrollmentMapper enrollmentMapper;


    //分页查询课程
    @Override
    public ToPage<Course> queryCoursePage(PageQuery query, String courseName, String subject) {
        log.info("queryCoursePage Now");

        // 1. 条件构造
        Page<Course> page = query.toMpPage();
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();

        //2. 增加条件
        if (courseName != null && !courseName.isEmpty()) {
            queryWrapper.like("course_name", courseName);  // 模糊查询课程名称
        }
        if (subject != null && !subject.isEmpty()) {
            queryWrapper.eq("subject", subject);  // 精确查询科目
        }

        //已结束的不查询
        queryWrapper.and(Wrapper -> Wrapper.eq("course_status",CourseStatus.IN_PROGRESS)
                .or()
                .eq("course_status",CourseStatus.NOT_STARTED));

        // 3. 查询，传入分页和查询条件
        page(page, queryWrapper);

        // 4. 返回数据
        return ToPage.of(page, Course.class);
    }

    //教师分页查询自己的课程
    @Override
    public ToPage<Course> queryCoursePageByTeacherUsername(PageQuery query) throws Exception {
        log.info("queryCoursePage Now");
        // 1. 条件构造
        Page<Course> page = query.toMpPage();  // 将 PageQuery 转换为 MyBatis-Plus Page 对象
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();  // 创建查询条件包装器
        String username = SecurityUtils.getCurrentUsername();

        if (username == null || username.isEmpty()) {
            throw new Exception("异常，请联系管理员");
        }
        //2.获取id
        Integer id = teachersService.getByUsername(username).getTeacherUserId();
        queryWrapper.eq("teacher_id", id);  // 精确查询教师ID

        // 3. 查询，传入分页和查询条件
        page(page, queryWrapper);
        // 4. 返回数据
        return ToPage.of(page, Course.class);
    }

    //学生和家长分页查询学生的课程
    @Override
    public ToPage<Course> queryCoursePageByStudentId(PageQuery query) throws Exception {
        log.info("queryCoursePage Now");

        Page<Course> page = query.toMpPage();  // 将 PageQuery 转换为 MyBatis-Plus Page 对象
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();  // 创建查询条件包装器

        // 1.1根据用户名获取学生ID，
        String username = SecurityUtils.getCurrentUsername();
        String role = SecurityUtils.getCurrentUserRole();
        Integer id;
        log.info("username:{}", username);

        if (username == null || username.isEmpty() || role == null || role.isEmpty()) {
            throw new Exception("账号异常，请联系管理员");
        }
        //1.2获取学生id

        if (role.equals("ROLE_student")) {
            id = studentsService.getByUsername(username).getStudentUserId();
        } else if (role.equals("ROLE_parent")) {
            id = parentsService.getStudentIdByUsername(username);
        } else {
            throw new Exception("账号异常，请联系管理员");
        }

        // 2.查询学生-课程关系表内课程ID，用数组返回课程ID，
        Integer[] cidList = scmappingMapper.getCourseIdByStudentId(id);

        if (cidList.length == 0) {
            return null;
        }


        // 3.根据课程ID分页查询，返回
        //构造查询条件
        queryWrapper.in("course_id", cidList);

        //查询，传入分页和查询条件
        page(page, queryWrapper);

        //返回数据
        return ToPage.of(page, Course.class);
    }

    @Override
    public Result getCourseDetailsCheck(Integer courseId) throws Exception {
        /*
         * 获取用户角色和id
         * 查看匹配表内是否与课程有关联
         * 没有关联抛出无权限
         * 有关联返回课程报名表等
         * */

        // 1.1根据用户名获取ID，
        String username = SecurityUtils.getCurrentUsername();
        String role = SecurityUtils.getCurrentUserRole();
        Integer id = null;
        log.info("username:{}", username);

        if (username == null || username.isEmpty() || role == null || role.isEmpty()) {
            throw new Exception("账号异常，请联系管理员");
        }

        //1.2获取相关id

        switch (role) {
            case "ROLE_student":
                id = studentsService.getByUsername(username).getStudentUserId();
                break;
            case "ROLE_parent":
                id = parentsService.getStudentIdByUsername(username);
                break;
            case "ROLE_teacher":
                id = teachersService.getByUsername(username).getTeacherUserId();
                break;
            default:
                throw new Exception("账号异常，请联系管理员");
        }
        //检测资格
        return checkRelationship(role, id, courseId);
    }

    //新增课程
    @Override
    public Boolean insertCourse(Course course) throws Exception {


        //管理员新增课程时无需校验
        if (Objects.equals(SecurityUtils.getCurrentUserRole(), "ROLE_admin")) {
            try {
                courseMapper.insert(course);
            } catch (Exception e) {
                log.info("教师ID获取错误");
                throw new Exception(e);
            }
            return true;
        }
        //用户新建课程时，教师ID由系统配置
        if (course.getTeacherId() != null) {
            throw new Exception("非法输入");
        }
        //获取用户名
        String username = SecurityUtils.getCurrentUsername();
        log.info("username:{}", username);

        if (username == null || username.isEmpty()) {
            throw new Exception("异常，请联系管理员");
        }
        //校验资格
        Teachers t = teachersService.getByUsername(username);
        if (t.getRating() == 0) {
            throw new Exception("未通过资质校验");
        }

        if (course.getHourlyRate().compareTo(levelRulesMapper.selectById(t.getRating()).getMaxClassFee()) > 0) {
            log.error("课时费超出权限最大课时费");
            return false;
        }

        try {
            //获取id
            Integer id = t.getTeacherUserId();
            //注入
            course.setTeacherId(id);
            courseMapper.insert(course);
        } catch (Exception e) {
            log.info("教师ID获取错误");
            throw new Exception(e);
        }
        return true;
    }

    //删除课程
    @Override
    @Transactional
    public Boolean deleteCourseById(Integer CourseId) {

        String role = SecurityUtils.getCurrentUserRole();

        assert role != null;
        switch (role) {
            case "ROLE_student":
            case "ROLE_parent":
                return false;
            case "ROLE_teacher":
                Course course = courseMapper.selectById(CourseId);
                Integer userId = teachersService.getByUsername(SecurityUtils.getCurrentUsername()).getTeacherUserId();

                if (course.getTeacherId().equals(userId)) {
                    try {
                        //将关联表一并删除
                        QueryWrapper<Enrollment> queryWrapperE = new QueryWrapper<>();
                        QueryWrapper<Teachercoursemapping> queryWrapperTC = new QueryWrapper<>();
                        QueryWrapper<Studentcoursemapping> queryWrapperSC = new QueryWrapper<>();

                        queryWrapperE.eq("course_id", CourseId);
                        queryWrapperTC.eq("course_id", CourseId);
                        queryWrapperSC.eq("course_id", CourseId);

                        enrollmentMapper.delete(queryWrapperE);
                        tcmappingMapper.delete(queryWrapperTC);
                        scmappingMapper.delete(queryWrapperSC);
                        courseMapper.deleteById(CourseId);

                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                }
                break;
            case "ROLE_admin":

                try {
                    //将关联表一并删除
                    QueryWrapper<Enrollment> queryWrapperE = new QueryWrapper<>();
                    QueryWrapper<Teachercoursemapping> queryWrapperTC = new QueryWrapper<>();
                    QueryWrapper<Studentcoursemapping> queryWrapperSC = new QueryWrapper<>();

                    queryWrapperE.eq("course_id", CourseId);
                    queryWrapperTC.eq("course_id", CourseId);
                    queryWrapperSC.eq("course_id", CourseId);

                    enrollmentMapper.delete(queryWrapperE);
                    tcmappingMapper.delete(queryWrapperTC);
                    scmappingMapper.delete(queryWrapperSC);
                    courseMapper.deleteById(CourseId);

                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                return true;

            default:
                return false;
        }
        return false;
    }

    //编辑课程
    @Override
    public Boolean updateCourseById(Course course) {

        try {
            String role = SecurityUtils.getCurrentUserRole();
            assert role != null;
            if (role.equals("ROLE_admin")) {
                courseMapper.updateById(course);
            } else if (role.equals("ROLE_teacher")) {
                Integer id = teachersService.getByUsername(SecurityUtils.getCurrentUsername()).getTeacherUserId();
                Integer CourseTeacherid = courseMapper.selectById(course.getCourseId()).getTeacherId();
                if (CourseTeacherid.equals(id)){
                    courseMapper.updateById(course);
                }else {
                    throw new Exception("非法操作");
                }
            }else {
                throw new Exception("非法操作");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //查询某课程已报名学生
    @Override
    public Result getStudentByMyCourse(Integer courseId) {
        QueryWrapper<Teachercoursemapping> queryWrappertc = new QueryWrapper<>();

        String role = SecurityUtils.getCurrentUserRole();
        assert role != null;
        if (role.equals("ROLE_teacher")) {
            //权限校验
            //获取教师id
            String username = SecurityUtils.getCurrentUsername();
            Integer id = teachersService.getByUsername(username).getTeacherUserId();
            queryWrappertc.eq("teacher_id", id);
            queryWrappertc.eq("course_id", courseId);

            if (!tcmappingMapper.exists(queryWrappertc)) {
                return Result.error("无权限");
            }
            return Result.success(getStudentIdUsernameMap(courseId));
        } else if (role.equals("ROLE_admin")) {
            //管理员直接获取，无需校验
            return Result.success(getStudentIdUsernameMap(courseId));

        }
        return Result.error("未知错误，请联系管理员");
    }

    @Override
    public Result GetMyCourseList() throws Exception {

        // 1.1根据用户名获取ID，
        String username = SecurityUtils.getCurrentUsername();
        String role = SecurityUtils.getCurrentUserRole();
        log.info("username:{}", username);

        if (username == null || username.isEmpty() || role == null || role.isEmpty()) {
            throw new Exception("账号异常，请联系管理员");
        }
        //初始化课程搜索条件
        Integer id = null;
        List<Integer> cidlist = null;

        //1.2获取相关id
        switch (role) {
            case "ROLE_student":
                throw new Exception("预约课程应由教师或家长发起");
            case "ROLE_parent":
                id = parentsService.getStudentIdByUsername(username);
                break;
            case "ROLE_teacher":
                id = teachersService.getByUsername(username).getTeacherUserId();
                break;
            default:
                throw new Exception("账号异常，请联系管理员");
        }
        //家长时，从scmapping获取clist，教师时从tcmapping获取clist，
        if (role.equals("ROLE_parent")) {
            QueryWrapper<Studentcoursemapping> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("student_id", id)
                    .select("course_id");
            //获取课程id列表
            cidlist = scmappingMapper.selectList(queryWrapper)
                    .stream().map(Studentcoursemapping::getCourseId)
                    .collect(Collectors.toList());
        } else {
            QueryWrapper<Teachercoursemapping> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("teacher_id", id)
                    .select("course_id");
            //获取课程id列表
            cidlist = tcmappingMapper.selectList(queryWrapper)
                    .stream().map(Teachercoursemapping::getCourseId)
                    .collect(Collectors.toList());
        }


        // 如果课程ID列表为空，提示用户无相关课程
        if (cidlist.isEmpty()) {
            return Result.error("没有相关的课程数据");
        }

        QueryWrapper<Course> queryWrapperc = new QueryWrapper<>();
        queryWrapperc.in("course_id", cidlist)
                .eq("course_status", CourseStatus.IN_PROGRESS) // 添加状态条件
                .select("course_id", "course_name");

// 获取课程列表
        List<Course> courseList = courseMapper.selectList(queryWrapperc);

// 如果没有符合条件的课程，提示用户
        if (courseList.isEmpty()) {
            return Result.error("没有相关的进行中的课程数据");
        }

// 构建课程ID和课程名的映射
        Map<Integer, String> courseMap = courseList.stream()
                .collect(Collectors.toMap(
                        Course::getCourseId,
                        Course::getCourseName
                ));

        return Result.success(courseMap);

    }

    @Override
    public Course getCourseWithStatusUpdate(Integer courseId) {
        Course course = getById(courseId);
        updateCourseStatus(course);
        return course;
    }

    @Override
    public ToPage<Course> adminGetCoursePage(PageQuery query, String keyword, String status, String registeredStudents, Integer teacherId, LocalDateTime startTime, LocalDateTime endTime, String subject) {
        Page<Course> page = query.toMpPage();
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();

        // 关键字查询（课程名称）
        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.like("course_name", keyword);
        }

        // 课程状态查询
        if (status != null && !status.isEmpty()) {
            try {
                CourseStatus courseStatus = CourseStatus.valueOf(status.toUpperCase());
                queryWrapper.eq("course_status", courseStatus);
            } catch (IllegalArgumentException e) {
                // 忽略无效的状态值
            }
        }

        // 已注册学生数查询（支持大于、小于等条件）
        if (registeredStudents != null && !registeredStudents.isEmpty()) {
            try {
                int students = Integer.parseInt(registeredStudents);
                queryWrapper.eq("registered_students", students);
            } catch (NumberFormatException e) {
                // 忽略无效的学生数
            }
        }

        // 教师ID查询
        if (teacherId != null) {
            queryWrapper.eq("teacher_id", teacherId);
        }

        // 上课时间范围查询
        if (startTime != null) {
            queryWrapper.ge("class_time", startTime);
        }

        if (endTime != null) {
            queryWrapper.le("class_time", endTime);
        }

        // 学科查询
        if (subject != null && !subject.isEmpty()) {
            queryWrapper.like("subject", subject);
        }

        // 执行分页查询
        page(page, queryWrapper);

        return ToPage.of(page, Course.class);
    }

    private void updateCourseStatus(Course course) {
        if (course.getClassTime() == null || course.getClassEndTime() == null) {
            return; // 时间未设置时不更新状态
        }

        LocalDateTime now = LocalDateTime.now();
        CourseStatus newStatus;

        if (now.isBefore(course.getClassTime())) {
            newStatus = CourseStatus.NOT_STARTED;
        } else if (now.isAfter(course.getClassEndTime())) {
            newStatus = CourseStatus.ENDED;
        } else {
            newStatus = CourseStatus.IN_PROGRESS;
        }

        if (newStatus != course.getCourseStatus()) {
            course.setCourseStatus(newStatus);
            updateById(course);
        }
    }

    //校验课程是否属于该用户
    public Result checkRelationship(String role, Integer id, Integer courseId) throws Exception {
        //没有关联抛出无权限
        if (role.equals("ROLE_student") || role.equals("ROLE_parent")) {
            if (scmappingMapper.checkStudentCourseRelationship(id, courseId) == null) {
                return Result.success(false);
            }
        } else if (role.equals("ROLE_teacher")) {
            if (tcmappingMapper.checkTeacherCourseRelationship(id, courseId) == null) {
                return Result.success(false);
            }
        } else {
            throw new Exception("账号异常，请联系管理员");
        }
        return Result.success(true);
    }

    //获取学生名与ID的map
    public Map<Integer, String> getStudentIdUsernameMap(int courseId) {
        // 构造学生ID查询条件
        QueryWrapper<Studentcoursemapping> queryWrappersc = new QueryWrapper<>();
        queryWrappersc.eq("course_id", courseId);
        queryWrappersc.select("student_id");

        // 检查是否有学生加入该课程
        if (!scmappingMapper.exists(queryWrappersc)) {
            throw new IllegalArgumentException("该课程暂无已加入学生");
        }

        // 获取学生ID列表
        List<Integer> sidlist = scmappingMapper.selectList(queryWrappersc)
                .stream()
                .map(Studentcoursemapping::getStudentId)
                .collect(Collectors.toList());

        // 构造学生用户名查询条件
        QueryWrapper<Students> queryWrappers = new QueryWrapper<>();
        queryWrappers.in("student_user_id", sidlist)
                .select("student_username");

        // 获取学生用户名列表
        List<String> snamelist = studentsService.list(queryWrappers).stream()
                .map(Students::getStudentUsername)
                .collect(Collectors.toList());

        // 验证学生ID列表和用户名列表的大小是否一致
        if (sidlist.size() != snamelist.size()) {
            throw new IllegalArgumentException("学生ID列表和用户名列表的大小不匹配");
        }

        // 构造学生ID和用户名的映射
        Map<Integer, String> studentMap = IntStream.range(0, sidlist.size())
                .boxed()
                .collect(Collectors.toMap(sidlist::get, snamelist::get));

        return studentMap;
    }
}

