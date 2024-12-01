package main.frame.apigat.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateLobbyRequest {
    @NotBlank(message = "Имя лобби не может быть пустым.")
    private String name;

    private String password;  // Пароль может быть пустым

    @Min(value = 2, message = "Максимальное количество игроков должно быть хотя бы 2.")
    @Max(value = 6, message = "Максимальное количество игроков должно быть не больше 6.")
    private Integer maxPlayers;

    private Long hostId;  // ID хозяина лобби
}
