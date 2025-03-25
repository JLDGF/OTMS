package com.zjz.onlinetutoringmanagementsystem.service;

import com.zjz.onlinetutoringmanagementsystem.query.PageQuery;
import com.zjz.pojo.Notice;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjz.pojo.Result;
import com.zjz.pojo.ToPage;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zjz
 * @since 2025-02-28
 */
public interface INoticeService extends IService<Notice> {

    ToPage<Notice> AdminGetNoticePage(PageQuery query,String title,String status);

    void add(String title, String content, String status, MultipartFile attachments);

    void updateNotice(Integer noticeId, String title, String content, String announcementStatus, MultipartFile attachments);

    void DeleteNoticeById(Integer noticeId);

    Result userGetAllNotice();
}
