package com.project.humanresource.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorMessage> exceptionHandler(RuntimeException exception){
        System.out.println("Exception Unexpected error....:  " + exception);
        return createErrorMessage(exception,ErrorType.INTERNAL_SERVER,HttpStatus.INTERNAL_SERVER_ERROR,null);
    }

    @ExceptionHandler(HumanResourceException.class)
    @ResponseBody
    public ResponseEntity<ErrorMessage> HumanResourceException(HumanResourceException exception){
        return createErrorMessage(exception,exception.getErrorType(),exception.getErrorType().getHttpStatus(),null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ErrorMessage> methodArgumentNotValid(MethodArgumentNotValidException exception){
        List<String> fields = new ArrayList<>();
        exception.getBindingResult().getFieldErrors().forEach(field -> {
            fields.add(field.getField()+ " : "+ field.getDefaultMessage());
        });
        return createErrorMessage(exception,ErrorType.BADREQUEST, HttpStatus.BAD_REQUEST,fields);
    }



    /**
     * Burada oluşan tüm hataların bir formatı ve response type ı olması gerekli. Tüm hatalar
     * bir noktadan log lanmalılar.
     */
    public ResponseEntity<ErrorMessage> createErrorMessage(Exception exception,ErrorType errorType, HttpStatus httpStatus, List<String> fields){
       // log.error("Exception caught in GlobalExceptionHandler", exception);
        log.error("Error message : {}", errorType.getMessage()+ " - Fields : " +fields);
        return new ResponseEntity<>(ErrorMessage.builder()
                .code(errorType.getCode())
                .fields(fields)
                .isSuccess(false)
                .message(errorType.getMessage())
                .build(),httpStatus);
    }

}
