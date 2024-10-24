package main.frame.apigat.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

//@Component
//public class GlobalErrorFilter extends AbstractGatewayFilterFactory<GlobalErrorFilter.Config> {
//
//    @Override
//    public GatewayFilter apply(Config config) {
//        return (exchange, chain) -> chain.filter(exchange)
//                .onErrorResume(throwable -> {
//                    ServerHttpResponse response = exchange.getResponse();
//                    response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
//                    return response.setComplete();
//                });
//    }
//
//    public static class Config {
//        // Настройки фильтра, если нужны
//    }
//}
