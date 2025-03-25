package com.zjz.onlinetutoringmanagementsystem.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zjz.pojo.FileInfo;
import com.zjz.onlinetutoringmanagementsystem.mapper.FileInfoMapper;
import com.zjz.onlinetutoringmanagementsystem.service.IFileInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjz.pojo.Result;
import com.zjz.utils.FileStorageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zjz
 * @since 2025-02-14
 */
@Service
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements IFileInfoService {

    @Autowired
    FileInfoMapper fileInfoMapper;


    @Override
    public Integer SingleFileSave(MultipartFile file, String fileType, Integer uploaderId, Integer referenceId) {
        // 调用工具类上传文件并获取存储路径
        String path = FileStorageUtils.uploadSingleFile(file, fileType, uploaderId, referenceId);

        // 创建 FileInfo 对象并设置属性
        FileInfo fileInfo = new FileInfo();
        fileInfo.setUploaderId(uploaderId);                // 设置上传者 ID
        fileInfo.setFileName(file.getOriginalFilename());  // 设置文件名（包括扩展名）
        fileInfo.setFilePath(path);                        // 设置文件存储路径
        fileInfo.setUploadTime(LocalDateTime.now());       // 设置文件上传时间

        // 获取文件大小并设置到 FileInfo 对象中
        long fileSize = file.getSize();  // 获取文件大小（单位为字节）
        fileInfo.setFileSize(fileSize);  // 设置文件大小

        // 将 FileInfo 对象保存到数据库
        int result = fileInfoMapper.insert(fileInfo);

        // 检查插入是否成功
        if (result > 0) {
            // 插入成功，返回文件 ID
            return fileInfo.getFileId();
        } else {
            // 插入失败，抛出异常或返回 null
            throw new RuntimeException("文件信息保存失败");
        }
    }

    @Override
    public List<Integer> FilesSave(List<MultipartFile> files, String fileType, Integer uploaderId, Integer referenceId) {
        // 创建一个列表用于存储成功保存的文件 ID
        List<Integer> fileIds = new ArrayList<>();

        // 遍历文件列表，逐个处理每个文件
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                // 如果文件为空，跳过处理
                continue;
            }

            try {
                // 调用工具类上传文件并获取存储路径
                String path = FileStorageUtils.uploadSingleFile(file, fileType, uploaderId, referenceId);

                // 创建 FileInfo 对象并设置属性
                FileInfo fileInfo = new FileInfo();
                fileInfo.setUploaderId(uploaderId);                // 设置上传者 ID
                fileInfo.setFileName(file.getOriginalFilename());  // 设置文件名（包括扩展名）
                fileInfo.setFilePath(path);                        // 设置文件存储路径
                fileInfo.setUploadTime(LocalDateTime.now());       // 设置文件上传时间

                // 获取文件大小并设置到 FileInfo 对象中
                long fileSize = file.getSize();  // 获取文件大小（单位为字节）
                fileInfo.setFileSize(fileSize);  // 设置文件大小

                // 将 FileInfo 对象保存到数据库
                int result = fileInfoMapper.insert(fileInfo);

                // 检查插入是否成功
                if (result > 0) {
                    // 插入成功，获取文件 ID 并添加到列表中
                    fileIds.add(fileInfo.getFileId());
                } else {
                    // 插入失败，记录错误信息（可以选择抛出异常或继续处理其他文件）
                    throw new RuntimeException("文件信息保存失败: " + fileInfo.getFileName());
                }
            } catch (Exception e) {
                // 捕获文件上传或保存过程中可能出现的异常
                // 可以记录日志或抛出自定义异常
                e.printStackTrace();
                // 也可以选择跳过当前文件，继续处理下一个文件
                continue;
            }
        }

        // 返回所有成功保存的文件 ID 列表
        return fileIds;
    }

    @Override
    public Result GetFilesDetailsByIdList(List<Integer> idList) {

        if (idList == null || idList.isEmpty()) {
            return Result.success(Collections.emptyMap());
        }

        // 使用 MyBatis Plus 的 selectBatchIds 方法批量查询
        List<FileInfo> fileInfos = fileInfoMapper.selectBatchIds(idList);

        // 转换为前端需要的 Map 结构：文件名 -> {id, size}
        Map<String, Map<String, Object>> resultMap = fileInfos.stream()
                .collect(Collectors.toMap(
                        FileInfo::getFileName,
                        fileInfo -> {
                            Map<String, Object> details = new HashMap<>();
                            details.put("id", fileInfo.getFileId());
                            details.put("size", fileInfo.getFileSize());
                            return details;
                        },
                        // 处理重复键冲突（保留旧值或新值，根据业务需求调整）
                        (existing, replacement) -> existing
                ));

        return Result.success(resultMap);
    }

    @Override
    public Result deleteFilesByIdList(List<Integer> fileIds) {
        // 首先检查文件ID列表是否为空
        if (fileIds == null || fileIds.isEmpty()) {
            return Result.success("文件ID列表为空，无需操作");
        }
        try {
            // 使用 MyBatis Plus 的 selectBatchIds 方法批量查询文件信息
            List<FileInfo> fileInfos = fileInfoMapper.selectBatchIds(fileIds);
            // 如果没有找到对应的文件记录
            if (fileInfos.isEmpty()) {
                return Result.success("未找到对应文件，无需操作");
            }
            // 遍历文件信息列表，删除文件系统中的文件
            for (FileInfo fileInfo : fileInfos) {
                String filePath = fileInfo.getFilePath();
                if (filePath != null && !filePath.isEmpty()) {
                    FileStorageUtils.deleteFile(filePath);
                }
            }
            // 批量删除文件信息记录
            int affectedRows = fileInfoMapper.deleteBatchIds(fileIds);
            // 如果删除行数与传入的文件ID数量不一致，说明部分删除失败
            if (affectedRows != fileIds.size()) {
                return Result.error("部分文件删除失败");
            }
            return Result.success("文件删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("文件删除失败: " + e.getMessage());
        }
    }
}
