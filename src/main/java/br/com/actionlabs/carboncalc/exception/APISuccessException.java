package br.com.actionlabs.carboncalc.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class APISuccessException extends RuntimeException {
    private HttpStatus httpStatus;

    public APISuccessException(){
    }

    public APISuccessException(String message) {
        super(message);
    }

    public APISuccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public APISuccessException (String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public APISuccessException(String title, String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
