package com.zjz.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.zjz.enums.ApprovalsStatus;
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
 * @since 2025-03-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("approvals")
@ApiModel(value="Approvals对象", description="")
public class Approvals implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "audit_id", type = IdType.AUTO)
    private Integer auditId;

    @ApiModelProperty(value = "发送者ID")
    private Integer userId;

    @ApiModelProperty(value = "用户提交审核的内容")
    private String content;

    @ApiModelProperty(value = "管理员审核意见")
    private String approvalComments;

    private ApprovalsStatus approvalsStatus;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updatedAt;


}
