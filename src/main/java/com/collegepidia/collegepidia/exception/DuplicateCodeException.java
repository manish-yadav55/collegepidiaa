package com.collegepidia.collegepidia.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when attempting to create a resource with a duplicate code under the same parent.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateCodeException extends RuntimeException {
    public DuplicateCodeException(String message) {
        super(message);
    }
}
