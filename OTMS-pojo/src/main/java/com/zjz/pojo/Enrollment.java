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
 * @since 2025-01-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("enrollment")
@ApiModel(value="Enrollment对象", description="")
public class Enrollment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "enrollment_id", type = IdType.AUTO)
    private Integer enrollmentId;

    private Integer courseId;

    private Integer studentId;

    private LocalDateTime enrollmentTime;

    private Integer acceptanceStatus;

    private Integer teacherId;


}
