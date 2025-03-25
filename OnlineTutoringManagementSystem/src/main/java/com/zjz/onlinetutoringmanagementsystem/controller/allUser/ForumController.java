package com.zjz.onlinetutoringmanagementsystem.controller.allUser;

import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.onlinetutoringmanagementsystem.service.ICommentsService;
import com.zjz.onlinetutoringmanagementsystem.service.IPostsService;
import com.zjz.pojo.Posts;
import com.zjz.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/user")
public class ForumController {

    @Autowired
    IPostsService postsService;

    @Autowired
    ICommentsService commentsService;

    //获取置顶和热门帖子信息
    @GetMapping("/GetStickiedPosts")
    public Result GetStickiedPosts() {
        log.info("query Stickied posts Now");
        return postsService.GetStickiedPosts();
    }

    //根据条件分页获取帖子信息
    @GetMapping("/GetPostsByQuery")
    public Result GetPostsByQuery(PageQuery query, @RequestParam(required = false) String title) {
        log.info("query posts Page Now");
        return postsService.GetPostsByQuery(query, title);
    }

    //新建帖子
    @PostMapping("createPost")
    public Result CreatePost(@RequestParam String title, @RequestParam String content) {
        try {
            if (title == null || title.isEmpty() || content == null || content.isEmpty()) {
                return Result.error("无内容");
            }
            return postsService.createPost(title, content);
        } catch (Exception e) {
            log.info("post error:" + e.toString());
            return Result.error("新建帖子失败");
        }
    }

    //删除帖子：DELETE /posts/{id} 允许用户或管理员删除帖子。
    @DeleteMapping("/user/deletePost")
    public Result UserDeletePost(Integer postId) {
        try {
            postsService.removeById(postId);
        } catch (Exception e) {
            return Result.error(e.toString());
        }
        return Result.success();
    }

    //根据帖子ID查看帖子详情
    @GetMapping("getPostById")
    public Posts getPostById(Integer postId) {
        return postsService.getById(postId);
    }

    //评论
    @PostMapping("/toComment")
    public Result UserComment(
            @RequestParam Integer postId,
            @RequestParam String content) {
        try {
            commentsService.UserCommentByPostId(postId, content);
        } catch (Exception e) {
            return Result.error(e.toString());
        }
        return Result.success();
    }

    //根据条件查询评论
    @GetMapping("/GetCommentsByPostId")
    public Result GetCommentsByPostId(PageQuery query, Integer postId) {
        try {
            return commentsService.GetCommentsByPostId(query, postId);
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error(e.toString());
        }
    }

    //删除评论：DELETE /comments/{id} 允许用户或管理员删除评论。
    @DeleteMapping("/user/deleteComment")
    public Result UserDeleteComment(Integer commentId) {
        try {
            commentsService.removeById(commentId);
        } catch (Exception e) {
            return Result.error(e.toString());
        }
        return Result.success();
    }

    //点赞或取消点赞
    @PutMapping("/Like")
    public Result UserLike(Integer commentId) {
        try {
            commentsService.UserLike(commentId);
        } catch (Exception e) {
            return Result.error(e.toString());
        }
        return Result.success();
    }
    //查看是否已点赞
    @GetMapping("/LikeItOrNot")
    public Result LikeItOrNot(Integer commentId) {
        try {
            return commentsService.LikeItOrNot(commentId);
        } catch (Exception e) {
            return Result.error(e.toString());
        }
    }
}
