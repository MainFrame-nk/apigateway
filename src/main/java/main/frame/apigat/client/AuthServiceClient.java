package main.frame.apigat.client;

//import main.frame.shared.dto.UserDTO;

import main.frame.apigat.dto.request.LoginRequest;
import main.frame.apigat.dto.request.RegisterRequest;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AuthServiceClient {

    private final WebClient webClient;

    public AuthServiceClient(@LoadBalanced WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://auth-service").build();  // Устанавливаем базовый URL для AuthService
    }

    public Mono<String> login(String email, String password) {
        System.out.println("Отправка запроса в AuthService.");
        LoginRequest loginRequest = new LoginRequest(email, password);

        return webClient
                .post()
                .uri("/auth/login")
                .body(Mono.just(loginRequest), LoginRequest.class)
                .exchangeToMono(response -> {
                    System.out.println("Response details: " + response);

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

    // Метод для логина, получения токена Возможно верный!!!!!!!!
//    public Mono<String> login(String email, String password) {
//        System.out.println("Отправка запроса в AuthService для логина.");
//        LoginRequest loginRequest = new LoginRequest(email, password);
//
//        return webClient
//                .post()
//                .uri("/auth/login")  // Эндпоинт для логина
//                .bodyValue(loginRequest)  // Передаем данные логина
//                .retrieve()
//                .bodyToMono(String.class)  // Ожидаем JWT токен в ответ
//                .doOnNext(token -> System.out.println("Получен токен: " + token))
//                .doOnError(e -> System.err.println("Ошибка при логине: " + e.getMessage()))
//                .onErrorResume(e -> Mono.empty());  // Обработка ошибок и возврат пустого значения при ошибке
//    }

    // Метод для регистрации пользователя в AuthService
    public Mono<Void> register(RegisterRequest registerRequest) {
        return webClient.post()
                .uri("/auth/register")  // Эндпоинт для регистрации
                .bodyValue(registerRequest)  // Передаем данные для регистрации
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> {
                    System.err.println("Ошибка при регистрации, статус: " + response.statusCode());
                    return Mono.error(new RuntimeException("Ошибка регистрации: " + response.statusCode()));
                })
                .bodyToMono(Void.class)  // Ожидаем пустой ответ
                .doOnTerminate(() -> System.out.println("Регистрация завершена"))
                .onErrorResume(e -> Mono.empty());  // Если ошибка, вернем пустое значение
    }
}