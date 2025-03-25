package com.zjz.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 作业状态枚举
 */
public enum AssignmentStatus {
    ACTIVE(1,"进行中"), // 作业正在进行中
    ENDED(2,"已结束");  // 作业已结束

    @EnumValue
    private final int code;

    @JsonValue
    private final String description;

    // 构造函数
    AssignmentStatus(int code,String description) {
        this.code = code;
        this.description = description;
    }

    // 获取状态描述
    public String getDescription() {
        return description;
    }

    // 根据描述获取枚举值（可选方法）
    public static AssignmentStatus fromDescription(String description) {
        for (AssignmentStatus status : AssignmentStatus.values()) {
            if (status.getDescription().equals(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid description: " + description);
    }
}
