package com.Product.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException{
    private static final long serialVersionUID = 1l;
    private HttpStatus status;
    
    public ApiException(HttpStatus status, String message){
        super(message);
        this.status = status;
 
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

}
