package com.studyParty.user.common;

import lombok.Getter;

@Getter
public class ResultException extends RuntimeException{
    private final String code;
    public ResultException(String code, String msg){
        super(msg);
        this.code = code;
    }
    public ResultException(String msg){
        super(msg);
        this.code = "500";
    }
}
