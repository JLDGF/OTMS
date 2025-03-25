package com.zjz.onlinetutoringmanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zjz.onlinetutoringmanagementsystem.mapper.FileInfoMapper;
import com.zjz.onlinetutoringmanagementsystem.service.IFileInfoService;
import com.zjz.onlinetutoringmanagementsystem.service.IUsersService;
import com.zjz.pojo.Avatar;
import com.zjz.onlinetutoringmanagementsystem.mapper.AvatarMapper;
import com.zjz.onlinetutoringmanagementsystem.service.IAvatarService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjz.utils.FileStorageUtils;
import com.zjz.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zjz
 * @since 2025-02-23
 */
@Service
public class AvatarServiceImpl extends ServiceImpl<AvatarMapper, Avatar> implements IAvatarService {

    @Autowired
    IFileInfoService fileInfoService;

    @Autowired
    IUsersService usersService;

    @Autowired
    AvatarMapper avatarMapper;

    @Override
    public void avatarUpload(MultipartFile avatarFile ) {
        Integer userId = usersService.selectIdByUserName(SecurityUtils.getCurrentUsername());

        // 调用工具类上传文件并获取存储路径
        String path = FileStorageUtils.uploadSingleFile(avatarFile, "avatar", userId, userId);

        QueryWrapper<Avatar> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);

        Avatar avatar = new Avatar();
        avatar.setUserId(userId);
        avatar.setAvatarPath(path);

        if (avatarMapper.exists(queryWrapper)){
            String oldPath = avatarMapper.selectOne(queryWrapper).getAvatarPath();
            FileStorageUtils.deleteFile(oldPath);

            avatarMapper.updateById(avatar);
        }else {
            avatarMapper.insert(avatar);
        }
    }
}
