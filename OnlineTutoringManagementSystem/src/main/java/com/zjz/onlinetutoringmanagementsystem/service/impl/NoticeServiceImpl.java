package com.zjz.onlinetutoringmanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.onlinetutoringmanagementsystem.service.IFileInfoService;
import com.zjz.pojo.Notice;
import com.zjz.onlinetutoringmanagementsystem.mapper.NoticeMapper;
import com.zjz.onlinetutoringmanagementsystem.service.INoticeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjz.pojo.Result;
import com.zjz.pojo.ToPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zjz
 * @since 2025-02-28
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements INoticeService {

    @Autowired
    private IFileInfoService fileInfoService;

    @Autowired
    private NoticeMapper noticeMapper;

    @Override
    public ToPage<Notice> AdminGetNoticePage(PageQuery query, String title, String status) {

        Page<Notice> page = query.toMpPage();
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();

        if (!title.isEmpty()) {
            queryWrapper.like("title", title);
        }
        if (!status.isEmpty()) {
            queryWrapper.eq("announcement_status", status.equals("true"));
        }

        page(page, queryWrapper);

        return ToPage.of(page, Notice.class);
    }

    @Override
    @Transactional
    public void add(String title, String content, String status, MultipartFile attachments) {
        Notice notice = new Notice();
        notice.setTitle(title);
        notice.setContent(content);
        notice.setAnnouncementStatus(status.equals("true"));

        noticeMapper.insert(notice);

        if (attachments != null) {
            notice.setCoverImageId(fileInfoService.SingleFileSave(attachments, "NoticeImage", notice.getNotificationId(), notice.getNotificationId()));
            noticeMapper.updateById(notice);
        }

    }

    @Override
    public void updateNotice(Integer noticeId, String title, String content, String status, MultipartFile attachments) {
        Notice notice = noticeMapper.selectById(noticeId);
        notice.setTitle(title);
        notice.setContent(content);
        notice.setAnnouncementStatus(status.equals("true"));

        if (attachments != null) {
            //文件先删后增
            List<Integer> list = new ArrayList<>();
            list.add(notice.getCoverImageId());
            fileInfoService.deleteFilesByIdList(list);
            notice.setCoverImageId(fileInfoService.SingleFileSave(attachments, "NoticeImage", notice.getNotificationId(), notice.getNotificationId()));
        }
        noticeMapper.updateById(notice);
    }

    @Override
    public void DeleteNoticeById(Integer noticeId) {
        Notice notice = noticeMapper.selectById(noticeId);
        List<Integer> list = new ArrayList<>();
        list.add(notice.getCoverImageId());
        fileInfoService.deleteFilesByIdList(list);
        noticeMapper.deleteById(noticeId);
    }

    @Override
    public Result userGetAllNotice() {

        List<Notice> list = new ArrayList<>();
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("announcement_status", true);
        list = noticeMapper.selectList(queryWrapper);

        return Result.success(list);
    }
}
