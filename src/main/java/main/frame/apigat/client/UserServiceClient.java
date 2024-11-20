package main.frame.apigat.client;

//import main.frame.shared.dto.UserDTO;

import main.frame.apigat.LoginRequest;
import main.frame.shared.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AuthServiceClient {

    private final WebClient webClient;

    public AuthServiceClient(@LoadBalanced WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://auth-service").build();  // Устанавливаем базовый URL для AuthService
    }

    // Получение данных пользователя
//    public Mono<UserDTO> getUserDetails(String email, String token) {
//        System.out.println("Отправка запроса в AuthService для email: " + email);
//        return webClient.get()
//                .uri("/auth/user?email={email}", email) // Используем эндпоинт для получения данных пользователя
//                .headers(headers -> headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token))
//                .exchangeToMono(response -> {
//                    System.out.println("Ответ от AuthService, статус: " + response.statusCode());
//                    if (response.statusCode().is2xxSuccessful()) {
//                        return response.bodyToMono(UserDTO.class);  // Получаем UserDTO вместо UserDetails
//                    } else {
//                        System.out.println("Ошибка при запросе к AuthService, статус: " + response.statusCode());
//                        return Mono.error(new RuntimeException("Не удалось получить данные пользователя"));
//                    }
//                });
//    }

    public Mono<String> auth(String email, String password) {
        System.out.println("Отправка запроса в AuthService.");
        LoginRequest loginRequest = new LoginRequest(email, password);

        return webClient
                .post()
                .uri("/auth/login")
                .body(Mono.just(loginRequest), LoginRequest.class)
                .exchangeToMono(response -> {
                    System.out.println("Status code: " + response.statusCode().value());

                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(String.class);
                    } else {
                        System.err.println("Ошибка при запросе к AuthService, статус: " + response.statusCode());
                        return Mono.error(new RuntimeException("Ошибка авторизации: " + response.statusCode()));
                    }
                })
                .doOnNext(token -> System.out.println("Получен токен: " + token))
                .doOnError(e -> System.err.println("Ошибка: " + e.getMessage()))
                .onErrorResume(e -> Mono.empty());

    }

    // Метод для получения данных пользователя
    public Mono<UserDTO> getUserDetails(String token) {
        return webClient
                .get()
                .uri("/auth/user") // Эндпоинт AuthService для получения данных пользователя
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) // Передаем токен в заголовке
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError, // Используем лямбда-функцию для проверки ошибки
                        response -> {
                            System.out.println("Ошибка при запросе к AuthService, статус: " + response.statusCode());
                            return Mono.error(new RuntimeException("Не удалось получить данные пользователя"));
                        }
                )
                .bodyToMono(UserDTO.class);
    }
}