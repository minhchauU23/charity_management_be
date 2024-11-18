package dev.ptit.charitymanagement.exceptions;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.http.protocol.HTTP;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION( 1000,"Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY( 1001,"Uncategorized error", HttpStatus.BAD_REQUEST),
    BAD_REQUEST(1002, "Bad request", HttpStatus.BAD_REQUEST),
    END_POINT_NOT_FOUND(1003, "Endpoint not found", HttpStatus.NOT_FOUND),

    UNAUTHENTICATED( HttpStatus.UNAUTHORIZED.value(), "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED( HttpStatus.FORBIDDEN.value(), "You do not have permission", HttpStatus.FORBIDDEN),

    USER_EXISTED( 2001,"User existed", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED( 2002,"User not existed", HttpStatus.NOT_FOUND),
    BAD_CREDENTIALS(2003, "Username or password incorrect", HttpStatus.UNAUTHORIZED),

    EMAIL_INVALID(2004,"Email must be not null and a valid format", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID( 2005,"Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    FIRST_NAME_INVALID( 2006,"First name must be not null", HttpStatus.BAD_REQUEST),
    LAST_NAME_INVALID( 2007,"Last name must be not null", HttpStatus.BAD_REQUEST),
    PHONE_INVALID( 2008,"Phone must be not null and only number", HttpStatus.BAD_REQUEST),
    DOB_INVALID(2009,"Date of birth must be not null", HttpStatus.BAD_REQUEST),
    ADDRESS_INVALID(2010,"Address must be not null", HttpStatus.BAD_REQUEST),
    RESET_PASSWORD_CODE_INVALID( 2011,"Reset password code invalid", HttpStatus.BAD_REQUEST),
    REPEAT_PASSWORD_NOT_MATCHING( 2012,"Repeat password not matching", HttpStatus.BAD_REQUEST),
    IMAGE_NAME_INVALID(2013, "Image name invalid", HttpStatus.BAD_REQUEST),

    ROLE_NOT_EXISTED(2021, "Role not existed", HttpStatus.NOT_FOUND),
    ROLE_NAME_INVALID(2022, "Role name must be not null", HttpStatus.BAD_REQUEST),
    USER_ROLE_INVALID(2023, "User must be have one role", HttpStatus.BAD_REQUEST),

    CAMPAIGN_STATUS_INVALID(2024, "Campaign status invalid", HttpStatus.BAD_REQUEST),
    CAMPAIGN_STATUS_INVALID_ORDER(2025, "Campaign status order invalid", HttpStatus.BAD_REQUEST),
    CAMPAIGN_STATUS_NOT_EXISTED(2026, "Campaign status not existed", HttpStatus.NOT_FOUND),
    CAMPAIGN_NOT_EXISTED(2027, "Campaign not existed", HttpStatus.NOT_FOUND),
    CAMPAIGN_TITLE_INVALID(2028, "Title of campaign invalid", HttpStatus.BAD_REQUEST),
    CAMPAIGN_FUNDRAISING_GOAL_INVALID(2029, "Fundraising goal of campaign invalid", HttpStatus.BAD_REQUEST),
    CAMPAIGN_SHORT_DESCRIPTION_INVALID(2030, "Short description of campaign invalid", HttpStatus.BAD_REQUEST),
    CAMPAIGN_CONTENT_INVALID(2031, "Content of campaign invalid", HttpStatus.BAD_REQUEST),

    CATEGORY_NAME_INVALID(2033, "Category name must be not null", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_EXISTED(2034, "Category not existed", HttpStatus.NOT_FOUND),
    ;

    ErrorCode( Integer code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
