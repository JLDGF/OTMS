package com.zjz.onlinetutoringmanagementsystem.mapper;

import com.zjz.pojo.Studentcoursemapping;
import com.zjz.pojo.Teachercoursemapping;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zjz
 * @since 2025-01-17
 */
public interface TeachercoursemappingMapper extends BaseMapper<Teachercoursemapping> {

    @Select("SELECT * from teachercoursemapping where teacher_id=#{teacherId} and course_id=#{courseId}")
    Teachercoursemapping checkTeacherCourseRelationship(@Param("teacherId")Integer teacherId, @Param("courseId")Integer courseId);

}

