package dev.ptit.charitymanagement.entity;

public enum NotificationType {
    EMAIL(0),
    PUSH(1),
    ;
    NotificationType(Integer code){
        this.code = code;
    }

    private Integer code;
}
