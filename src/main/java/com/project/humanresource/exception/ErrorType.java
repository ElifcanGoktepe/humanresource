package com.project.humanresource.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorType {

    COMMENT_NOT_FOUND(2004 ,"There is no such comment", HttpStatus.NOT_FOUND ),
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
    EMPLOYEE_NOT_FOUND(5001, "Employee not found.", BAD_REQUEST),
    CURRENT_PASSWORD_INCORRECT(4012, "Current password is incorrect.", BAD_REQUEST),
    SHIFT_NOT_FOUND(4080, "Shift not found.", BAD_REQUEST),
    UNAUTHORIZED_ACCESS(4081,"Unauthorized access", BAD_REQUEST),
    // File Upload Errors
    FILE_UPLOAD_ERROR(4013, "File upload failed.", BAD_REQUEST),
    FILE_NOT_FOUND(4014, "File not found or empty.", BAD_REQUEST),
    FILE_SIZE_TOO_LARGE(4015, "File size exceeds maximum limit (5MB).", BAD_REQUEST),
    INVALID_FILE_TYPE(4016, "Invalid file type. Only JPG, JPEG, PNG, GIF are allowed.", BAD_REQUEST),
    INVALID_FILE_NAME(4017, "Invalid file name.", BAD_REQUEST),
    INVALID_FILE_EXTENSION(4018, "Invalid file extension.", BAD_REQUEST),
    ASSIGNMENT_NOT_FOUND(4019, "Assignment not found.", NOT_FOUND);


    int code;
    String message;
    HttpStatus httpStatus;

}
