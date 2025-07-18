package com.studyparty.gateway.common;

import lombok.Getter;

@Getter
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}
