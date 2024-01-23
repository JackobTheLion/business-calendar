package ru.yakovlev.businesscalendar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yakovlev.businesscalendar.dto.event.EventDtoFullResponse;
import ru.yakovlev.businesscalendar.dto.event.EventDtoRequest;
import ru.yakovlev.businesscalendar.dto.event.EventDtoShortResponse;
import ru.yakovlev.businesscalendar.dto.user.MonthsWorkingResult;
import ru.yakovlev.businesscalendar.service.EventService;
import ru.yakovlev.businesscalendar.validation.ValidationGroups;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/event")
@AllArgsConstructor
@Slf4j
@Tag(name = "Private event", description = "Private endpoints for events")
@Validated
public class EventController {

    private final EventService eventService;

    @Operation(summary = "Adding new event",
            description = "Adding new event.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDtoFullResponse addEvent(@RequestBody @Validated(ValidationGroups.Create.class) EventDtoRequest eventDtoRequest,
                                         Principal principal) {
        return eventService.addEvent(eventDtoRequest, principal);
    }

    @Operation(summary = "Updating event",
            description = "Updating event. Only owner can update")
    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDtoFullResponse updateEvent(@RequestBody @Validated(ValidationGroups.Update.class) EventDtoRequest eventDtoRequest,
                                            Principal principal, @PathVariable Long eventId) {
        return eventService.updateEventByUser(eventDtoRequest, principal, eventId);
    }

    @Operation(summary = "Finding event",
            description = "Finding event by ID.")
    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDtoFullResponse findEvent(@PathVariable Long eventId) {
        return eventService.findEventById(eventId);
    }

    @Operation(summary = "Finding user events with pagination",
            description = "from - start point of pagination, size - size of single page")
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<EventDtoShortResponse> findUserEvents(@RequestParam Long userId,
                                                      @RequestParam(defaultValue = "0") Integer from,
                                                      @RequestParam(defaultValue = "10") Integer size) {
        return eventService.findUserEvents(userId, from, size);
    }

    @Operation(summary = "Getting month result",
            description = "Getting month result")
    @GetMapping("/monthResult")
    @ResponseStatus(HttpStatus.OK)
    public MonthsWorkingResult getMonthResult(@RequestParam Long userId,
                                              @RequestParam @Min(2013) Integer year,
                                              @RequestParam @Min(1) @Max(12) Integer month) {
        return eventService.getMonthsWorkingResult(userId, year, month);
    }
}
