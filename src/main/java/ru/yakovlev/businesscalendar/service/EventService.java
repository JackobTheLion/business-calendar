package ru.yakovlev.businesscalendar.service;

import ru.yakovlev.businesscalendar.dto.event.EventDtoFullResponse;
import ru.yakovlev.businesscalendar.dto.event.EventDtoRequest;
import ru.yakovlev.businesscalendar.dto.event.EventDtoShortResponse;
import ru.yakovlev.businesscalendar.dto.user.MonthsWorkingResult;

import java.security.Principal;
import java.util.List;

public interface EventService {
    EventDtoFullResponse addEvent(EventDtoRequest eventDtoRequest, Principal principal);

    EventDtoFullResponse updateEventByUser(EventDtoRequest eventDtoRequest, Principal principal, Long eventId);

    EventDtoFullResponse updateEventByAdmin(EventDtoRequest eventDtoRequest, Long eventId);

    List<EventDtoShortResponse> findUserEvents(Long userId, int from, int size);

    EventDtoFullResponse findEventById(Long eventId);

    MonthsWorkingResult getMonthsWorkingResult(Long userId, Integer year, Integer month);
}
