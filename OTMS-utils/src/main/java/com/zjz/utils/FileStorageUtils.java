package com.zjz.utils;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class FileStorageUtils {

//    @Value("${file.storage.root-path}")
    private static final String ROOT_PATH = "E:\\OTMS-UPLOAD\\";

    // 存储文件的根路径（可以在配置文件中配置）
    //    private static final String ROOT_PATH = "/path/to/your/storage/directory/";


    // 私有构造器，防止实例化
    private FileStorageUtils() {
    }

    /**
     * 上传单个文件
     *
     * @param file        文件
     * @param fileType    文件类型（如 "teacher-assignment" 或 "student-submission"）
     * @param uploaderId  上传者ID
     * @param referenceId 对应ID（如附件ID或作业ID）
     * @return 文件存储路径
     */
    public static String uploadSingleFile(MultipartFile file, String fileType, Integer uploaderId, Integer referenceId) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 生成存储路径
        String filePath = generateFilePath(fileType, uploaderId.toString(), referenceId.toString(), file.getOriginalFilename());

        System.out.println(ROOT_PATH + filePath);

        File destFile = new File(ROOT_PATH + filePath);

        // 确保目录存在
        destFile.getParentFile().mkdirs();

        // 将上传的文件复制到目标路径
        try (InputStream inputStream = file.getInputStream();
             OutputStream outputStream = new FileOutputStream(destFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
        return filePath;
    }

    /**
     * 上传多个文件
     *
     * @param files       文件列表
     * @param fileType    文件类型（如 "teacher-assignment" 或 "student-submission"）
     * @param uploaderId  上传者ID
     * @param referenceId 对应ID（如附件ID或作业ID）
     * @return 文件存储路径列表
     */
    public static List<String> uploadMultipleFiles(List<MultipartFile> files, String fileType, Integer uploaderId, Integer referenceId) {
        List<String> filePaths = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String filePath = uploadSingleFile(file, fileType, uploaderId, referenceId);
                filePaths.add(filePath);
            }
        }

        return filePaths;
    }

    /**
     * 生成文件存储路径
     *
     * @param fileType       文件类型
     * @param uploaderId     上传者ID
     * @param referenceId    对应ID
     * @param originalFileName 文件原始名称
     * @return 格式化的文件路径
     */
    private static String generateFilePath(String fileType, String uploaderId, String referenceId, String originalFileName) {
        // 示例路径格式：{fileType}/{uploaderId}/{referenceId}/{originalFileName_时间戳}
        return fileType + "/" + uploaderId + "/" + referenceId + "/" + System.currentTimeMillis() + "_" + originalFileName;

    }

    /**
     * 获取文件存储的完整路径（包括根路径）
     *
     * @param filePath 文件路径（相对于根路径）
     * @return 完整路径
     */
    public static String getFullFilePath(String filePath) {
        return ROOT_PATH + filePath;
    }

    public static void deleteFile(String filePath) {
        File file = new File(ROOT_PATH + filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    // 文件类型白名单验证
    public static boolean isValidFileType(String contentType) {
        // 定义允许的文件类型
        Set<String> allowedContentTypes = new HashSet<>(Arrays.asList(
                "application/pdf", // PDF 文件
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // Word 文件 (.docx)
                "application/vnd.ms-excel", // Excel 文件 (.xls)
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // Excel 文件 (.xlsx)
                "application/vnd.ms-powerpoint", // PowerPoint 文件 (.ppt)
                "application/vnd.openxmlformats-officedocument.presentationml.presentation", // PowerPoint 文件 (.pptx)
                "application/msword", // Word 文件 (.doc)
                "application/zip", // ZIP 压缩文件
                "application/x-rar-compressed", // RAR 压缩文件
                "application/x-7z-compressed",// 7Z 压缩文件
                "video/mp4", // MP4 视频
                "image/jpeg", // JPEG 图像
                "image/png", // PNG 图像
                "text/plain" // 文本文件 (.txt)
        ));

        // 检查文件类型是否在白名单中
        return allowedContentTypes.contains(contentType);
    }

    public static ResponseEntity<Resource> downloadFileByPathAndName(String filePath,String fileName){
        String FullFilePath = FileStorageUtils.getFullFilePath(filePath);
        File file = new File(FullFilePath);
        if (file.exists()) {
            HttpHeaders headers = new HttpHeaders();
            try {
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + URLEncoder.encode(fileName, StandardCharsets.UTF_8) + "\"");
                headers.add(HttpHeaders.CONTENT_TYPE, Files.probeContentType(file.toPath()));
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .body(new FileSystemResource(file));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
