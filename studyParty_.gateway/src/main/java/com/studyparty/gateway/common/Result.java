package com.studyparty.gateway.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.core.ResolvableType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Data // 自动生成getter、setter、toString等方法
@NoArgsConstructor
public class Result<T> {
    private Integer code;
    private String msg;
    private Object data; // 使用泛型类型T

    public Result(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    public Result(int code, String msg){
        this.code = code;
        this.msg = msg;
        this.data = null;
    }

    @Override
    public String toString() {
        return "{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public static Result success(){
        return new Result(200,"操作成功",null);
    }

    public static Result success(Object data){
        return new Result(200,"操作成功",data);
    }
    public  static Result success(Integer code,String msg){return new Result(code,msg,null);}
    public  static Result success(Integer code,String msg,Object data){return new Result(code,msg,data);}
    public static Result error(String msg){
        return new Result(500,msg,null);
    }

    public static Result error(Integer code,String msg){
        return new Result(code,msg,null);
    }

    public static Result error(){
        return new Result(500,"系统错误，请联系管理员",null);
    }
}