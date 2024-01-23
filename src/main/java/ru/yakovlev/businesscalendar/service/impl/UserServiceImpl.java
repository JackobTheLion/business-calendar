package ru.yakovlev.businesscalendar.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yakovlev.businesscalendar.dto.user.UserDtoFullResponse;
import ru.yakovlev.businesscalendar.dto.user.UserDtoRequestAdmin;
import ru.yakovlev.businesscalendar.dto.user.UserDtoRequestUser;
import ru.yakovlev.businesscalendar.exception.exceptions.EmailOrNameRegisteredException;
import ru.yakovlev.businesscalendar.exception.exceptions.NotFoundException;
import ru.yakovlev.businesscalendar.mapper.UserMapper;
import ru.yakovlev.businesscalendar.model.user.User;
import ru.yakovlev.businesscalendar.model.user.UserRole;
import ru.yakovlev.businesscalendar.repository.UserRepository;
import ru.yakovlev.businesscalendar.repository.UserRoleRepository;
import ru.yakovlev.businesscalendar.service.UserService;

import java.security.Principal;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserRoleRepository userRoleRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetailsService userDetailsService() {
        return this::findUserByName;
    }

    /**
     * Adding new user.
     *
     * @param userDtoRequestUser new user to add
     * @return {@link UserDtoFullResponse} saved user
     */
    @Override
    @Transactional
    public UserDtoFullResponse addUser(UserDtoRequestUser userDtoRequestUser) {
        log.trace("Saving user: {}", userDtoRequestUser);
        User userToSave = UserMapper.mapFromDto(userDtoRequestUser, passwordEncoder.encode(userDtoRequestUser.getPassword()));
        UserRole userRole = userRoleRepository.findUserRoleByName("ROLE_USER");
        userToSave.setUserRole(List.of(userRole));
        userToSave.setEnabled(false);

        User savedUser = saveUserToDb(userToSave);

        log.trace("User saved: {}", savedUser);
        return UserMapper.mapToFullDto(savedUser);
    }

    /**
     * Updating existing user. User cannot update role or enable account.
     *
     * @param userDtoRequestUser user information to update
     * @param principal          current user
     * @return {@link UserDtoFullResponse} saved user
     */
    @Override
    @Transactional
    public UserDtoFullResponse updateUser(UserDtoRequestUser userDtoRequestUser, Principal principal) {
        log.trace("Updating user: {}.", userDtoRequestUser);
        User userToUpdate = findUserByName(principal.getName());
        updateUserFields(userToUpdate, userDtoRequestUser);
        User updateduser = saveUserToDb(userToUpdate);
        log.trace("User updated: {}.", updateduser);
        return UserMapper.mapToFullDto(updateduser);
    }

    /**
     * User update by admin.
     *
     * @param userDtoRequestUser user information to update
     * @return {@link UserDtoFullResponse} saved user
     */
    @Override
    @Transactional
    public UserDtoFullResponse updateUserByAdmin(UserDtoRequestAdmin userDtoRequestUser, Long userId) {
        log.trace("Updating user: {}.", userDtoRequestUser);
        User userToUpdate = findUserById(userId);
        updateUserFields(userToUpdate, userDtoRequestUser);

        if (userDtoRequestUser.getUserRole() != null && !userDtoRequestUser.getUserRole().isEmpty()) {
            List<UserRole> userRoles = userRoleRepository.findUserRoleByNameIn(userDtoRequestUser.getUserRole());
            userToUpdate.setUserRole(userRoles);
        }

        if (userDtoRequestUser.getEnabled() != null) {
            userToUpdate.setEnabled(userDtoRequestUser.getEnabled());
        }

        User updateduser = userRepository.save(userToUpdate);
        log.trace("User updated: {}.", updateduser);
        return UserMapper.mapToFullDto(updateduser);
    }

    /**
     * Finding user by id
     *
     * @param id user id
     * @return {@link User}
     */
    @Override
    @Transactional(readOnly = true)
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User id %s not found", id)));
    }

    /**
     * Finding user by name
     *
     * @param userName username
     * @return {@link User}
     */
    @Override
    @Transactional(readOnly = true)
    public User findUserByName(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new NotFoundException(String.format("User name %s not found", userName)));
    }

    private void updateUserFields(User userToUpdate, UserDtoRequestUser userDtoRequestUser) {
        if (userDtoRequestUser.getEmail() != null) {
            userToUpdate.setEmail(userDtoRequestUser.getEmail());
        }

        if (userDtoRequestUser.getUserName() != null) {
            userToUpdate.setUserName(userDtoRequestUser.getUserName());
        }

        if (userDtoRequestUser.getFirstName() != null) {
            userToUpdate.setFirstName(userDtoRequestUser.getFirstName());
        }

        if (userDtoRequestUser.getLastName() != null) {
            userToUpdate.setLastName(userDtoRequestUser.getLastName());
        }

        if (userDtoRequestUser.getPassword() != null) {
            userToUpdate.setPassword(passwordEncoder.encode(userDtoRequestUser.getPassword()));
        }
    }

    private User saveUserToDb(User userToSave) {
        try {
            return userRepository.save(userToSave);
        } catch (DataIntegrityViolationException e) {
            log.error(e.getMessage());
            throw new EmailOrNameRegisteredException("User with such email or username already exists.");
        }
    }
}
