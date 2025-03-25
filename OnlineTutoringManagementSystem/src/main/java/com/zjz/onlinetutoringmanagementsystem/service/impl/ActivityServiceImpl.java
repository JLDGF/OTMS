package com.zjz.onlinetutoringmanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjz.enums.ActivityStatus;
import com.zjz.enums.UserRole;
import com.zjz.onlinetutoringmanagementsystem.mapper.FeeTransactionsMapper;
import com.zjz.onlinetutoringmanagementsystem.mapper.StudentcoursemappingMapper;
import com.zjz.onlinetutoringmanagementsystem.mapper.TeachercoursemappingMapper;
import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.onlinetutoringmanagementsystem.service.*;
import com.zjz.pojo.*;
import com.zjz.onlinetutoringmanagementsystem.mapper.ActivityMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjz.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zjz
 * @since 2025-01-15
 */
@Slf4j
@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements IActivityService {


    @Autowired
    private ITeachersService teachersService;

    @Autowired
    private IStudentsService studentsService;

    @Autowired
    private IParentsService parentsService;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private StudentcoursemappingMapper scmappingMapper;

    @Autowired
    private TeachercoursemappingMapper tcmappingMapper;

    @Autowired
    private ILevelRulesService levelRulesService;

    @Autowired
    private FeeTransactionsMapper feeTransactionsMapper;

    @Autowired
    private ICourseService courseService;

    @Override
    public ToPage<Activity> queryCompletedActivityPageByUsername(PageQuery query) throws Exception {
        log.info("query Completed Activity Page Now");
        Page<Activity> page = query.toMpPage();
        QueryWrapper<Activity> queryWrapper = new QueryWrapper<>();
        String username = SecurityUtils.getCurrentUsername();
        String role = SecurityUtils.getCurrentUserRole();
        Integer id;

        log.info("username:{}", username);

        if (username == null || username.isEmpty() || role == null || role.isEmpty()) {
            throw new Exception("账号异常，请联系管理员");
        }
        //获取用户id

        switch (role) {
            case "ROLE_student":
                id = studentsService.getByUsername(username).getStudentUserId();
                if (id == null) {
                    return null;
                }
                queryWrapper.eq("student_id", id);
                break;
            case "ROLE_parent":
                id = parentsService.getStudentIdByUsername(username);
                if (id == null) {
                    return null;
                }
                queryWrapper.eq("student_id", id);
                break;
            case "ROLE_teacher":
                id = teachersService.getByUsername(username).getTeacherUserId();
                if (id == null) {
                    return null;
                }
                queryWrapper.eq("teacher_id", id);
                break;
            default:
                throw new Exception("账号异常，请联系管理员");
        }
        queryWrapper.and(wrapper -> wrapper.eq("activity_status", ActivityStatus.COMPLETED)
                .or()
                .eq("activity_status", ActivityStatus.SEVALUATED)
                .or()
                .eq("activity_status", ActivityStatus.TEVALUATED)
                .or()
                .eq("activity_status", ActivityStatus.OVER))
                .orderByAsc("scheduled_start_time");
        // 3. 查询，传入分页和查询条件
        page(page, queryWrapper);

        // 4. 返回数据
        return ToPage.of(page, Activity.class);
    }

    @Override
    public ToPage<Activity> queryUpcomingActivityPageByUsername(PageQuery query) throws Exception {
        log.info("query Completed Activity Page Now");
        Page<Activity> page = query.toMpPage();
        QueryWrapper<Activity> queryWrapper = new QueryWrapper<>();
        UpdateWrapper<Activity> updateWrapper = new UpdateWrapper<>();
        String username = SecurityUtils.getCurrentUsername();
        String role = SecurityUtils.getCurrentUserRole();
        Integer id;

        log.info("username:{}", username);

        if (username == null || username.isEmpty() || role == null || role.isEmpty()) {
            throw new Exception("账号异常，请联系管理员");
        }
        //获取用户id
        switch (role) {
            case "ROLE_student":
                id = studentsService.getByUsername(username).getStudentUserId();
                if (id == null) {
                    return null;
                }
                queryWrapper.eq("student_id", id);
                updateWrapper.eq("student_id", id);
                break;
            case "ROLE_parent":
                id = parentsService.getStudentIdByUsername(username);
                if (id == null) {
                    return null;
                }
                queryWrapper.eq("student_id", id);
                updateWrapper.eq("student_id", id);
                break;
            case "ROLE_teacher":
                id = teachersService.getByUsername(username).getTeacherUserId();
                if (id == null) {
                    return null;
                }
                queryWrapper.eq("teacher_id", id);
                updateWrapper.eq("teacher_id", id);
                break;
            default:
                throw new Exception("账号异常，请联系管理员");
        }
        //搜索已同意和待处理
        queryWrapper.and(wrapper -> wrapper
                .eq("activity_status", ActivityStatus.AGREED)
                .or()
                .eq("activity_status", ActivityStatus.TPENDING)
                .or()
                .eq("activity_status", ActivityStatus.PPENDING))
                .orderByAsc("scheduled_start_time");
        // 3. 更新，查询，传入分页和查询条件
        updateActivityBeforeSearch(updateWrapper);
        page(page, queryWrapper);

        // 4. 返回数据
        return ToPage.of(page, Activity.class);
    }

    @Override
    public ToPage<Activity> queryInvalidActivityPageByUsername(PageQuery query) throws Exception {
        log.info("query Completed Activity Page Now");
        Page<Activity> page = query.toMpPage();
        QueryWrapper<Activity> queryWrapper = new QueryWrapper<>();
        UpdateWrapper<Activity> updateWrapper = new UpdateWrapper<>();
        String username = SecurityUtils.getCurrentUsername();
        String role = SecurityUtils.getCurrentUserRole();
        Integer id;

        log.info("username:{}", username);

        if (username == null || username.isEmpty() || role == null || role.isEmpty()) {
            throw new Exception("账号异常，请联系管理员");
        }
        //获取用户id
        switch (role) {
            case "ROLE_student":
                id = studentsService.getByUsername(username).getStudentUserId();
                if (id == null) {
                    return null;
                }
                queryWrapper.eq("student_id", id);
                updateWrapper.eq("student_id", id);
                break;
            case "ROLE_parent":
                id = parentsService.getStudentIdByUsername(username);
                if (id == null) {
                    return null;
                }
                queryWrapper.eq("student_id", id);
                updateWrapper.eq("student_id", id);
                break;
            case "ROLE_teacher":
                id = teachersService.getByUsername(username).getTeacherUserId();
                if (id == null) {
                    return null;
                }
                queryWrapper.eq("teacher_id", id);
                updateWrapper.eq("teacher_id", id);
                break;
            default:
                throw new Exception("账号异常，请联系管理员");
        }
        //搜索已同意和待处理
        queryWrapper.and(wrapper -> wrapper
                .eq("activity_status", ActivityStatus.REJECTED)
                .or()
                .eq("activity_status", ActivityStatus.EXPIRED)
        )
                .orderByAsc("scheduled_start_time");
        // 3. 更新，查询，传入分页和查询条件
        updateActivityBeforeSearch(updateWrapper);
        page(page, queryWrapper);

        // 4. 返回数据
        return ToPage.of(page, Activity.class);
    }

    @Override
    public ToPage<Activity> queryActivityPageByCourse(PageQuery query, Integer courseId) throws Exception {
        log.info("query Completed Activity Page Now");
        Page<Activity> page = query.toMpPage();
        QueryWrapper<Activity> queryWrapper = new QueryWrapper<>();
        UpdateWrapper<Activity> updateWrapper = new UpdateWrapper<>();
        String username = SecurityUtils.getCurrentUsername();
        String role = SecurityUtils.getCurrentUserRole();
        Integer id;

        log.info("username:{}", username);

        if (username == null || username.isEmpty() || role == null || role.isEmpty()) {
            throw new Exception("账号异常，请联系管理员");
        }
        //获取用户id

        switch (role) {
            case "ROLE_student":
                id = studentsService.getByUsername(username).getStudentUserId();
                queryWrapper.eq("student_id", id);
                updateWrapper.eq("student_id", id);
                break;
            case "ROLE_parent":
                id = parentsService.getStudentIdByUsername(username);
                queryWrapper.eq("student_id", id);
                updateWrapper.eq("student_id", id);
                break;
            case "ROLE_teacher":
                id = teachersService.getByUsername(username).getTeacherUserId();
                queryWrapper.eq("teacher_id", id);
                updateWrapper.eq("teacher_id", id);
                break;
            default:
                throw new Exception("账号异常，请联系管理员");
        }
        queryWrapper.eq("course_id", courseId)
                .orderByAsc("scheduled_start_time");

        // 3. 更新，然后查询，传入分页和查询条件
        updateActivityBeforeSearch(updateWrapper);
        page(page, queryWrapper);

        // 4. 返回数据
        return ToPage.of(page, Activity.class);
    }

    //用户查询活动记录详细信息
    @Override
    public Result getActivityDetails(Integer activityId) throws Exception {
        log.info("get Activity Details Now");
        QueryWrapper<Activity> queryWrapper = new QueryWrapper<>();
        UpdateWrapper<Activity> updateWrapper = new UpdateWrapper<>();
        String username = SecurityUtils.getCurrentUsername();
        String role = SecurityUtils.getCurrentUserRole();
        Integer id;

        log.info("username:{}", username);

        if (username == null || username.isEmpty() || role == null || role.isEmpty()) {
            throw new Exception("账号异常，请联系管理员");
        }
        //获取用户id

        switch (role) {
            case "ROLE_student":
                id = studentsService.getByUsername(username).getStudentUserId();
                queryWrapper.eq("student_id", id);
                updateWrapper.eq("student_id", id);
                break;
            case "ROLE_parent":
                id = parentsService.getStudentIdByUsername(username);
                queryWrapper.eq("student_id", id);
                updateWrapper.eq("student_id", id);
                break;
            case "ROLE_teacher":
                id = teachersService.getByUsername(username).getTeacherUserId();
                queryWrapper.eq("teacher_id", id);
                updateWrapper.eq("teacher_id", id);
                break;
            default:
                throw new Exception("账号异常，请联系管理员");
        }
        queryWrapper.eq("activity_id", activityId);

        updateActivityBeforeSearch(updateWrapper);

        return Result.success(activityMapper.selectOne(queryWrapper));
    }

    //updateActivityStatus
    //1.获取2.校验3.修改4.返回
    @Override
    public Result agree(Integer activityId) {
        Activity a = activityMapper.selectById(activityId);
        String role = SecurityUtils.getCurrentUserRole();
        assert role != null;
        //当前进行操作的用户ID与活动中对应时更新状态
        if (role.equals("ROLE_teacher")) {
            if (a.getTeacherId().equals(teachersService.getByUsername(SecurityUtils.getCurrentUsername()).getTeacherUserId())) {
                a.setActivityStatus(ActivityStatus.AGREED);
            }
        } else if (role.equals("ROLE_parent")) {
            //先获取用户ID在查找学生ID
            if (a.getStudentId().equals(parentsService.getStudentIdByUsername(SecurityUtils.getCurrentUsername()))) {
                a.setActivityStatus(ActivityStatus.AGREED);
            }
        }
        //获取对象
        Teachers teachers = teachersService.getById(a.getTeacherId());
        Students students = studentsService.getById(a.getStudentId());
        Parents parents = parentsService.getById(students.getParentUserId());
        //更新双方活动次数
        teachers.setActivityCount(teachers.getActivityCount() + 1);
        students.setActivityCount(students.getActivityCount() + 1);

        //取整计算课时
        int hours = a.calculateClassHours();
        BigDecimal newHours = new BigDecimal(hours);
        //计算费用，家长直接计算课时*课时费*学生等级折扣，教师计算应得,教师应得为家长应付乘0.8
        BigDecimal parentsFee = (newHours
                .multiply(courseService.getById(a.getCourseId()).getHourlyRate()))
                .multiply(levelRulesService.getById(students.getRating()).getPaymentDiscount());
        BigDecimal teacherFee = parentsFee.multiply(BigDecimal.valueOf(0.80));

        //构造账单
        FeeTransactions feeTransactions = new FeeTransactions();
        feeTransactions.setActivityId(activityId);
        feeTransactions.setTeacherId(teachers.getTeacherUserId());
        feeTransactions.setStudentId(students.getStudentUserId());
        feeTransactions.setFeeAmount(teacherFee);
        feeTransactions.setRealFeeAmount(parentsFee);
        //记录账单
        feeTransactionsMapper.insert(feeTransactions);
        //更新费用,教师费用在支付后更新
        parents.setConfirmPrice(parents.getConfirmPrice().add(parentsFee));
        //更新活动时长
        teachers.setActivityTime(teachers.getActivityTime() + hours);
        students.setActivityTime(students.getActivityTime() + hours);

        //集体更新
        teachersService.updateById(teachers);
        studentsService.updateById(students);
        parentsService.updateById(parents);
        activityMapper.updateById(a);
        return Result.success("已同意");
    }

    @Override
    public Result reject(Integer activityId) {
        Activity a = activityMapper.selectById(activityId);
        String role = SecurityUtils.getCurrentUserRole();
        assert role != null;
        if (role.equals("ROLE_teacher")) {
            if (a.getTeacherId().equals(teachersService.getByUsername(SecurityUtils.getCurrentUsername()).getTeacherUserId())) {
                a.setActivityStatus(ActivityStatus.REJECTED);
            }
        } else if (role.equals("ROLE_parent")) {
            //先获取用户ID在查找学生ID
            if (a.getStudentId().equals(parentsService.getStudentIdByUsername(SecurityUtils.getCurrentUsername()))) {
                a.setActivityStatus(ActivityStatus.REJECTED);
            }
        }
        activityMapper.updateById(a);
        return Result.success("已拒绝");
    }

    @Override
    public ToPage<Activity> adminGetActivityPage(
            PageQuery query, String activityName, ActivityStatus status,
            Integer courseId, Integer teacherId, Integer studentId,
            LocalDateTime startTime, LocalDateTime endTime) {

        Page<Activity> page = query.toMpPage();
        QueryWrapper<Activity> queryWrapper = new QueryWrapper<>();

        // 活动名称模糊查询
        if (activityName != null && !activityName.isEmpty()) {
            queryWrapper.like("activity_name", activityName);
        }

        // 活动状态查询（枚举直接匹配）
        if (status != null) {
            queryWrapper.eq("activity_status", status.getCode()); // 根据枚举code存储
        }

        // 关联三方ID精确查询
        if (courseId != null) {
            queryWrapper.eq("course_id", courseId);
        }
        if (teacherId != null) {
            queryWrapper.eq("teacher_id", teacherId);
        }
        if (studentId != null) {
            queryWrapper.eq("student_id", studentId);
        }

        // 活动时间范围查询
        if (startTime != null) {
            queryWrapper.ge("scheduled_start_time", startTime);
        }
        if (endTime != null) {
            queryWrapper.le("scheduled_end_time", endTime);
        }

        // 执行分页查询
        page(page, queryWrapper);

        return ToPage.of(page, Activity.class);
    }

    @Override
    public Result adminGetActivityTrend(LocalDateTime startTime, LocalDateTime endTime) {
        // 1. 生成完整日期范围
        List<LocalDate> allDates = new ArrayList<>();
        LocalDate currentDate = startTime.toLocalDate();
        LocalDate endDate = endTime.toLocalDate();

        while (!currentDate.isAfter(endDate)) {
            allDates.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }

        // 2. 初始化默认值为0的映射表
        Map<LocalDate, Long> dateCountMap = allDates.stream()
                .collect(Collectors.toMap(
                        date -> date,
                        date -> 0L
                ));

        // 3. 查询数据库并更新存在记录的日期
        QueryWrapper<Activity> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("scheduled_start_time", startTime, endTime);

        List<Activity> activityList = this.list(queryWrapper);

        activityList.stream()
                .collect(Collectors.groupingBy(
                        activity -> activity.getScheduledStartTime().toLocalDate(),
                        Collectors.counting()
                ))
                .forEach(dateCountMap::put);

        // 4. 构建有序结果列表
        List<Map<String, Object>> dataList = allDates.stream()
                .map(date -> {
                    Map<String, Object> dataMap = new HashMap<>();
                    dataMap.put("date", date.format(DateTimeFormatter.ISO_DATE));
                    dataMap.put("count", dateCountMap.get(date));
                    return dataMap;
                })
                .collect(Collectors.toList());

        return Result.success(dataList);
    }


    @Override
    public Result AddActivity(Activity activity) throws Exception {
        try {
            if (activity.getActivityId() != null) {
                throw new Exception("非法输入");
            }
            String role = SecurityUtils.getCurrentUserRole();
            //输入校验
            assert role != null;
            switch (role) {
                case "ROLE_parent": {
                    //构造查询条件，检查学生与课程的关联
                    String username = SecurityUtils.getCurrentUsername();
                    Integer id = parentsService.getStudentIdByUsername(username);
                    QueryWrapper<Studentcoursemapping> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("course_id", activity.getCourseId())
                            .eq("student_id", id);
                    //家长预约时，设置状态为等待教师
                    if (scmappingMapper.exists(queryWrapper)) {
                        activity.setStudentId(id);
                        activity.setActivityStatus(ActivityStatus.TPENDING);
                    } else {
                        throw new Exception("系统异常");
                    }
                    QueryWrapper<Teachercoursemapping> queryWrappertc = new QueryWrapper<>();
                    queryWrappertc.eq("course_id", activity.getCourseId())
                            .select("teacher_id");
                    activity.setTeacherId(tcmappingMapper.selectOne(queryWrappertc).getTeacherId());
                    break;
                }
                case "ROLE_teacher": {
                    //构造查询条件，检查教师与课程的关联
                    String username = SecurityUtils.getCurrentUsername();
                    Integer id = teachersService.getByUsername(username).getTeacherUserId();
                    QueryWrapper<Teachercoursemapping> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("course_id", activity.getCourseId())
                            .eq("teacher_id", id);
                    //教师预约时，设置状态为等待家长
                    if (tcmappingMapper.exists(queryWrapper)) {
                        activity.setTeacherId(id);
                        activity.setActivityStatus(ActivityStatus.PPENDING);
                    } else {
                        throw new Exception("非法输入");
                    }
                    break;
                }
                case "ROLE_admin":
                    activity.setActivityStatus(ActivityStatus.AGREED);
                    break;
                default:
                    throw new Exception("未知异常，请联系管理员");
            }
        } catch (Exception e) {
            return Result.error("error:" + e.toString());
        }
        //插入数据并返回
        activityMapper.insert(activity);
        return Result.success("新增成功");
    }

    //每次查询前更新所有活动的状态
    public void updateActivityBeforeSearch(UpdateWrapper<Activity> updateWrapper) {

        UpdateWrapper<Activity> updateWrapper1 = updateWrapper.clone();

        // 时间已过且状态为待处理，设置为过期
        updateWrapper.lt("scheduled_end_time", new Date())
                .and(wrapper -> wrapper.eq("activity_status", ActivityStatus.TPENDING)
                        .or()
                        .eq("activity_status", ActivityStatus.PPENDING));
        updateWrapper.set("activity_status", ActivityStatus.EXPIRED);
        activityMapper.update(null, updateWrapper);


        // 时间已过且状态为待完成，设置为完成
        updateWrapper1.lt("scheduled_end_time", new Date())
                .and(wrapper -> wrapper.eq("activity_status", ActivityStatus.AGREED));
        updateWrapper1.set("activity_status", ActivityStatus.COMPLETED);
        activityMapper.update(null, updateWrapper1);
    }


}
