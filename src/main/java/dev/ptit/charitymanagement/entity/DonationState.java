package dev.ptit.charitymanagement.entity;

import dev.ptit.charitymanagement.exceptions.AppException;
import dev.ptit.charitymanagement.exceptions.ErrorCode;

public enum DonationState {
    PENDING(0),
    ACCEPT(1),
    FAILED(2),
    ;
    DonationState(Integer code){
        this.code = code;
    }

    private Integer code;
}
