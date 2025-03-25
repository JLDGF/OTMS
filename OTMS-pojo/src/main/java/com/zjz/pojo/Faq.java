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
 * @since 2025-03-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("faq")
@ApiModel(value="Faq对象", description="")
public class Faq implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "faq_id", type = IdType.AUTO)
    private Integer faqId;

    private String questionTitle;

    private String questionContent;

    private String questionCategory;

    private String answerContent;

    private LocalDateTime createTime;

    private Boolean isValid;


}
