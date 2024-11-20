package main.frame.apigat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
//ComponentScan(basePackages = {"main.frame.shared", "main.frame"})  // Обязательно добавь sharedmodule
public class ApigatApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApigatApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/auth/**")
                        .uri("lb://auth-service"))
//                .route("user-service", r -> r.path("/user/**")
//                        .uri("lb://USER-SERVICE"))
                .build();
    }
}
