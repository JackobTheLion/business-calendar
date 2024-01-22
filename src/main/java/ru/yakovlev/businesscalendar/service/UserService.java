package ru.yakovlev.businesscalendar.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.yakovlev.businesscalendar.dto.user.UserDtoFullResponse;
import ru.yakovlev.businesscalendar.dto.user.UserDtoRequestAdmin;
import ru.yakovlev.businesscalendar.dto.user.UserDtoRequestUser;
import ru.yakovlev.businesscalendar.model.user.User;

import java.security.Principal;

public interface UserService {
    UserDetailsService userDetailsService();

    /**
     * Adding new user.
     *
     * @param userDtoRequestUser new user to add
     * @return {@link UserDtoFullResponse} saved user
     */
    UserDtoFullResponse addUser(UserDtoRequestUser userDtoRequestUser);

    /**
     * Updating existing user
     *
     * @param userDtoRequestUser user information to update
     * @param principal          current user
     * @return {@link UserDtoFullResponse} saved user
     */
    UserDtoFullResponse updateUser(UserDtoRequestUser userDtoRequestUser, Principal principal);

    /**
     * User update by admin.
     *
     * @param userDtoRequestUser user information to update
     * @return {@link UserDtoFullResponse} saved user
     */
    UserDtoFullResponse updateUserByAdmin(UserDtoRequestAdmin userDtoRequestUser, Long userId);

    /**
     * Finding user by id
     *
     * @param id user id
     * @return {@link User}
     */
    User findUserById(Long id);

    /**
     * Finding user by name
     *
     * @param userName username
     * @return {@link User}
     */
    User findUserByName(String userName);
}
