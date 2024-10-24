package main.frame.apigat.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

//@Component
//public class AddRequestHeaderFilter extends AbstractGatewayFilterFactory<AddRequestHeaderFilter.Config> {
//
//    public AddRequestHeaderFilter() {
//        super(Config.class);
//    }
//
//    @Override
//    public GatewayFilter apply(Config config) {
//        return (exchange, chain) -> {
//            exchange.getRequest().mutate().header("X-Request-ID", UUID.randomUUID().toString());
//            return chain.filter(exchange);
//        };
//    }
//
//    public static class Config {
//        // Настройки фильтра, если нужны
//    }
//}
