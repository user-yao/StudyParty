package com.studyparty.gateway.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *      JWT.create()
 *         .withIssuer("yzt")
 *         .withClaim("Id",user.getId())
 *         .withClaim("phone",user.getPhone())
 *         .withClaim("passwordHash", hashPassword(user.getPassword()))
 *         .withSubject(user.getName())
 *         .withExpiresAt(new Date(System.currentTimeMillis()+60*60*1000))//1h
 *         .sign(algorithm);
 */
@Component
@Log
public class JwtUtil {

    @Autowired
    private RedisUtil redisUtil;

    private final String sign = "yzt9345245374";

    private String hashPassword(String password) {// 加密密码哈希
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    public Mono<String> tokenVerify(String token){// 验证token
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            if(!decodedJWT.getIssuer().equals("yzt")){
                return Mono.just(null); // 假设这是一个返回Mono<Boolean>的方法
            }
            return redisUtil.getValue(decodedJWT.getClaim("phone").asString())// key == phone value = passwordHash
                    .map(userPassword -> {
                        if (!userPassword.equals(decodedJWT.getClaim("passwordHash").asString())){
                            return null;
                        }
                        // 检查密码哈希值
                        return decodedJWT.getClaim("Id").toString();
                    })
                    .defaultIfEmpty(null); // 如果没有找到对应的用户密码，默认返回false
        }catch (JWTVerificationException e) {
            // Token验证失败
            System.err.println("Token verification failed: " + e.getMessage());
            log.info("token验证失败: " + e.getMessage());
            return Mono.just(null);
        } catch (NullPointerException e) {
            // 空指针异常
            System.err.println("Null pointer exception: " + e.getMessage());
            log.info("空指针异常: " + e.getMessage());
            return Mono.just(null);
        } catch (IllegalArgumentException e) {
            // 非法参数异常
            System.err.println("Illegal argument exception: " + e.getMessage());
            log.info("非法参数异常: " + e.getMessage());
            return Mono.just(null);
        } catch (Exception e) {
            // 其他未知异常
            System.err.println("Unknown exception: " + e.getMessage());
            log.info("未知异常: " + e.getMessage());
            return Mono.just(null);
        }
    }
}
