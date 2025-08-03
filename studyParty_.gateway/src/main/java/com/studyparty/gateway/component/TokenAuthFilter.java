package com.studyparty.gateway.component;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.studyparty.gateway.common.Result;
import com.studyparty.gateway.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.List;


// 过滤器
@Component
public class TokenAuthFilter implements GlobalFilter, Ordered {

    // 放行的路径（如登录、注册）
    private static final List<String> ALLOWED_PATHS = Arrays.asList(
            "/user/login",
            "/user/register",
            "/studyParty-user/login",
            "/studyParty-user/register",
            "/doc.html",
            "/studyParty-user/v3/api-docs",
            "/studyParty-group/v3/api-docs",
            "/studyParty-websocket/v3/api-docs",
            "/static/**"
    );

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();
        // 1. 检查是否在白名单中
        if (isAllowedPath(path)) {
            return chain.filter(exchange); // 直接放行
        }

        // 2. 从请求头获取 Token
        String token = request.getHeaders().getFirst("Authorization");
        if (StringUtils.isEmpty(token)) {
            return unauthorized(exchange.getResponse(), "无权限");
        }
        // 3. 异步校验 Token（非阻塞）
        return validateTokenAsync(token)
                .flatMap(userId -> {
                    if (userId != null && !userId.isEmpty()) {
                        exchange.mutate()
                                .request(builder -> builder.header("X-User-Id", userId))
                                .build();
                        return chain.filter(exchange); // 校验通过，继续执行
                    } else {
                        return unauthorized(exchange.getResponse(), "无效 Token");
                    }
                })
                .onErrorResume(JWTVerificationException.class, ex -> {
                    return writeError(exchange, "权限错误", HttpStatus.UNAUTHORIZED);
                })
                .onErrorResume(IllegalArgumentException.class, ex -> {
                    return writeError(exchange, "参数错误: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
                })
                .onErrorResume(RuntimeException.class, ex -> {
                    return writeError(exchange, "服务器错误: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                })
                .onErrorResume(Exception.class, ex -> {
                    return writeError(exchange, "权限校验失败: " + ex.getMessage(), HttpStatus.FORBIDDEN);
                })
                .then();


    }
    
    // 异步校验 Token（示例）
    private Mono<String> validateTokenAsync(String token) {
        return jwtUtil.tokenVerify(token)
                .subscribeOn(Schedulers.boundedElastic()); // 将阻塞操作调度到弹性线程池
    }
    private final Jackson2JsonEncoder jsonEncoder = new Jackson2JsonEncoder();

    private Mono<Void> writeError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        Result<?> error = new Result<>(status.value(), message);
        return jsonEncoder.encode(Mono.just(error), response.bufferFactory(), ResolvableType.forClass(Result.class), MediaType.APPLICATION_JSON, null)
                .flatMap(buffer -> response.writeWith(Mono.just(buffer)))
                .then();
    }
    // 返回 401 未授权响应
    private Mono<Void> unauthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        Result<?> error = new Result<>(401, message);
        return jsonEncoder.encode(Mono.just(error), response.bufferFactory(), ResolvableType.forClass(Result.class), MediaType.APPLICATION_JSON, null)
                .flatMap(buffer -> response.writeWith(Mono.just(buffer))).then();
    }

    // 检查路径是否在白名单
    private boolean isAllowedPath(String path) {
        return ALLOWED_PATHS.stream().anyMatch(path::startsWith);
    }

    @Override
    public int getOrder() {
        return -100; // 高优先级
    }
}