package com.zjz.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum GradeStatus {

    PENDING(1, "pending"),
    GRADED(2, "graded");

    @EnumValue
    private final int value;
    @JsonValue
    private final String desc;

    GradeStatus(int value, String desc) {
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

    // 根据 desc 获取对应的枚举
    public static GradeStatus getGradeStatusByDesc(String desc) {
        for (GradeStatus gradeStatus : GradeStatus.values()) {
            if (gradeStatus.getDesc().equals(desc)) {
                return gradeStatus;
            }
        }
        throw new IllegalArgumentException("Invalid grade status description: " + desc);
    }

}