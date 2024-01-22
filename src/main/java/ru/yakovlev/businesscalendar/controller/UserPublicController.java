package ru.yakovlev.businesscalendar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yakovlev.businesscalendar.dto.jwt.JwtRequest;
import ru.yakovlev.businesscalendar.dto.jwt.JwtResponse;
import ru.yakovlev.businesscalendar.dto.user.UserDtoFullResponse;
import ru.yakovlev.businesscalendar.dto.user.UserDtoRequestUser;
import ru.yakovlev.businesscalendar.service.AuthenticationService;
import ru.yakovlev.businesscalendar.service.UserService;
import ru.yakovlev.businesscalendar.validation.ValidationGroups;

@RestController
@RequestMapping("/userPub")
@AllArgsConstructor
@Slf4j
@Validated
@Tag(name = "Public user", description = "Private endpoints for users")
public class UserPublicController {
    private final UserService userService;

    private final AuthenticationService authenticationService;

    @Operation(summary = "User registration.",
            description = "Created user will have enabled status 'false' and have to be approved by administrator.")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDtoFullResponse addUser(@RequestBody
                                       @Validated(ValidationGroups.Create.class)
                                       UserDtoRequestUser userDtoRequestUser) {
        return userService.addUser(userDtoRequestUser);
    }

    @Operation(summary = "User receiving JWT token")
    @PostMapping("/auth")
    @ResponseStatus(HttpStatus.OK)
    public JwtResponse getToken(@RequestBody @Validated JwtRequest jwtRequest) {
        return authenticationService.getToken(jwtRequest);
    }
}
