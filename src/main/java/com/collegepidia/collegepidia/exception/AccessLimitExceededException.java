// File: exception/AccessLimitExceededException.java
package com.collegepidia.collegepidia.exception;

public class AccessLimitExceededException extends RuntimeException {
    public AccessLimitExceededException(String message) {
        super(message);
    }
}
