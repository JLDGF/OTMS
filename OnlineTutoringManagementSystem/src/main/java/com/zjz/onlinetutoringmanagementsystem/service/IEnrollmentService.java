package com.zjz.onlinetutoringmanagementsystem.service;

import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.pojo.Enrollment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjz.pojo.Result;
import com.zjz.pojo.ToPage;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zjz
 * @since 2025-01-10
 */
public interface IEnrollmentService extends IService<Enrollment> {

    Boolean ParentEnroll(Integer courseId,Integer teacherId) throws Exception;

    ToPage<Enrollment> queryEnrollmentPageByUsername(PageQuery query) throws Exception;


    Result agree(Integer enrollmentId);

    Result reject(Integer enrollmentId);
}
