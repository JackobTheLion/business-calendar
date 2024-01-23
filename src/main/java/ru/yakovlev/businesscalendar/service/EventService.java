package ru.yakovlev.businesscalendar.service;

import ru.yakovlev.businesscalendar.dto.event.EventDtoFullResponse;
import ru.yakovlev.businesscalendar.dto.event.EventDtoRequest;
import ru.yakovlev.businesscalendar.dto.event.EventDtoShortResponse;
import ru.yakovlev.businesscalendar.dto.event.EventDtoUpdateRequest;
import ru.yakovlev.businesscalendar.dto.user.MonthsWorkingResult;

import java.security.Principal;
import java.util.List;

/**
 * Service responsible for working with events.
 */

public interface EventService {
    /**
     * Adding new event by user.
     *
     * @param eventDtoRequest
     * @param principal
     * @return
     */
    EventDtoFullResponse addEvent(EventDtoRequest eventDtoRequest, Principal principal);

    /**
     * Updating event.
     *
     * @param eventDtoRequest
     * @param principal
     * @param eventId
     * @return
     */
    EventDtoFullResponse updateEventByUser(EventDtoUpdateRequest eventDtoRequest, Principal principal, Long eventId);

    /**
     * Updating event by admin
     *
     * @param eventDtoRequest
     * @param eventId
     * @return
     */
    EventDtoFullResponse updateEventByAdmin(EventDtoUpdateRequest eventDtoRequest, Long eventId);

    /**
     * Find user events with pagination by user ID
     *
     * @param userId
     * @param from
     * @param size
     * @return
     */
    List<EventDtoShortResponse> findUserEvents(Long userId, int from, int size);

    /**
     * Find event by id to see detailed information.
     *
     * @param eventId
     * @return
     */
    EventDtoFullResponse findEventById(Long eventId);

    /**
     * Provides working stats by month.
     *
     * @param userId
     * @param year
     * @param month
     * @return
     */
    MonthsWorkingResult getMonthsWorkingResult(Long userId, Integer year, Integer month);
}
