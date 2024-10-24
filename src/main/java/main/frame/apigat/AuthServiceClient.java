package main.frame.apigat;

//import main.frame.shared.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

//@Service
//public class AuthServiceClient {
//
//    private final WebClient webClient;
//
//    public AuthServiceClient(@LoadBalanced WebClient.Builder webClientBuilder) {
//        this.webClient = webClientBuilder.baseUrl("http://auth-service").build();  // Устанавливаем базовый URL для AuthService
//    }
//
//    // Получение данных пользователя
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
//}