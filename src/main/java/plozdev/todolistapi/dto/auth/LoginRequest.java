package plozdev.todolistapi.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @Email
    @NotBlank(message = "Email must not be empty.")
    private String email;

    @Size(min = 8, max = 30, message = "Password must be 8-30 characters long.")
    @NotBlank(message = "Password must not be empty.")
    private String password;

}
