package com.zjz.onlinetutoringmanagementsystem.mapper;

import com.zjz.pojo.Assignment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zjz
 * @since 2025-02-14
 */
public interface AssignmentMapper extends BaseMapper<Assignment> {

    @Select("SELECT assignment_id\n" +
            "FROM assignment\n" +
            "WHERE JSON_CONTAINS(release_target, #{studentId})")
    List<Integer> selectAssignmentIdListByStudentId(String studentId);
}
