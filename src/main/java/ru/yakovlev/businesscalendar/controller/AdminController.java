package ru.yakovlev.businesscalendar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yakovlev.businesscalendar.dto.event.EventDtoFullResponse;
import ru.yakovlev.businesscalendar.dto.event.EventDtoUpdateRequest;
import ru.yakovlev.businesscalendar.dto.user.UserDtoFullResponse;
import ru.yakovlev.businesscalendar.dto.user.UserDtoRequestAdmin;
import ru.yakovlev.businesscalendar.service.EventService;
import ru.yakovlev.businesscalendar.service.UserService;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
@Slf4j
@Validated
@Tag(name = "Admin", description = "Administrator endpoints")
public class AdminController {

    private final UserService userService;

    private final EventService eventService;

    @PatchMapping("/user/{userId}/update")
    @Operation(summary = "Updating user information",
            description = "Updating user information by administrator")
    @ResponseStatus(HttpStatus.OK)
    public UserDtoFullResponse adminUpdateUser(@RequestBody UserDtoRequestAdmin userDtoRequestAdmin,
                                               @PathVariable @Min(1) Long userId) {
        return userService.updateUserByAdmin(userDtoRequestAdmin, userId);
    }

    @PatchMapping("/event/{eventId}/update")
    @Operation(summary = "Updating event information",
            description = "Updating event information by administrator")
    @ResponseStatus(HttpStatus.OK)
    public EventDtoFullResponse adminUpdateEvent(@RequestBody EventDtoUpdateRequest eventDtoRequest,
                                                 @PathVariable @Min(1) Long eventId) {
        return eventService.updateEventByAdmin(eventDtoRequest, eventId);
    }
}
