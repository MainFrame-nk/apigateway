package main.frame.apigat.controller;

import lombok.AllArgsConstructor;
import main.frame.apigat.client.AuthServiceClient;
import main.frame.apigat.client.UserServiceClient;
import main.frame.apigat.dto.request.RegisterRequest;
import main.frame.shared.dto.RoleDTO;
import main.frame.shared.dto.UserDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserServiceClient userServiceClient;

    // Получение данных текущего пользователя
    @GetMapping("/user")
    public Mono<ResponseEntity<UserDTO>> getUserDetails(ServerWebExchange exchange) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        return userServiceClient.getUserDetails(token)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new UserDTO()))  // Если пользователь не найден, возвращаем пустой UserDTO
                .onErrorResume(e -> {
                    System.err.println("Ошибка: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UserDTO())); // Обрабатываем ошибку и возвращаем пустой UserDTO
                });
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<String>> deleteUser(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        return userServiceClient.deleteUser(id, token)
                .then(Mono.just(ResponseEntity.ok("Пользователь успешно удален")))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Ошибка удаления пользователя: " + e.getMessage())));
    }

    @GetMapping("/")
    public Mono<ResponseEntity<List<UserDTO>>> getAllUsers(ServerWebExchange exchange) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        return userServiceClient.getAllUsers(token)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList()))  // Если нет пользователей, возвращаем пустой список
                .onErrorResume(e -> {
                    System.err.println("Ошибка: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList()));  // Обрабатываем ошибку и возвращаем пустой список
                });
    }

    // Получение пользователя по ID
    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserDTO>> getUserById(@PathVariable Long id, ServerWebExchange exchange) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        // Включаем асинхронность при вызове метода
        return userServiceClient.getUserById(id, token)
                .map(responseEntity -> {
                    // Здесь мы извлекаем UserDTO из ResponseEntity
                    UserDTO user = responseEntity.getBody();
                    if (user == null) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new UserDTO()); // Если не найден, возвращаем пустой объект UserDTO
                    }
                    return ResponseEntity.ok(user); // Возвращаем пользователя
                })
                .onErrorResume(e -> {
                    System.err.println("Ошибка: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UserDTO())); // Обрабатываем ошибку и возвращаем пустой UserDTO
                });
    }

//    @PutMapping("/{id}")
//    public Mono<ResponseEntity<UserDTO>> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO, @RequestHeader("Authorization") String token) {
//        return userServiceClient.updateUser(id, userDTO, token);
//    }


    // Обновление данных пользователя
    @PutMapping("/{id}")
    public Mono<ResponseEntity<UserDTO>> updateUser(
            @PathVariable Long id,
            @RequestBody UserDTO userDTO,
            @RequestHeader("Authorization") String token) {

        return userServiceClient.updateUser(id, userDTO, token)
                .flatMap(responseEntity -> {
                    if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                        return Mono.just(ResponseEntity.ok(responseEntity.getBody()));
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
                });
//                .onErrorResume(e -> {
//                    System.err.println("Ошибка обновления пользователя: " + e.getMessage());
//                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
//                });
    }
//        return userServiceClient.updateUser(id, userDTO, token)
//                .flatMap(responseEntity -> {
//                    if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
//                        return Mono.just(ResponseEntity.ok(responseEntity.getBody()));
//                    }
//                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
//                })
//                .onErrorResume(e -> {
//                    System.err.println("Ошибка обновления пользователя: " + e.getMessage());
//                    //log.error("Ошибка обновления пользователя: {}", e.getMessage());
//                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
//                });
//    }

    @PutMapping("/{id}/roles")
    public Mono<ResponseEntity<UserDTO>> updateUserRoles(
            @PathVariable Long id,
            @RequestBody List<String> roles,
            @RequestHeader("Authorization") String token) {

        return userServiceClient.updateUserRoles(id, roles, token)
                .flatMap(responseEntity -> {
                    if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                        return Mono.just(ResponseEntity.ok(responseEntity.getBody()));
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
//                })
//                .onErrorResume(e -> {
//                    System.err.println("Ошибка обновления ролей пользователя: " + e.getMessage());
//                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
               // Нужно обязательно сделать обработку ошибок, хоть и работает
                });
    }
}