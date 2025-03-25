package com.zjz.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("课程状态枚举")
public enum CourseStatus {
    @ApiModelProperty(value = "未开始", example = "0")
    NOT_STARTED(0, "未开始"),

    @ApiModelProperty(value = "进行中", example = "1")
    IN_PROGRESS(1, "进行中"),

    @ApiModelProperty(value = "已结束", example = "2")
    ENDED(2, "已结束");

    @EnumValue  // MyBatis-Plus 标识存储到数据库的值
    private final Integer code;
    private final String description;

    CourseStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    // 根据code获取枚举
    public static CourseStatus getByCode(Integer code) {
        for (CourseStatus value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }

    // Getter
    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}