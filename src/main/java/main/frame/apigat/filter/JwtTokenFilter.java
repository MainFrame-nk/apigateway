package main.frame.apigat.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

//@Component
//public class JwtTokenFilter extends AbstractGatewayFilterFactory<JwtTokenFilter.Config> {
//
//    public JwtTokenFilter() {
//        super(Config.class);
//    }
//
//    @Override
//    public GatewayFilter apply(Config config) {
//        return (exchange, chain) -> {
//            ServerHttpRequest request = exchange.getRequest();
//            String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//
//            if (token == null || !token.startsWith("Bearer ")) {
//                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                return exchange.getResponse().setComplete();
//            }
//
//            return chain.filter(exchange);
//        };
//    }
//
//    @Bean
//    public GlobalFilter customFilter() {
//        return (exchange, chain) -> {
//            System.out.println("Headers: " + exchange.getRequest().getHeaders());
//            return chain.filter(exchange);
//        };
//    }
//
//    public static class Config {
//        // Настройки фильтра, если они необходимы
//    }
//}