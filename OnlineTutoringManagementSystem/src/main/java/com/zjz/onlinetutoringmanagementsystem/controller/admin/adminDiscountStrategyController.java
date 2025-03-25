package com.zjz.onlinetutoringmanagementsystem.controller.admin;

import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.onlinetutoringmanagementsystem.service.ILevelRulesService;
import com.zjz.pojo.LevelRules;
import com.zjz.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/strategy")
public class adminDiscountStrategyController {
    @Autowired
    ILevelRulesService levelRulesService;
    //增
    @PostMapping("add")
    public Result adminCreateRules(@RequestBody LevelRules levelRules){
        try {
            levelRulesService.save(levelRules);
        }catch (Exception e){
            log.error(e.toString());
            return Result.error("新增失败");
        }
        return Result.success();
    }
    //删
    @DeleteMapping("delete")
    public Result adminDeleteRulesById(Integer rating){
        try {
            levelRulesService.removeById(rating);
        }catch (Exception e){
            log.error(e.toString());
            return Result.error("删除失败");
        }
        return Result.success();
    }
    //改
    @PutMapping("update")
    public Result adminUpdateRules(@RequestBody LevelRules levelRules){
        try {
            levelRulesService.updateById(levelRules);
        }catch (Exception e){
            log.error(e.toString());
            return Result.error("修改失败");
        }
        return Result.success();
    }
    //查
    @GetMapping("list")
    public Result adminGetRulesByQuery(PageQuery query,Integer rating){

        try {
            return levelRulesService.GetRulesByQuery(query,rating);
        }catch (Exception e){
            log.error(e.toString());
            return Result.error("修改失败");
        }
    }
}
