package com.zjz.onlinetutoringmanagementsystem.controller.admin;

import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.onlinetutoringmanagementsystem.service.ICommentsService;
import com.zjz.onlinetutoringmanagementsystem.service.IPostsService;
import com.zjz.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/admin/forum")
public class adminForumController {

    @Autowired
    private IPostsService postsService;

    @Autowired
    private ICommentsService commentsService;

    @DeleteMapping("postDelete")
    public Result adminDeletePost(Integer postId) {
        try {
            return postsService.adminDeletePost(postId);
        } catch (Exception e) {
            return Result.error(e.toString());
        }
    }

    @PutMapping("PostIsPinned")
    public Result adminUpdatePostIsPinned(Integer postId,String isPinned) {
        try {
            return postsService.adminUpdatePostIsPinned(postId,isPinned);
        } catch (Exception e) {
            return Result.error(e.toString());
        }
    }

    @DeleteMapping("commentDelete")
    public Result adminDeleteComment(Integer commentId) {
        try {
            return commentsService.adminDeleteComment(commentId);
        } catch (Exception e) {
            return Result.error(e.toString());
        }
    }

    // 分页获取Post列表
    @GetMapping("/AllPostPage")
    public ToPage<Posts> adminGetPostPage(
            PageQuery query,
            @RequestParam(required = false) Integer postId,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String titleKeyword,
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime createdAt,
            @RequestParam(required = false) String createdAtStatus,
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime lastUpdated,
            @RequestParam(required = false) String lastUpdatedStatus,
            @RequestParam(required = false) String isPinned

    ) {
        log.info("Post分页查询：页码{}，页大小{}", query.getPageNo(), query.getPageSize());

        return postsService.adminQueryPostPage(
                query, postId, userId, titleKeyword, createdAt, createdAtStatus, lastUpdated, lastUpdatedStatus, isPinned
        );
    }

    // 分页获取Comment列表
    @GetMapping("/AllCommentPage")
    public ToPage<Comments> adminGetCommentPage(
            PageQuery query,
            @RequestParam(required = false) Integer postId,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer commentId,
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime createdAt,
            @RequestParam(required = false) String createdAtStatus
    ) {
        log.info("Comment分页查询：页码{}，页大小{}", query.getPageNo(), query.getPageSize());

        return commentsService.adminGetCommentPage(query,userId,postId,commentId,createdAt,createdAtStatus);
    }

}



