package com.zjz.onlinetutoringmanagementsystem.mapper;

import com.zjz.pojo.Parents;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zjz
 * @since 2024-12-10
 */
public interface ParentsMapper extends BaseMapper<Parents> {

    @Select("select * from parents where parent_username = #{username}")
    Parents getByUsername(String username);

    @Select("select student_user_id from parents where parent_username = #{username}")
    Integer getStudentIdByUsername(String username);
}
