package com.studyparty.gateway.component;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.studyparty.gateway.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private static final List<String> ALLOWED_PATHS = Arrays.asList("/user/login", "/user/register","/user/update","/studyparty-user/**");

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();

        // 1. 检查是否在白名单中
        if(true){
//        if (isAllowedPath(path)) {
            return chain.filter(exchange); // 直接放行
        }

        // 2. 从请求头获取 Token
        String token = request.getHeaders().getFirst("Authorization");
        if (StringUtils.isEmpty(token)) {
            return unauthorized(exchange.getResponse(), "无权限");
        }

        // 3. 异步校验 Token（非阻塞）
        return validateTokenAsync(token)
                .flatMap(isValid -> {
                    if (!isValid.isEmpty()) {
                        String userId = isValid;
                        exchange.mutate()
                                .request(builder -> builder.header("X-User-Id", userId))
                                .build();
                        return chain.filter(exchange); // 校验通过，继续执行
                    } else {
                        return unauthorized(exchange.getResponse(), "Invalid token");
                    }
                });
    }

    // 异步校验 Token（示例）
    private Mono<String> validateTokenAsync(String token) {
        return jwtUtil.tokenVerify(token)
                .subscribeOn(Schedulers.boundedElastic()); // 将阻塞操作调度到弹性线程池
    }
    // 返回 401 未授权响应
    private Mono<Void> unauthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = String.format("{\"code\": 401, \"message\": \"%s\"}", message);
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes());
        return response.writeWith(Mono.just(buffer));
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