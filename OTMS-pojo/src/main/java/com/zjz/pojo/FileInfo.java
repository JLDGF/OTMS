package com.zjz.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author zjz
 * @since 2025-02-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("file_info")
@ApiModel(value="FileInfo对象", description="")
public class FileInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "文件唯一标识符")
    @TableId(value = "file_id", type = IdType.AUTO)
    private Integer fileId;

    @ApiModelProperty(value = "上传者ID")
    private Integer uploaderId;

    @ApiModelProperty(value = "文件名（包括扩展名）")
    private String fileName;

    @ApiModelProperty(value = "文件存储路径")
    private String filePath;

    @ApiModelProperty(value = "文件大小")
    private Long fileSize;

    @ApiModelProperty(value = "文件上传时间")
    private LocalDateTime uploadTime;


}
