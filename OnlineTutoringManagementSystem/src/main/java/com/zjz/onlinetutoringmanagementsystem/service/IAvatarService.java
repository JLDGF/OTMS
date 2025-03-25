package com.zjz.onlinetutoringmanagementsystem.service;

import com.zjz.pojo.Avatar;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zjz
 * @since 2025-02-23
 */
public interface IAvatarService extends IService<Avatar> {

    void avatarUpload(MultipartFile avatar);
}
