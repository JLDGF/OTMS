package com.zjz.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ApprovalsStatus {
    PENDING(0, "pending"),
    AGREE(1, "agree"),
    REJECT(2,"reject");

    @EnumValue
    private final int value;
    @JsonValue
    private final String desc;

    ApprovalsStatus(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    // 根据 value 获取对应的枚举
    public static GradeStatus getGradeStatusByValue(int value) {
        for (GradeStatus gradeStatus : GradeStatus.values()) {
            if (gradeStatus.getValue() == value) {
                return gradeStatus;
            }
        }
        throw new IllegalArgumentException("Invalid grade status value: " + value);
    }
}

