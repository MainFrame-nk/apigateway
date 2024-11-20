package main.frame.apigat.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Component
public class TokenPresenceFilter implements org.springframework.web.server.WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        // Пропускаем маршруты для логина и регистрации
        if (exchange.getRequest().getURI().getPath().startsWith("/auth/login") ||
                exchange.getRequest().getURI().getPath().startsWith("/auth/register")) {
            return chain.filter(exchange);
        }

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // Если заголовка нет или он неверный, возвращаем 401
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            System.out.println("Token not found, continuing...");
            return exchange.getResponse().setComplete(); // Завершаем обработку
        }
        exchange.getRequest().getHeaders()
                .forEach((key, value) -> System.out.println("Request Header: " + key + " -> " + value));

        System.out.println("Token found: " + authorizationHeader);
        return chain.filter(exchange);
    }
}