package com.zjz.pojo;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName("level_rules")
@ApiModel(value="LevelRules对象", description="")
public class LevelRules implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "rating", type = IdType.INPUT)
    private Integer rating;

    @ApiModelProperty(value = "该等级对应的最大课时费")
    private BigDecimal maxClassFee;

    @ApiModelProperty(value = "该等级对应的缴费折扣")
    private BigDecimal paymentDiscount;


}
