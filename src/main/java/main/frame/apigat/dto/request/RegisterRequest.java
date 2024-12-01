package main.frame.apigat.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

//import javax.validation.constraints.Email;
//import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class RegisterRequest {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String password;

    private String username;
    private String phoneNumber;
    private LocalDateTime dateOfCreated;
}