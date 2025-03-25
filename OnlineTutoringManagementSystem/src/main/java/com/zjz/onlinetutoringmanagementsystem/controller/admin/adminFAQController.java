package com.zjz.onlinetutoringmanagementsystem.controller.admin;

import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.onlinetutoringmanagementsystem.service.IFaqService;
import com.zjz.pojo.Faq;
import com.zjz.pojo.Result;
import com.zjz.pojo.ToPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/faq")
public class adminFAQController {

    @Autowired
    private IFaqService faqService;

    // 分页获取FAQ列表
    @GetMapping("/AllFaqPage")
    public ToPage<Faq> adminGetFaqPage(PageQuery query,
                                       @RequestParam(required = false) String keyword,
                                       @RequestParam(required = false) String status) {
        log.info("FAQ分页查询：页码{}，页大小{}", query.getPageNo(), query.getPageSize());
        return faqService.adminGetFaqPage(query, keyword, status);
    }

    // 新增FAQ
    @PostMapping("/add")
    public Result createFaq(@RequestBody Faq faq) {
        log.info("新增FAQ：{}", faq.getQuestionTitle());
        faqService.save(faq);
        return Result.success("FAQ创建成功");
    }

    // 更新FAQ
    @PutMapping("/update")
    public Result updateFaq(@RequestBody Faq faq) {
        log.info("更新FAQ：ID{}", faq.getFaqId());
        faqService.updateById(faq);
        return Result.success("FAQ更新成功");
    }

    // 删除FAQ
    @DeleteMapping("/delete")
    public Result deleteFaq(@RequestParam Integer faqId) {
        log.info("删除FAQ：ID{}", faqId);
        faqService.removeById(faqId);
        return Result.success("FAQ删除成功");
    }

}