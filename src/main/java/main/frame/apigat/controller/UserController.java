package main.frame.apigat.controller;

import lombok.AllArgsConstructor;
import main.frame.apigat.client.AuthServiceClient;
import main.frame.apigat.client.UserServiceClient;
import main.frame.apigat.dto.request.ChangePasswordRequest;
import main.frame.apigat.dto.request.RegisterRequest;
import main.frame.shared.dto.RoleDTO;
import main.frame.shared.dto.UserDTO;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserServiceClient userServiceClient;

    // Получение данных текущего пользователя
    @GetMapping("/user")
    public Mono<ResponseEntity<UserDTO>> getUserDetails(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
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
            ServerWebExchange exchange) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        return userServiceClient.updateUser(id, userDTO, token)
                .map(responseEntity -> {
                    UserDTO updatedUser = responseEntity.getBody();
                    if (updatedUser == null) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserDTO()); // Если не удалось обновить, возвращаем пустой объект
                    }
                    return ResponseEntity.ok(updatedUser); // Возвращаем обновленного пользователя
                })
                .onErrorResume(e -> {
                    System.err.println("Ошибка: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UserDTO())); // Возвращаем пустой объект при ошибке
                });
    }
      //      @RequestHeader("Authorization") String token) {

    @PutMapping("/{id}/roles")
    public Mono<ResponseEntity<UserDTO>> updateUserRoles(@PathVariable Long id, @RequestBody List<String> roles, ServerWebExchange exchange) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        return userServiceClient.updateUserRoles(id, roles, token)
                .map(responseEntity -> {
                    UserDTO updatedUser = responseEntity.getBody();
                    if (updatedUser == null) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserDTO()); // Если не удалось обновить, возвращаем пустой объект
                    }
                    return ResponseEntity.ok(updatedUser); // Возвращаем обновленного пользователя
                })
                .onErrorResume(e -> {
                    System.err.println("Ошибка: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UserDTO())); // Возвращаем пустой объект при ошибке
                });
    }


    @GetMapping("/role/{roleName}")
    public Mono<ResponseEntity<List<UserDTO>>> getUsersByRole(@PathVariable String roleName, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return userServiceClient.getUsersByRole(roleName, token)
                .map(responseEntity -> {
                    List<UserDTO> users = responseEntity.getBody();
                    if (users == null || users.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());  // Если пользователей нет, возвращаем пустой список
                    }
                    return ResponseEntity.ok(users); // Если пользователи найдены, возвращаем их
//                })
//                .onErrorResume(e -> {
//                    System.err.println("Ошибка: " + e.getMessage());
//                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList())); // Ошибка сервера
                });
    }

    @GetMapping("/search")
    public Mono<ResponseEntity<List<UserDTO>>> searchUsers(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateOfCreated,
            @RequestParam(required = false) String roleName,
            ServerWebExchange exchange) {

        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        return userServiceClient.searchUsers(email, username, phoneNumber, active, dateOfCreated, roleName, token)
                .map(responseEntity -> {
                    List<UserDTO> users = responseEntity.getBody();
                    if (users == null || users.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());  // Если нет пользователей, возвращаем пустой список
                    }
                    return ResponseEntity.ok(users); // Если пользователи найдены, возвращаем их
//                })
//                .onErrorResume(e -> {
//                    System.err.println("Ошибка: " + e.getMessage());
//                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList())); // Ошибка сервера
                });
    }

    @PutMapping("/{id}/activate")
    public Mono<ResponseEntity<String>> activateUser(@PathVariable Long id, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return userServiceClient.activateUser(id, token)
                .map(responseEntity -> {
                    if (responseEntity.getStatusCode() == HttpStatus.OK) {
                        return ResponseEntity.ok("Пользователь активирован!");
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден!");
                    }
                })
                .onErrorResume(e -> {
                    System.err.println("Ошибка: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при активации пользователя"));
                });
    }

    @PutMapping("/{id}/deactivate")
    public Mono<ResponseEntity<String>> deactivateUser(@PathVariable Long id, ServerWebExchange exchange) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        return userServiceClient.deactivateUser(id, token)
                .map(responseEntity -> {
                    if (responseEntity.getStatusCode() == HttpStatus.OK) {
                        return ResponseEntity.ok("Пользователь деактивирован!");
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден!");
                    }
                })
                .onErrorResume(e -> {
                    System.err.println("Ошибка: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при деактивации пользователя"));
                });
    }

    @PutMapping("/{id}/changePassword")
    public Mono<ResponseEntity<String>> changePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest changePasswordRequest, ServerWebExchange exchange) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        return userServiceClient.changePassword(id, changePasswordRequest, token)
                .map(responseEntity -> {
                    if (responseEntity.getStatusCode() == HttpStatus.OK) {
                        return ResponseEntity.ok("Пароль успешно изменен!");
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка при изменении пароля");
                    }
                })
                .onErrorResume(e -> {
                    System.err.println("Ошибка: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка сервера"));
                });
    }


}