package main.frame.apigat.controller;


import lombok.AllArgsConstructor;
import main.frame.apigat.client.AuthServiceClient;
import main.frame.apigat.dto.request.LoginRequest;
import main.frame.apigat.dto.request.RegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthServiceClient authServiceClient;

    @PostMapping("/login")
    public Mono<ResponseEntity<String>> getUser(@RequestBody LoginRequest request) {
        System.out.println("Отправляем запрос в AuthService для входа.");
        return authServiceClient.login(request.getEmail(), request.getPassword())
                .map(token -> ResponseEntity.status(HttpStatus.OK).body(token))
                .doOnNext(token -> System.out.println("Токен получен: " + token))
                .doOnError(e -> System.err.println("Ошибка получения токена: " + e.getMessage()))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ошибка авторизации")));
    }

    // Регистрация пользователя
    @PostMapping("/register")
    public Mono<ResponseEntity<String>> register(@RequestBody RegisterRequest registerRequest) {
        return authServiceClient.register(registerRequest)
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("Пользователь успешно зарегистрирован"))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка регистрации")));
    }
}
