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
 * @since 2025-02-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("posts")
@ApiModel(value="Posts对象", description="")
public class Posts implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "post_id", type = IdType.AUTO)
    private Integer postId;

    private Integer userId;

    private String title;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime lastUpdated;

    private Boolean isPinned;


}
