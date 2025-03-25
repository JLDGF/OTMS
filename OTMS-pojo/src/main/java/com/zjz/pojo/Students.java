package com.zjz.pojo;

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
@TableName("students")
@ApiModel(value="Students对象", description="")
public class Students implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "student_user_id", type = IdType.AUTO)
    private Integer studentUserId;

    private Integer parentUserId;

    private String studentUsername;

    private String studentName;

    private Integer rating;

    private String grade;

    private LocalDate registrationDate;

    private Integer activityCount;

    private Integer activityTime;

    private Float overallRating;

    private Integer totalPoints;

    private String contactInfo;

    private String profile;


}
