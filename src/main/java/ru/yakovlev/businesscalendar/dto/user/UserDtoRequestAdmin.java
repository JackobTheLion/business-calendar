package ru.yakovlev.businesscalendar.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.yakovlev.businesscalendar.validation.ValidationGroups.Create;

import java.util.List;

@Getter
@Setter
@Schema(description = "Create and update user request by admin")
public class UserDtoRequestAdmin extends UserDtoRequestUser {

    @Schema(description = "'ROLE_ADMIN', 'ROLE_USER'")
    @NotNull(groups = Create.class)
    private List<String> userRole;

    private Boolean enabled;

    public UserDtoRequestAdmin(@Email(groups = Create.class) @NotBlank String email,
                               @NotEmpty(groups = Create.class) String userName,
                               @NotEmpty(groups = Create.class) String firstName,
                               @NotEmpty(groups = Create.class) String lastName,
                               @NotEmpty(groups = Create.class) String password,
                               List<String> userRole,
                               Boolean enabled) {
        super(email, userName, firstName, lastName, password);
        this.userRole = userRole;
        this.enabled = enabled;
    }
}
