package com.zjz.onlinetutoringmanagementsystem.controller.admin;


import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.onlinetutoringmanagementsystem.service.INoticeService;
import com.zjz.pojo.Notice;
import com.zjz.pojo.Result;
import com.zjz.pojo.ToPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zjz
 * @since 2025-02-28
 */
@Slf4j
@RestController
@RequestMapping("/admin/notice")
public class adminNoticeController {

    @Autowired
    INoticeService noticeService;

    //分页获取公告
    @GetMapping("/AllNoticePage")
    public ToPage<Notice> AdminGetNoticePage(PageQuery query,String title,String status){
        log.info("query Notice Page Now");
        return noticeService.AdminGetNoticePage(query,title,status);
    }

    @PostMapping("/add")
    public Result CreateNotice(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("announcementStatus") String announcementStatus,
            @RequestParam(value = "attachments",required = false) MultipartFile attachments
    ){
        noticeService.add(title,content,announcementStatus,attachments);

        return Result.success();
    }

    @PutMapping("/update")
    public Result UpdateNotice(
            @RequestParam("noticeId")Integer noticeId,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("announcementStatus") String announcementStatus,
            @RequestParam(value = "attachments",required = false) MultipartFile attachments
    ){
        noticeService.updateNotice(noticeId,title,content,announcementStatus,attachments);

        return Result.success();
    }

    @DeleteMapping("/delete")
    public Result DeleteNotice(Integer noticeId){
        noticeService.DeleteNoticeById(noticeId);
        return Result.success();
    }
}
