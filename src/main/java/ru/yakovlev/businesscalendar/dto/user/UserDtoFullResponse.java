package ru.yakovlev.businesscalendar.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yakovlev.businesscalendar.model.user.UserRole;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDtoFullResponse {

    private Long id;

    private String email;

    private String userName;

    private String firstName;

    private String lastName;

    private List<UserRole> userRole;

    private Boolean enabled;
}
