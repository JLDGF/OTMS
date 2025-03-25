package com.zjz.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.zjz.enums.AssignmentStatus;
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
 * @since 2025-02-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("assignment")
@ApiModel(value="Assignment对象", description="")
public class Assignment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "assignment_id", type = IdType.AUTO)
    private Integer assignmentId;

    private Integer teacherId;

    private Integer courseId;

    private String assignmentName;

    private String assignmentRequirements;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private AssignmentStatus assignmentStatus;

    private String attachmentId;

    private String releaseTarget;


}
