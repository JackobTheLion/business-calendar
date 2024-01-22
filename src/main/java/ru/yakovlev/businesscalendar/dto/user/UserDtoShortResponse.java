package ru.yakovlev.businesscalendar.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDtoShortResponse {
    private Long id;

    private String firstName;

    private String lastName;
}
