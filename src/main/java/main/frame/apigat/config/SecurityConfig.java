package main.frame.apigat.config;

//import main.frame.apigat.filter.JwtAuthenticationFilter;
import main.frame.apigat.filter.TokenPresenceFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    public SecurityConfig1(JwtAuthenticationFilter jwtAuthenticationFilter) {
//        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//    }
    @Autowired
    private TokenPresenceFilter tokenPresenceFilter;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorize -> authorize
                        .pathMatchers("/auth/login", "/auth/register").permitAll()  // Публичные маршруты
                        .anyExchange().authenticated()  // Все остальные требуют аутентификации
                )
                .addFilterBefore(tokenPresenceFilter, SecurityWebFiltersOrder.AUTHENTICATION)  // Добавляем фильтр
                //    .addFilterAfter(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)  // Добавляем JWT фильтр
                .build();
    }
}