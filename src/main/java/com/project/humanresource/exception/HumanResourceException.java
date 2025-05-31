package com.project.humanresource.exception;

import lombok.Getter;

@Getter
public class HumanResourceException extends RuntimeException{
    private ErrorType errorType;
    public HumanResourceException(ErrorType errorType){
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    // New constructor
    public HumanResourceException(ErrorType errorType, String customMessage){
        super(customMessage);
        this.errorType = errorType;
    }
}
