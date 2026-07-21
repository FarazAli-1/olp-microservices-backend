package com.centegy.user_service.model.enums;

import lombok.Getter;

@Getter
public enum RoleLevel {
    ADMIN(1),
    INSTRUCTOR(2),
    STUDENT(3);


    private final int level;


    RoleLevel(int level) {
        this.level = level;
    }
}
