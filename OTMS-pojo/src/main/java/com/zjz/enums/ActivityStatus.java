package com.zjz.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ActivityStatus {
    AGREED(1, "已同意"),
    REJECTED(2, "被拒绝"),
    TPENDING(3, "待教师处理"),
    PPENDING(4, "待家长处理"),
    EXPIRED(5, "已过期"),
    COMPLETED(6, "已完成"),
    SEVALUATED(7, "学生已评价"),
    TEVALUATED(8, "教师已评价"),
    OVER(9,"已评价");

    @EnumValue
    private final int code;
    @JsonValue
    private final String desc;

    ActivityStatus(int code, String description) {
        this.code = code;
        this.desc = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return desc;
    }

    /**
     * 根据代码获取对应的活动状态枚举
     * @param code 状态代码
     * @return 对应的活动状态枚举，如果找不到则返回null
     */
    public static ActivityStatus getStatusByCode(int code) {
        for (ActivityStatus status : ActivityStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }

    /**
     * 根据描述获取对应的活动状态枚举
     * @param desc 状态描述
     * @return 对应的活动状态枚举，如果找不到则返回null
     */
    public static ActivityStatus getStatusByDescription(String desc) {
        for (ActivityStatus status : ActivityStatus.values()) {
            if (status.getDescription().equals(desc)) {
                return status;
            }
        }
        return null;
    }
}