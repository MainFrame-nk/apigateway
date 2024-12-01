package main.frame.apigat.client;

import main.frame.apigat.dto.request.CreateLobbyRequest;
import main.frame.apigat.dto.request.LoginRequest;
import main.frame.apigat.dto.request.RegisterRequest;
import main.frame.shared.dto.LobbyDTO;
import main.frame.shared.dto.UserDTO;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class LobbyServiceClient {
    private final WebClient webClient;

    public LobbyServiceClient(@LoadBalanced WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://lobby-service").build();  // Устанавливаем базовый URL для AuthService
    }

    public Mono<Void> createLobby(CreateLobbyRequest createLobbyRequest, String token) {
        return webClient
                .post()
                .uri("/lobby/create")  // Эндпоинт для регистрации
                .bodyValue(createLobbyRequest)  // Передаем данные для регистрации
                .headers(headers -> headers.set(HttpHeaders.AUTHORIZATION, token))  // Передача токена для авторизации
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> {
                    System.err.println("Ошибка при создании лобби, статус: " + response.statusCode());
                    return Mono.error(new RuntimeException("Ошибка создания лобби: " + response.statusCode()));
                })
                .bodyToMono(Void.class)  // Ожидаем пустой ответ
                .doOnTerminate(() -> System.out.println("Лобби успешно создано"))
                .onErrorResume(e -> Mono.empty());  // Если ошибка, вернем пустое значение
    }

//    public Mono<List<UserDTO>> getAllUsers(String token) {
//        return webClient
//                .get()
//                .uri("/lobby/")  // Эндпоинт, по которому получаем данные пользователя
//                .headers(headers -> headers.set(HttpHeaders.AUTHORIZATION, token))  // Передаем токен в заголовке
//                .retrieve()
//                .onStatus(
//                        HttpStatusCode::isError,  // Если ошибка, генерируем исключение
//                        response -> {
//                            System.out.println("Ошибка при запросе к UserService, статус: " + response.statusCode());
//                            return Mono.error(new RuntimeException("Не удалось получить данные пользователя"));
//                        }
//                )
//                .bodyToFlux(UserDTO.class)  // Преобразуем ответ в поток UserDTO
//                .collectList()  // Преобразуем поток в список
//                .doOnError(e -> System.err.println("Ошибка при запросе к UserService: " + e.getMessage()))  // Логируем ошибку
//                .onErrorResume(e -> Mono.empty());  // Возвращаем пустой Mono в случае ошибки
//    }

//    public Mono<Void> deleteUser(Long id, String token) {
//        return webClient
//                .delete()
//                .uri("/users/" + id)  // Эндпоинт для удаления пользователя
//                .headers(headers -> headers.set(HttpHeaders.AUTHORIZATION, token))  // Передача токена для авторизации
//                .retrieve()
//                .bodyToMono(Void.class)  // Ожидаем пустое тело в ответе
//                .doOnError(e -> System.err.println("Ошибка при удалении пользователя: " + e.getMessage()));  // Логируем ошибку
//        // .doOnError(e -> log.error("Ошибка при удалении пользователя: {}", e.getMessage()));
//    }


    public Mono<ResponseEntity<LobbyDTO>> getLobbyById(Long id, String token) {
        return webClient
                .get()
                .uri("/lobby/" + id) // Эндпоинт для получения пользователя по ID
                .headers(headers -> headers.set(HttpHeaders.AUTHORIZATION, token))
                .retrieve()
                .toEntity(LobbyDTO.class) // Преобразуем ответ в ResponseEntity с UserDTO
                .doOnNext(response -> System.out.println("Lobby response: " + response))
                .doOnError(e -> System.err.println("Ошибка при запросе к LobbyService: " + e.getMessage())) // Логируем ошибку
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null))); // Возвращаем ошибку при сбое
    }
}
