package com.zjz.onlinetutoringmanagementsystem.service;

import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.pojo.Posts;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjz.pojo.Result;
import com.zjz.pojo.ToPage;

import java.time.LocalDateTime;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zjz
 * @since 2025-02-25
 */
public interface IPostsService extends IService<Posts> {

    Result createPost(String title, String content);

    Result GetPostsByQuery(PageQuery query, String title);

    Result GetStickiedPosts();

    Result adminDeletePost(Integer postId);

    ToPage<Posts> adminQueryPostPage(PageQuery query, Integer postId, Integer userId, String titleKeyword, LocalDateTime createdAt, String createdAtStatus, LocalDateTime lastUpdated, String lastUpdatedStatus, String isPinned);

    Result adminUpdatePostIsPinned(Integer postId, String isPinned);

    Result adminGetPostTrend(LocalDateTime startTime, LocalDateTime endTime);


}
