package com.zjz.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.zjz.enums.GradeStatus;
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
 * @since 2025-02-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("assignment_submissions")
@ApiModel(value="AssignmentSubmissions对象", description="")
public class AssignmentSubmissions implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "submission_id", type = IdType.AUTO)
    private Integer submissionId;

    private Integer assignmentId;

    private LocalDateTime submitTime;

    private GradeStatus gradeStatus;

    private LocalDateTime gradeTime;

    private String feedback;

    private String content;

    private String attachmentIds;

    private Integer bonusPoints;

    private Integer score;

    private Integer studentId;


}
