package com.project.humanresource.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorType {


    BADREQUEST(4002, "The provided parameters are invalid.", BAD_REQUEST),
    PASSWORD_MISMATCH(4003, "The entered passwords do not match.", BAD_REQUEST),
    EMAIL_PASSWORD_ERROR(4004, "Incorrect username or password.", BAD_REQUEST),
    EMAIL_ALREADY_EXISTS(4006, "Email already exists.", BAD_REQUEST),
    CATEGORY_ALREADY_EXISTS(4005, "This category is already registered.", BAD_REQUEST),
    ALREADY_EXISTS(4007, "The category already exists.", BAD_REQUEST),
    INVALID_TOKEN(4999, "Invalid user information.", FORBIDDEN),
    INVALID_PASSWORD(4007, "Invalid password.", FORBIDDEN),
    UNAUTHORIZED(4010, "Unauthorized.", FORBIDDEN),
    DUPLICATE_PERSONAL_FILE(4011, "Duplicate personal file.", FORBIDDEN),
    EXPIRED_TOKEN(4998,"Expired session",INTERNAL_SERVER_ERROR),
    USED_TOKEN(4997,"Used session",BAD_REQUEST),
    MAIL_SEND_FAILED(4800,"Email could not be sent",BAD_REQUEST),
    USER_NOT_ACTIVE(2003,"User not active",BAD_REQUEST),
    USER_NOT_FOUND(2004, "User not found.", INTERNAL_SERVER_ERROR),
    COMPANY_NOT_FOUND(2005,"Company not found.",INTERNAL_SERVER_ERROR),
    COMPANY_BRANCH_NOT_FOUND(2006,"Company branch not found.",INTERNAL_SERVER_ERROR),
    DEPARTMENT_NOT_FOUND(2007,"Department not found.",INTERNAL_SERVER_ERROR),
    INTERNAL_SERVER(5000, "An unexpected error occurred on the server.", INTERNAL_SERVER_ERROR),
    EMPLOYEE_NOT_FOUND(5001, "Employee not found.", BAD_REQUEST);


    int code;
    String message;
    HttpStatus httpStatus;

}
