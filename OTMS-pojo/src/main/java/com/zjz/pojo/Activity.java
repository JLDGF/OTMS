package com.zjz.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.Duration;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.zjz.enums.ActivityStatus;
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
 * @since 2025-01-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("activity")
@ApiModel(value="Activity对象", description="")
public class Activity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "activity_id", type = IdType.AUTO)
    private Integer activityId;

    private String activityName;

    private LocalDateTime scheduledStartTime;

    private LocalDateTime scheduledEndTime;

    private String location;

    private Integer courseId;

    private Integer teacherId;

    private Integer studentId;

    private ActivityStatus activityStatus;

    private String activityDescription;

    public int calculateClassHours() {
        if (scheduledStartTime == null || scheduledEndTime == null) {
            return 0;
        }
        long minutes = Duration.between(scheduledStartTime, scheduledEndTime).toMinutes();
        return (int) Math.ceil(minutes / 60.0);
    }
}
