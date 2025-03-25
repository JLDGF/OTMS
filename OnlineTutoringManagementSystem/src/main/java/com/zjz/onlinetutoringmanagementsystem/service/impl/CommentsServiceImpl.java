package com.zjz.onlinetutoringmanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjz.onlinetutoringmanagementsystem.mapper.CommentLikesMapper;
import com.zjz.onlinetutoringmanagementsystem.mapper.PostsMapper;
import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.onlinetutoringmanagementsystem.service.IUsersService;
import com.zjz.pojo.*;
import com.zjz.onlinetutoringmanagementsystem.mapper.CommentsMapper;
import com.zjz.onlinetutoringmanagementsystem.service.ICommentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjz.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zjz
 * @since 2025-02-25
 */
@Service
public class CommentsServiceImpl extends ServiceImpl<CommentsMapper, Comments> implements ICommentsService {

    @Autowired
    IUsersService usersService;

    @Autowired
    CommentsMapper commentsMapper;

    @Autowired
    PostsMapper postsMapper;

    @Autowired
    CommentLikesMapper commentLikesMapper;

    @Override
    public void UserCommentByPostId(Integer postId, String content) {
        //构造评论数据
        Comments comments = new Comments();
        comments.setPostId(postId);
        comments.setContent(content);
        comments.setUserId(usersService.selectIdByUserName(SecurityUtils.getCurrentUsername()));
        comments.setCreatedAt(LocalDateTime.now());

        //更新帖子的最后更新时间
        Posts posts = postsMapper.selectById(postId);
        posts.setLastUpdated(LocalDateTime.now());
        postsMapper.updateById(posts);

        commentsMapper.insert(comments);
    }

    //评论点赞
    @Override
    public void UserLike(Integer commentId) {
        Integer userId = usersService.selectIdByUserName(SecurityUtils.getCurrentUsername());

        QueryWrapper<CommentLikes> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("user_id", userId)
                .eq("comment_id", commentId);

        UpdateWrapper<Comments> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("comment_id", commentId);

        //已点赞时取消，否则新增
        if (commentLikesMapper.exists(queryWrapper)) {
            commentLikesMapper.delete(queryWrapper);
            updateWrapper.setSql("like_count = like_count-1");
        } else {
            CommentLikes commentLikes = new CommentLikes();
            commentLikes.setCommentId(commentId);
            commentLikes.setUserId(userId);
            commentLikesMapper.insert(commentLikes);
            updateWrapper.setSql("like_count = like_count+1");
        }

        commentsMapper.update(null, updateWrapper);
    }

    @Override
    public Result GetCommentsByPostId(PageQuery query, Integer postId) {
        Page<Comments> page = query.toMpPage();
        QueryWrapper<Comments> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", postId);

        page(page, queryWrapper);
        return Result.success(ToPage.of(page, Comments.class));

    }

    @Override
    public Result LikeItOrNot(Integer commentId) {
        Integer userId = usersService.selectIdByUserName(SecurityUtils.getCurrentUsername());
        QueryWrapper<CommentLikes> queryWrapper = new QueryWrapper<>();

        queryWrapper
                .eq("user_id", userId)
                .eq("comment_id", commentId);

        if (commentLikesMapper.exists(queryWrapper)) {
            return Result.success(true);
        } else {
            return Result.success(false);
        }
    }

    @Override
    @Transactional
    //单项删除
    public Result adminDeleteComment(Integer commentId) {
        //删除评论及其相关点赞信息
        commentsMapper.deleteById(commentId);

        QueryWrapper<CommentLikes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("comment_id", commentId);

        commentLikesMapper.delete(queryWrapper);
        return Result.success();
    }

    @Override
    @Transactional
    //批量删除
    public Result adminDeleteComments(List<Integer> IdList) {
        //删除评论及其相关点赞信息
        commentsMapper.deleteBatchIds(IdList);

        QueryWrapper<CommentLikes> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("comment_id", IdList);

        if (commentLikesMapper.exists(queryWrapper)){
            commentLikesMapper.delete(queryWrapper);
        }


        return Result.success();
    }

    @Override
    public ToPage<Comments> adminGetCommentPage(PageQuery query, Integer userId, Integer postId, Integer commentId, LocalDateTime createdAt, String createdAtStatus) {
        Page<Comments> page = query.toMpPage();
        QueryWrapper<Comments> queryWrapper = new QueryWrapper<>();

        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }

        if (commentId != null) {
            queryWrapper.eq("comment_id", commentId);
        }

        if (postId != null) {
            queryWrapper.eq("post_id", postId);
        }

        if (createdAt != null) {
            if (createdAtStatus.equals("before")) {
                queryWrapper.lt("created_at", createdAt);
            } else if (createdAtStatus.equals("after")) {
                queryWrapper.gt("created_at", createdAt);
            }
        }

        page(page,queryWrapper);

        return ToPage.of(page,Comments.class);
    }


}
