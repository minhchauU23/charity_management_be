package dev.ptit.charitymanagement.entity;

import dev.ptit.charitymanagement.exceptions.AppException;
import dev.ptit.charitymanagement.exceptions.ErrorCode;
import jakarta.persistence.Entity;

public enum CampaignStatus {
    CREATED(1),
    SCHEDULED(2),
    STARTED(3),
    ENDED(4),
    REJECTED(5),
    STOPPED(6),
    RESULTED(7)
    ;
    CampaignStatus(Integer code){
        this.code = code;
    }
    public static CampaignStatus fromString(String status) {
        try {
            return CampaignStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.CAMPAIGN_STATUS_INVALID);
        }
    }
    private Integer code;
}
