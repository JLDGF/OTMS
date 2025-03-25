package com.zjz.onlinetutoringmanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjz.onlinetutoringmanagementsystem.mapper.CommentsMapper;
import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.onlinetutoringmanagementsystem.service.ICommentsService;
import com.zjz.onlinetutoringmanagementsystem.service.IUsersService;
import com.zjz.pojo.Comments;
import com.zjz.pojo.Posts;
import com.zjz.onlinetutoringmanagementsystem.mapper.PostsMapper;
import com.zjz.onlinetutoringmanagementsystem.service.IPostsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjz.pojo.Result;
import com.zjz.pojo.ToPage;
import com.zjz.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zjz
 * @since 2025-02-25
 */
@Service
public class PostsServiceImpl extends ServiceImpl<PostsMapper, Posts> implements IPostsService {

    @Autowired
    PostsMapper postsMapper;

    @Autowired
    IUsersService usersService;

    @Autowired
    ICommentsService commentsService;

    @Autowired
    CommentsMapper commentsMapper;

    @Override
    public Result createPost(String title, String content) {

        Posts post = new Posts();
        post.setTitle(title);
        post.setContent(content);
        post.setCreatedAt(LocalDateTime.now());
        post.setLastUpdated(LocalDateTime.now());
        post.setUserId(usersService.selectIdByUserName(SecurityUtils.getCurrentUsername()));

        if (SecurityUtils.getCurrentUserRole().equals("ROLE_admin")) {
            post.setIsPinned(true);
        }
        postsMapper.insert(post);

        return Result.success("发布成功");
    }

    @Override
    public Result GetPostsByQuery(PageQuery query, String title) {
        try {
            Page<Posts> page = query.toMpPage();
            QueryWrapper<Posts> queryWrapper = new QueryWrapper<>();

            //指定查询字段
            queryWrapper
                    .eq("is_pinned", false)
                    .select("post_id", "title", "created_at", "last_updated");

            if (title != null && !title.isEmpty()) {
                queryWrapper.like("title", title);
            }

            page(page, queryWrapper);
            return Result.success(ToPage.of(page, Posts.class));
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error("查询失败");
        }
    }

    @Override
    public Result GetStickiedPosts() {
        try {
            PageQuery query = new PageQuery();
            query.setPageNo(1L);
            query.setPageSize(10L);
            query.setSortBy("created_at");
            query.setIsAsc(false);
            Page<Posts> page = query.toMpPage();

            QueryWrapper<Posts> queryWrapper = new QueryWrapper<>();
            queryWrapper
                    .eq("is_pinned", true)
                    .select("post_id", "title", "created_at", "last_updated");

            page(page, queryWrapper);
            return Result.success(ToPage.of(page, Posts.class));
        } catch (Exception e) {
            log.error(e.toString());
            return Result.error("查询失败");
        }
    }

    @Override
    @Transactional
    public Result adminDeletePost(Integer postId) {
        //删除帖子并删除有关评论
        postsMapper.deleteById(postId);

        QueryWrapper<Comments> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("post_id", postId)
                .select("comment_id");


        if (commentsMapper.exists(queryWrapper)) {
            //获取该帖子的所有评论的ID
            List<Integer> list = commentsService.list(queryWrapper)
                    .stream()
                    .map(Comments::getCommentId) // 提取commentId
                    .collect(Collectors.toList()); // 收集到List中

            commentsService.adminDeleteComments(list);
        }


        return Result.success("删除成功");

    }

    @Override
    public ToPage<Posts> adminQueryPostPage(
            PageQuery query, Integer postId, Integer userId,
            String titleKeyword, LocalDateTime createdAt,
            String createdAtStatus, LocalDateTime lastUpdated,
            String lastUpdatedStatus, String isPinned) {
        Page<Posts> page = query.toMpPage();

        QueryWrapper<Posts> queryWrapper = new QueryWrapper<>();

        if (postId != null) {
            queryWrapper.eq("post_id", postId);
        }

        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }

        if (titleKeyword != null && !titleKeyword.isEmpty()) {
            queryWrapper.like("title", titleKeyword);
        }

        if (createdAt != null) {
            if (createdAtStatus.equals("before")) {
                queryWrapper.lt("created_at", createdAt);
            } else if (createdAtStatus.equals("after")) {
                queryWrapper.gt("created_at", createdAt);
            }
        }

        if (lastUpdated != null) {
            if (lastUpdatedStatus.equals("before")) {
                queryWrapper.lt("last_updated", lastUpdated);
            } else if (lastUpdatedStatus.equals("after")) {
                queryWrapper.gt("last_updated", lastUpdated);
            }
        }

        if (isPinned != null && !isPinned.isEmpty()) {
            queryWrapper.eq("is_pinned", isPinned.equals("true"));
        }

        queryWrapper.select("post_id", "user_id", "title", "created_at", "last_updated", "is_pinned");

        page(page, queryWrapper);
        return ToPage.of(page, Posts.class);

    }

    @Override
    public Result adminUpdatePostIsPinned(Integer postId, String isPinned) {
        QueryWrapper<Posts> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", postId);

        if (postsMapper.exists(queryWrapper)) {
            Posts posts = postsMapper.selectOne(queryWrapper);
            posts.setIsPinned(isPinned.equals("true"));
            postsMapper.updateById(posts);
            return Result.success();
        }
        return Result.error("置顶失败");
    }

    @Override
    public Result adminGetPostTrend(LocalDateTime startTime, LocalDateTime endTime) {
        // 1. 生成完整日期范围
        List<LocalDate> allDates = new ArrayList<>();
        LocalDate currentDate = startTime.toLocalDate();
        LocalDate endDate = endTime.toLocalDate();

        while (!currentDate.isAfter(endDate)) {
            allDates.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }

        // 2. 初始化默认值为0的映射表
        Map<LocalDate, Long> dateCountMap = allDates.stream()
                .collect(Collectors.toMap(
                        date -> date,
                        date -> 0L
                ));

        // 3. 查询数据库并更新存在记录的日期
        QueryWrapper<Posts> queryWrapper = new QueryWrapper<>();
        queryWrapper.between("created_at", startTime, endTime);

        List<Posts> postsList = this.list(queryWrapper);

        postsList.stream()
                .collect(Collectors.groupingBy(
                        post -> post.getCreatedAt().toLocalDate(),
                        Collectors.counting()
                ))
                .forEach((date, count) -> dateCountMap.put(date, count));

        // 4. 构建有序结果列表
        List<Map<String, Object>> dataList = allDates.stream()
                .map(date -> {
                    Map<String, Object> dataMap = new HashMap<>();
                    dataMap.put("date", date.format(DateTimeFormatter.ISO_DATE));
                    dataMap.put("count", dateCountMap.get(date));
                    return dataMap;
                })
                .collect(Collectors.toList());

        return Result.success(dataList);
    }


}
