package com.zjz.pojo;

import java.math.BigDecimal;
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
 * @since 2025-03-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("fee_transactions")
@ApiModel(value="FeeTransactions对象", description="")
public class FeeTransactions implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "交易ID")
    @TableId(value = "transaction_id", type = IdType.AUTO)
    private Integer transactionId;

    @ApiModelProperty(value = "活动ID")
    private Integer activityId;

    @ApiModelProperty(value = "教师ID")
    private Integer teacherId;

    @ApiModelProperty(value = "学生ID")
    private Integer studentId;

    @ApiModelProperty(value = "教师应得")
    private BigDecimal feeAmount;

    @ApiModelProperty(value = "家长应缴")
    private BigDecimal realFeeAmount;

    private Boolean feeStatus;

    @ApiModelProperty(value = "支付时间")
    private LocalDateTime paymentDate;

    @ApiModelProperty(value = "记录创建时间")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "记录更新时间")
    private LocalDateTime updatedAt;


}
