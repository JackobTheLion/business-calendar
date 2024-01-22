package ru.yakovlev.businesscalendar.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yakovlev.businesscalendar.validation.ValidationGroups.Create;
import ru.yakovlev.businesscalendar.validation.password.Password;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Create and update user request")
public class UserDtoRequestUser {
    @Email(groups = Create.class)
    @NotBlank
    private String email;

    @NotEmpty(groups = Create.class)
    private String userName;

    private String firstName;

    private String lastName;

    @NotEmpty(groups = Create.class)
    @Password
    private String password;

    @Override
    public String toString() {
        return "UserDtoRequest{" +
                "email='" + email + '\'' +
                ", userName='" + userName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
