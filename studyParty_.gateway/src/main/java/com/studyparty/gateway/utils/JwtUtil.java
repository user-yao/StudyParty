package com.studyparty.gateway.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.AbstractMap;
import java.util.Map;

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

    public Mono<String> tokenVerify(String token) {
        return Mono.defer(() -> {
                    try {
                        DecodedJWT decodedJWT = JWT.decode(token);
                        Algorithm algorithm = Algorithm.HMAC256(sign);
                        if (!"yzt".equals(decodedJWT.getIssuer())) {
                            return Mono.error(new JWTVerificationException("无效发行商"));
                        }
                        if (decodedJWT.getSignature().equals(algorithm.toString())){
                            return Mono.error(new JWTVerificationException("无效签名"));
                        }
                        decodedJWT.getSignature();
                        String phone = decodedJWT.getClaim("phone").asString();
                        String passwordHash = decodedJWT.getClaim("passwordHash").asString();
                        if (phone == null || passwordHash == null) {
                            return Mono.error(new Throwable("令牌错误"));
                        }
                        return redisUtil.getValue(phone)
                                .switchIfEmpty(Mono.error(new RuntimeException("服务器错误，请重新登录")))
                                .publishOn(Schedulers.boundedElastic())
                                .mapNotNull(storedHash -> {
                                    if (!storedHash.equals(passwordHash)) {
                                        return Mono.<String>error(new Throwable("密码错误")).block();
                                    }
                                    return decodedJWT.getSubject(); // 或返回用户信息
                                })
                                .onErrorResume(ex -> {
                                    return Mono.error(new RuntimeException("服务器错误，请重新登录", ex));
                                });
                    } catch (JWTVerificationException ex) {
                        return Mono.error(new Throwable("权限错误"));
                    } catch (Exception ex) {
                        return Mono.error(new RuntimeException("权限校验失败", ex));
                    }
                })
                .onErrorResume(JWTVerificationException.class,ex -> Mono.error(new JWTVerificationException(ex.getMessage(),ex)))
                .onErrorResume(Throwable.class,ex -> Mono.error(new Throwable(ex.getMessage(),ex)))
                .onErrorResume(RuntimeException.class,ex -> Mono.error(new RuntimeException(ex.getMessage(),ex)))
                .onErrorResume(Exception.class, ex -> Mono.error(new Exception(ex.getMessage(), ex)));
    }

}
