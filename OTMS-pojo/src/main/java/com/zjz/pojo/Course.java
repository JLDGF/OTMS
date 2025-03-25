package com.zjz.pojo;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.zjz.enums.CourseStatus;
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
 * @since 2025-03-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("course")
@ApiModel(value="Course对象", description="")
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "course_id", type = IdType.AUTO)
    private Integer courseId;

    private String courseName;

    private String subject;

    private Integer maxStudents;

    private Integer registeredStudents;

    @ApiModelProperty(value = "每小时费用")
    private BigDecimal hourlyRate;

    private Integer teacherId;

    private LocalDateTime classTime;

    private LocalDateTime classEndTime;

    private String classLocation;

    @ApiModelProperty(value = "课程状态（0: 未开始；1: 进行中；2: 已结束）")
    private CourseStatus courseStatus;

    private String courseDescription;


}
