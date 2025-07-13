package com.studyParty.user.Utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.studyParty.user.domain.User;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
@Log
@Component
public class TokenUtil {
    @Autowired
    private RedisUtil redisUtil;
    private final String sign = "yzt9345245374";
    public String createToken(User user){
        Algorithm algorithm = Algorithm.HMAC256(sign);
        String passwordHash = hashPassword(user.getPassword());
        if (redisUtil.getFromRedis(user.getPhone())==null){
            redisUtil.saveToRedis(user.getPhone(),passwordHash);
        }
        return JWT.create()
                .withIssuer("yzt")
                .withClaim("Id",user.getId())
                .withClaim("phone",user.getPhone())
                .withClaim("passwordHash", passwordHash)
                .withExpiresAt(new Date(System.currentTimeMillis()+60*60*1000))//1h
                .sign(algorithm);
    }

    private String hashPassword(String password) {
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

    public boolean tokenVerify(String token,User user){
        try {
            Algorithm algorithm = Algorithm.HMAC256(sign);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("yzt")
                    .withClaim("id",user.getId())
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            // 检查密码哈希值
            return hashPassword(user.getPassword()).equals(decodedJWT.getClaim("passwordHash").asString());
        }catch (JWTVerificationException e) {
            // Token验证失败
            System.err.println("Token verification failed: " + e.getMessage());
            return false;
        } catch (NullPointerException e) {
            // 空指针异常
            System.err.println("Null pointer exception: " + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            // 非法参数异常
            System.err.println("Illegal argument exception: " + e.getMessage());
            return false;
        } catch (Exception e) {
            // 其他未知异常
            System.err.println("Unknown exception: " + e.getMessage());
            return false;
        }
    }
    public String extractUsername(String token) {
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}
