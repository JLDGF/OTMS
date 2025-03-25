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
@TableName("parents")
@ApiModel(value="Parents对象", description="")
public class Parents implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "parent_user_id", type = IdType.AUTO)
    private Integer parentUserId;

    private Integer studentUserId;

    private String parentUsername;

    private String parentName;

    private LocalDate registrationDate;

    private String contactInfo;

    @ApiModelProperty(value = "待交付课时费")
    private BigDecimal confirmPrice;

    private String profile;


}
