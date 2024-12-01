package main.frame.apigat.client;

import main.frame.apigat.dto.request.ChangePasswordRequest;
import main.frame.shared.dto.RoleDTO;
import main.frame.shared.dto.UserDTO;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceClient {

    private final WebClient webClient;

    public UserServiceClient(@LoadBalanced WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://user-service").build();  // Устанавливаем базовый URL для AuthService
    }

    public Mono<UserDTO> getUserDetails(String token) {
        return webClient
                .get()
                .uri("/users/user")  // Эндпоинт, по которому получаем данные пользователя
                .headers(headers -> headers.set(HttpHeaders.AUTHORIZATION, token))  // Передаем токен в заголовке
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,  // Если ошибка, генерируем исключение
                        response -> {
                            System.out.println("Ошибка при запросе к UserService, статус: " + response.statusCode());
                            return Mono.error(new RuntimeException("Не удалось получить данные пользователя"));
                        }
                )
                .bodyToMono(UserDTO.class)  // Преобразуем ответ в объект UserDTO
                .doOnError(e -> System.err.println("Ошибка при запросе к UserService: " + e.getMessage()))  // Логируем ошибку
                .onErrorResume(e -> Mono.empty());  // Возвращаем пустой Mono в случае ошибки
    }

    public Mono<List<UserDTO>> getAllUsers(String token) {
        return webClient
                .get()
                .uri("/users/")  // Эндпоинт, по которому получаем данные пользователя
                .headers(headers -> headers.set(HttpHeaders.AUTHORIZATION, token))  // Передаем токен в заголовке
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,  // Если ошибка, генерируем исключение
                        response -> {
                            System.out.println("Ошибка при запросе к UserService, статус: " + response.statusCode());
                            return Mono.error(new RuntimeException("Не удалось получить данные пользователя"));
                        }
                )
                .bodyToFlux(UserDTO.class)  // Преобразуем ответ в поток UserDTO
                .collectList()  // Преобразуем поток в список
                .doOnError(e -> System.err.println("Ошибка при запросе к UserService: " + e.getMessage()))  // Логируем ошибку
                .onErrorResume(e -> Mono.empty());  // Возвращаем пустой Mono в случае ошибки
    }

    public Mono<Void> deleteUser(Long id, String token) {
        return webClient
                .delete()
                .uri("/users/" + id)  // Эндпоинт для удаления пользователя
                .headers(headers -> headers.set(HttpHeaders.AUTHORIZATION, token))  // Передача токена для авторизации
                .retrieve()
                .bodyToMono(Void.class)  // Ожидаем пустое тело в ответе
                .doOnError(e -> System.err.println("Ошибка при удалении пользователя: " + e.getMessage()));  // Логируем ошибку
        // .doOnError(e -> log.error("Ошибка при удалении пользователя: {}", e.getMessage()));
    }



    // Метод для получения пользователя по ID
    public Mono<ResponseEntity<UserDTO>> getUserById(Long id, String token) {
        return webClient
                .get()
                .uri("/users/" + id) // Эндпоинт для получения пользователя по ID
                .headers(headers -> headers.set(HttpHeaders.AUTHORIZATION, token))
                .retrieve()
                .toEntity(UserDTO.class) // Преобразуем ответ в ResponseEntity с UserDTO
                .doOnNext(response -> System.out.println("User response: " + response))
                .doOnError(e -> System.err.println("Ошибка при запросе к UserService: " + e.getMessage())) // Логируем ошибку
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null))); // Возвращаем ошибку при сбое
    }

    // Обновление пользователя
    public Mono<ResponseEntity<UserDTO>> updateUser(Long id, UserDTO userDTO, String token) {
        return webClient
                .put()
                .uri("/users/" + id)  // Эндпоинт для обновления пользователя по ID
                .headers(headers -> headers.set(HttpHeaders.AUTHORIZATION, token))  // Передача токена
                .bodyValue(userDTO)  // Передаем обновленные данные пользователя
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.createException()
                        .flatMap(Mono::error))  // Обработка ошибок
                .toEntity(UserDTO.class)  // Ожидаем ответ в виде обновленного объекта UserDTO
                .doOnNext(response -> System.out.println("Пользователь успешно обновлен: " + response))
                        //log.info("User updated: {}", response))
                .doOnError(e -> System.out.println("Ошибка при обновлении пользователя: " + e.getMessage()))
                        //log.error("Ошибка при обновлении пользователя: {}", e.getMessage()))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(null)));  // Возвращаем пустой объект в случае ошибки
    }

    public Mono<ResponseEntity<UserDTO>> updateUserRoles(Long id, List<String> roles, String token) {
        return webClient
                .put()
                .uri("/users/" + id + "/roles")
                .headers(headers -> headers.set(HttpHeaders.AUTHORIZATION, token))  // Передаем токен
                .bodyValue(roles)  // Список ролей
                .retrieve()
                .toEntity(UserDTO.class)
                .doOnNext(response -> System.out.println("Роли пользователя обновлены: " + response))
                .doOnError(e -> System.err.println("Ошибка при обновлении ролей: " + e.getMessage()));
    }

    public Mono<ResponseEntity<List<UserDTO>>> getUsersByRole(String roleName, String token) {
        return webClient
                .get()
                .uri("/role/" + roleName)
                .headers(headers -> headers.set(HttpHeaders.AUTHORIZATION, token))  // Передаем токен в заголовке
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,  // Если ошибка, генерируем исключение
                        response -> {
                            System.out.println("Ошибка при запросе к UserService, статус: " + response.statusCode());
                            return Mono.error(new RuntimeException("Не удалось получить данные пользователя"));
                        }
                )
                .toEntity(new ParameterizedTypeReference<List<UserDTO>>() {})  // Преобразуем ответ в список UserDTO
                .doOnError(e -> System.err.println("Ошибка при запросе к UserService: " + e.getMessage()))  // Логируем ошибку
                .onErrorResume(e -> Mono.empty());  // Возвращаем пустой Mono в случае ошибки
    }

    public Mono<ResponseEntity<List<UserDTO>>> searchUsers(String email, String username, String phoneNumber,
                                                           Boolean active, LocalDateTime dateOfCreated, String roleName, String token) {
        return webClient
                .get()
                .uri(uriBuilder -> {
                    UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/search"); // Correct usage of UriComponentsBuilder
                    if (email != null) builder.queryParam("email", email);
                    if (username != null) builder.queryParam("username", username);
                    if (phoneNumber != null) builder.queryParam("phoneNumber", phoneNumber);
                    if (active != null) builder.queryParam("active", active);
                    if (dateOfCreated != null) builder.queryParam("dateOfCreated", dateOfCreated);
                    if (roleName != null) builder.queryParam("roleName", roleName);
                    return builder.build().toUri(); // Correct method to build URI
                })
              //  .header(HttpHeaders.AUTHORIZATION, token)
                .headers(headers -> headers.set(HttpHeaders.AUTHORIZATION, token))  // Передаем токен
                .retrieve()
                .toEntityList(UserDTO.class);
    }


    public Mono<ResponseEntity<String>> activateUser(Long id, String token) {
        return webClient
                .put()
                .uri(id + "/activate")
                .headers(headers -> headers.set(HttpHeaders.AUTHORIZATION, token))  // Передаем токен в заголовке
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,  // Если ошибка, генерируем исключение
                        response -> {
                            System.out.println("Ошибка при запросе к UserService, статус: " + response.statusCode());
                            return Mono.error(new RuntimeException("Не удалось получить данные пользователя"));
                        }
                )
                .toEntity(String.class)  // Преобразуем ответ в объект UserDTO
                .doOnError(e -> System.err.println("Ошибка при запросе к UserService: " + e.getMessage()))  // Логируем ошибку
                .onErrorResume(e -> Mono.empty());  // Возвращаем пустой Mono в случае ошибки
    }


    public Mono<ResponseEntity<String>> deactivateUser(Long id, String token) {
        return webClient
                .put()
                .uri(id + "/deactivate")
                .headers(headers -> headers.set(HttpHeaders.AUTHORIZATION, token))  // Передаем токен в заголовке
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,  // Если ошибка, генерируем исключение
                        response -> {
                            System.out.println("Ошибка при запросе к UserService, статус: " + response.statusCode());
                            return Mono.error(new RuntimeException("Не удалось получить данные пользователя"));
                        }
                )
                .toEntity(String.class)
                .doOnError(e -> System.err.println("Ошибка при запросе к UserService: " + e.getMessage()))  // Логируем ошибку
                .onErrorResume(e -> Mono.empty());  // Возвращаем пустой Mono в случае ошибки
    }

    public Mono<ResponseEntity<String>> changePassword(Long id, ChangePasswordRequest changePasswordRequest, String token) {
        return webClient.put()
                .uri(uriBuilder -> uriBuilder.path("/{id}/change-password")
                        .build(id))
                //.header(HttpHeaders.AUTHORIZATION, token)
                .headers(headers -> headers.set(HttpHeaders.AUTHORIZATION, token))  // Передаем токен
                .bodyValue(changePasswordRequest)
                .retrieve()
                .toEntity(String.class);
    }
}