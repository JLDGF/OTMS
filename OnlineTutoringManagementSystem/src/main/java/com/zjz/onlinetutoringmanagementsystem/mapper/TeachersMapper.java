package com.zjz.onlinetutoringmanagementsystem.mapper;

import com.zjz.pojo.Teachers;
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
public interface TeachersMapper extends BaseMapper<Teachers> {

    @Select("select * from teachers where teacher_username = #{username}")
    Teachers getByUserName(String username);
}
