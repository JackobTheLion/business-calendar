package ru.yakovlev.businesscalendar.mapper;

import ru.yakovlev.businesscalendar.dto.user.UserDtoFullResponse;
import ru.yakovlev.businesscalendar.dto.user.UserDtoRequestUser;
import ru.yakovlev.businesscalendar.dto.user.UserDtoShortResponse;
import ru.yakovlev.businesscalendar.model.user.User;

public class UserMapper {
    public static User mapFromDto(UserDtoRequestUser userDtoRequestUser, String password) {
        return User.builder()
                .userName(userDtoRequestUser.getUserName())
                .firstName(userDtoRequestUser.getFirstName())
                .lastName(userDtoRequestUser.getLastName())
                .email(userDtoRequestUser.getEmail())
                .password(password)
                .build();
    }

    public static UserDtoFullResponse mapToFullDto(User user) {
        return UserDtoFullResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .userName(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userRole(user.getUserRole())
                .enabled(user.getEnabled())
                .build();
    }

    public static UserDtoShortResponse mapToShortDto(User user) {
        return UserDtoShortResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }
}
