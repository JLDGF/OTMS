package com.zjz.onlinetutoringmanagementsystem.service;

import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.pojo.Comments;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjz.pojo.Posts;
import com.zjz.pojo.Result;
import com.zjz.pojo.ToPage;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zjz
 * @since 2025-02-25
 */
public interface ICommentsService extends IService<Comments> {

    void UserCommentByPostId(Integer postId, String content);

    void UserLike(Integer commentId);

    Result GetCommentsByPostId(PageQuery query, Integer postId);

    Result LikeItOrNot(Integer commentId);

    Result adminDeleteComment(Integer commentId);

    Result adminDeleteComments(List<Integer> IdList);

    ToPage<Comments> adminGetCommentPage(PageQuery query, Integer userId, Integer postId, Integer commentId, LocalDateTime createdAt, String createdAtStatus);
}
