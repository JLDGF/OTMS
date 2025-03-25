package com.zjz.onlinetutoringmanagementsystem.mapper;

import com.zjz.pojo.Students;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zjz
 * @since 2024-12-10
 */
public interface StudentsMapper extends BaseMapper<Students> {

    @Select("select * from students where student_username = #{username}")
    Students getByUsername(String username);



}
