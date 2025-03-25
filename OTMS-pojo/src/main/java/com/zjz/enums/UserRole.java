package com.zjz.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum UserRole {
    student(1,"student"),
    parent(2, "parent"),
    teacher(3, "teacher"),
    admin(4, "admin");

    @EnumValue
    private final int value;
    @JsonValue
    private final String desc;


    UserRole(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    // 根据desc值查找枚举
    public static UserRole fromDesc(String desc) {
        if (desc == null || desc.isEmpty()){
            return null;
        }
        for (UserRole role : UserRole.values()) {
            if (role.getDesc().equalsIgnoreCase(desc)) {
                return role;
            }
        }
        throw new IllegalArgumentException("角色错误： " + desc);
    }
}
