package dev.ptit.charitymanagement.dtos;

public enum SimpleMessageAction {
    NEW_STATUS(0),
    NEW_COMMENT(1),
    NEW_DONATION(2),
    NEW_DETAIL(3),
    NEW_CAMPAIGN(4),
    UPDATE_COMMENT(5),
    DELETE_COMMENT(6)
    ;
    SimpleMessageAction(Integer code){
        this.code = code;
    }

    private Integer code;
}
