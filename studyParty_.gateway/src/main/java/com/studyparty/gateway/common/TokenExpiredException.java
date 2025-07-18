package com.studyparty.gateway.common;

import lombok.Getter;

@Getter
public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {
        super(message);
    }
}
