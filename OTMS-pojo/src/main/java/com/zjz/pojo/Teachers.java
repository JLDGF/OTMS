package com.zjz.pojo;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2025-03-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("teachers")
@ApiModel(value="Teachers对象", description="")
public class Teachers implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "teacher_user_id", type = IdType.AUTO)
    private Integer teacherUserId;

    private String teacherName;

    private String teacherUsername;

    private Integer age;

    private LocalDate registrationDate;

    private Integer activityCount;

    private Integer activityTime;

    private Float overallRating;

    private Integer totalPoints;

    @ApiModelProperty(value = "待结算课时费")
    private BigDecimal settlementPrice;

    private String contactInfo;

    private Integer rating;

    private String educationInfo;

    private String profile;


}
