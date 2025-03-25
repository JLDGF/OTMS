package com.zjz.onlinetutoringmanagementsystem.service;

import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.pojo.LevelRules;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjz.pojo.Result;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zjz
 * @since 2025-03-03
 */
public interface ILevelRulesService extends IService<LevelRules> {

    Result GetRulesByQuery(PageQuery query, Integer rating);
}
