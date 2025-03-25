package com.zjz.onlinetutoringmanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjz.onlinetutoringmanagementsystem.mapper.FaqMapper;
import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.onlinetutoringmanagementsystem.service.IFaqService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjz.pojo.Faq;
import com.zjz.pojo.ToPage;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zjz
 * @since 2025-03-06
 */
@Service
public class FaqServiceImpl extends ServiceImpl<FaqMapper, Faq> implements IFaqService {

    @Override
    public ToPage<Faq> adminGetFaqPage(PageQuery query, String keyword, String status) {

        Page<Faq> page = query.toMpPage();

        QueryWrapper<Faq> queryWrapper = new QueryWrapper<>();

        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.like("question_title", keyword);
        }
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("is_valid", status.equals("true"));
        }

        page(page, queryWrapper);

        return ToPage.of(page, Faq.class);
    }

    @Override
    public ToPage<Faq> userGetFaqPage(PageQuery query, String keyword, String status) {
        Page<Faq> page = query.toMpPage();

        QueryWrapper<Faq> queryWrapper = new QueryWrapper<>();

        if (keyword != null && !keyword.isEmpty()) {
            queryWrapper.like("question_title", keyword);
        }

        queryWrapper
                .select("faq_id","question_title","question_content","question_category","answer_content")
                .eq("is_valid",true);
        ;

        page(page, queryWrapper);

        return ToPage.of(page, Faq.class);
    }
}
