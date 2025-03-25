package com.zjz.onlinetutoringmanagementsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.pojo.Faq;
import com.zjz.pojo.ToPage;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zjz
 * @since 2025-03-06
 */
public interface IFaqService extends IService<Faq> {

    ToPage<Faq> adminGetFaqPage(PageQuery query, String keyword, String status);

    ToPage<Faq> userGetFaqPage(PageQuery query, String keyword, String status);
}
