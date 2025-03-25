package com.zjz.onlinetutoringmanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.pojo.LevelRules;
import com.zjz.onlinetutoringmanagementsystem.mapper.LevelRulesMapper;
import com.zjz.onlinetutoringmanagementsystem.service.ILevelRulesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjz.pojo.Result;
import com.zjz.pojo.ToPage;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zjz
 * @since 2025-03-03
 */
@Service
public class LevelRulesServiceImpl extends ServiceImpl<LevelRulesMapper, LevelRules> implements ILevelRulesService {

    @Override
    public Result GetRulesByQuery(PageQuery query, Integer rating) {
        Page<LevelRules> page = query.toMpPage();
        QueryWrapper<LevelRules> queryWrapper = new QueryWrapper<>();
        if (rating != null){
            queryWrapper.eq("rating",rating);
        }

        page(page,queryWrapper);
        return Result.success(ToPage.of(page,LevelRules.class));
    }
}
