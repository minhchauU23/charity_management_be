package dev.ptit.charitymanagement.entity;

public enum NotificationStatus {
    PENDING(0),
    SENDED(1),
    FAILED(2)
    ;
    NotificationStatus(Integer code){
        this.code = code;
    }

    private Integer code;
}
