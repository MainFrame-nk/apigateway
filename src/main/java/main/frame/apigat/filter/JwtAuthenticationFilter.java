package main.frame.apigat.filter;

//import main.frame.apigat.AuthServiceClient;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.stereotype.Component;
//import main.frame.shared.JwtUtil;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;


//@Component
//public class JwtAuthenticationFilter implements WebFilter {
//    private final JwtUtil jwtUtil;
//    private final AuthServiceClient authServiceClient;
//
//    public JwtAuthenticationFilter(JwtUtil jwtUtil, AuthServiceClient authServiceClient) {
//        this.jwtUtil = jwtUtil;
//        this.authServiceClient = authServiceClient;
//    }
//
//    @Override
//    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
//        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return chain.filter(exchange); // Если токена нет, пропускаем запрос дальше
//        }
//
//        String token = authHeader.substring(7);
//        String userEmail = jwtUtil.extractEmail(token);
//
//        // Проверка токена на валидность
//        if (!jwtUtil.validateToken(token, userEmail)) {
//            System.out.println("Token invalid " +  jwtUtil.validateToken(token, userEmail));
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        }
//
//        System.out.println("Auth header: " + authHeader);
//        System.out.println("Token: " +  token);
//        System.out.println("User email from token: " +  userEmail);
//        System.out.println("Is token valid: " +  jwtUtil.validateToken(token, userEmail));
//
//        // Получение деталей пользователя из AuthService
//        return authServiceClient.getUserDetails(userEmail, token)
//                .flatMap(userDTO -> {
//                    System.out.println("Получен UserDTO от AuthService: " + userDTO);
//
//                    // Преобразуем UserDTO в UserDetails
//                    UserDetails userDetails = new org.springframework.security.core.userdetails.User(
//                            userDTO.getEmail(),
//                            "",  // Пароль не нужен для аутентификации через токен
//                            userDTO.getRoles().stream()
//                                    .map(role -> new SimpleGrantedAuthority(role.getName()))
//                                    .collect(Collectors.toList())
//                    );
//                    System.out.println("Преобразован UserDetails: " + userDetails);
//                    System.out.println("Роли: " + userDetails.getAuthorities());
//
//                    // Установка аутентификации в SecurityContext
//                    UsernamePasswordAuthenticationToken authentication =
//                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//
//                    // Проверка, авторизован ли пользователь
//                    if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
//                        System.out.println("Пользователь авторизован: " + userDetails.getUsername());
//                        System.out.println(SecurityContextHolder.getContext().getAuthentication());
//                    }
//
//                    return chain.filter(exchange);  // Пропускаем запрос дальше
//                })
//                .onErrorResume(e -> {
//                    System.err.println("Ошибка при вызове AuthService: " + e.getMessage());
//                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                    return exchange.getResponse().setComplete();
//                });
//    }
//}