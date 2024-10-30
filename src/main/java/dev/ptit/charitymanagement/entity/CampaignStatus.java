package dev.ptit.charitymanagement.entity;

public enum CampaignStatus {
    PENDING(1,"Campaign is pending"),
    RUNNING(2, "Campaign is running"),
    ENDED(3, "Campaign was ended"),
    ;
    CampaignStatus(Integer code, String status){
        this.code = code;
        this.status = status;
    }
    private Integer code;
    private String status;
}