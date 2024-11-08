package dev.ptit.charitymanagement.entity;

import dev.ptit.charitymanagement.exceptions.AppException;
import dev.ptit.charitymanagement.exceptions.ErrorCode;
import jakarta.persistence.Entity;

public enum CampaignStatus {
    CREATED(1,"Campaign is pending"),
    SCHEDULED(2, "Campaign was scheduled"),
    STARTED(3, "Campaign was started"),
    RUNNING(4, "Campaign is running"),
    ENDED(5, "Campaign was ended"),
    REJECTED(6, "Campaign was rejected"),
    STOPPED(7, "Campaign was stopped"),
    RESULTED(8, "Campaign was resulted")
    ;
    CampaignStatus(Integer code, String status){
        this.code = code;
        this.status = status;
    }
    public static CampaignStatus fromString(String status) {
        try {
            return CampaignStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.CAMPAIGN_STATUS_INVALID);
        }
    }
    private Integer code;
    private String status;
}
