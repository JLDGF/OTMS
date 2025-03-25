package com.zjz.onlinetutoringmanagementsystem.service;

import com.zjz.pojo.FileInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjz.pojo.Result;
import io.swagger.models.auth.In;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zjz
 * @since 2025-02-14
 */
public interface IFileInfoService extends IService<FileInfo> {

    /**
     * 上传单个文件
     *
     * @param file        文件
     * @param fileType    文件类型（如 "teacher-assignment" 或 "student-submission"）
     * @param uploaderId  上传者ID
     * @param referenceId 对应ID（如附件ID或作业ID）
     * @return 文件id
     */
    Integer SingleFileSave(MultipartFile file,String fileType, Integer uploaderId,Integer referenceId);

    /**
     * 上传多个文件
     *
     * @param files       文件列表
     * @param fileType    文件类型（如 "teacher-assignment" 或 "student-submission"）
     * @param uploaderId  上传者ID
     * @param referenceId 对应ID（如附件ID或作业ID）
     * @return 文件id列表
     */
    List<Integer> FilesSave(List<MultipartFile> files,String fileType, Integer uploaderId,Integer referenceId);

    Result GetFilesDetailsByIdList(List<Integer> idList);

    Result deleteFilesByIdList(List<Integer> fileIds);
}
