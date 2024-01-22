package ru.yakovlev.businesscalendar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yakovlev.businesscalendar.dto.user.UserDtoFullResponse;
import ru.yakovlev.businesscalendar.dto.user.UserDtoRequestUser;
import ru.yakovlev.businesscalendar.service.UserService;
import ru.yakovlev.businesscalendar.validation.ValidationGroups;

import java.security.Principal;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
@Slf4j
@Tag(name = "Private user", description = "Private endpoints for users")
@Validated
public class UserPrivateController {

    private final UserService userService;

    @Operation(summary = "User updating information.",
            description = "User update personal information. Only user's own information can be updated.")
    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public UserDtoFullResponse updateUser(@RequestBody @Validated(ValidationGroups.Update.class) UserDtoRequestUser userDtoRequestUser,
                                          Principal principal) {
        return userService.updateUser(userDtoRequestUser, principal);
    }
}
