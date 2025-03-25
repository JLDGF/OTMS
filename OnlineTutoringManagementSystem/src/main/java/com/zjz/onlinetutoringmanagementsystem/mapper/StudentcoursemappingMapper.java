package com.zjz.onlinetutoringmanagementsystem.mapper;

import com.zjz.pojo.Studentcoursemapping;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zjz
 * @since 2025-01-11
 */
public interface StudentcoursemappingMapper extends BaseMapper<Studentcoursemapping> {

    @Insert("INSERT INTO studentcoursemapping (student_id, course_id)\n" +
            "VALUES (#{studentId}, #{courseId})")
    void insertNewMapping(@Param("studentId")Integer studentId, @Param("courseId")Integer courseId);

    @Select("select course_id from studentcoursemapping where student_id=#{studentId};")
    Integer[] getCourseIdByStudentId(@Param("studentId")Integer studentId);

    @Select("SELECT * from studentcoursemapping where student_id=#{studentId} and course_id=#{courseId}")
    Studentcoursemapping checkStudentCourseRelationship(@Param("studentId")Integer studentId, @Param("courseId")Integer courseId);
}
