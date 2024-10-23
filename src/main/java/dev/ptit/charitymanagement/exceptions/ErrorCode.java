package dev.ptit.charitymanagement.exceptions;

import lombok.Getter;
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
    PHONE_INVALID( 2008,"Phone must be not null", HttpStatus.BAD_REQUEST),
    RESET_PASSWORD_CODE_INVALID( 20010,"Reset password code invalid", HttpStatus.BAD_REQUEST),
    REPEAT_PASSWORD_NOT_MATCHING( 2011,"Repeat password not matching", HttpStatus.BAD_REQUEST),
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
