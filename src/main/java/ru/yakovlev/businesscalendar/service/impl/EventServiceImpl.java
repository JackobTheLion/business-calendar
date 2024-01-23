package ru.yakovlev.businesscalendar.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yakovlev.businesscalendar.dto.event.EventDtoFullResponse;
import ru.yakovlev.businesscalendar.dto.event.EventDtoRequest;
import ru.yakovlev.businesscalendar.dto.event.EventDtoShortResponse;
import ru.yakovlev.businesscalendar.dto.user.MonthsWorkingResult;
import ru.yakovlev.businesscalendar.exception.exceptions.AccessDeniedException;
import ru.yakovlev.businesscalendar.exception.exceptions.NotFoundException;
import ru.yakovlev.businesscalendar.mapper.EventMapper;
import ru.yakovlev.businesscalendar.mapper.UserMapper;
import ru.yakovlev.businesscalendar.model.event.Event;
import ru.yakovlev.businesscalendar.model.user.User;
import ru.yakovlev.businesscalendar.repository.EventRepository;
import ru.yakovlev.businesscalendar.service.DaysOffService;
import ru.yakovlev.businesscalendar.service.EventService;
import ru.yakovlev.businesscalendar.service.UserService;

import java.security.Principal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

import static ru.yakovlev.businesscalendar.mapper.EventMapper.mapFromDto;
import static ru.yakovlev.businesscalendar.mapper.EventMapper.mapToFullDto;
import static ru.yakovlev.businesscalendar.model.event.EventType.SICK_LEAVE;
import static ru.yakovlev.businesscalendar.model.event.EventType.VACATION;

/**
 * Service responsible for working with events.
 */

@Service
@Slf4j
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserService userService;
    private final DaysOffService daysOffService;

    /**
     * Adding new event by user. Current user is owner of the event.
     *
     * @param eventDtoRequest
     * @param principal
     * @return
     */
    @Override
    @Transactional
    public EventDtoFullResponse addEvent(EventDtoRequest eventDtoRequest, Principal principal) {
        log.trace("Adding new event: {} by user {}", eventRepository, principal.getName());

        Event eventToSave = mapFromDto(eventDtoRequest);
        eventToSave.setOwner(userService.findUserByName(principal.getName()));

        Event savedEvent = eventRepository.save(eventToSave);
        log.trace("Event saved: {}", savedEvent);
        return mapToFullDto(savedEvent);
    }


    /**
     * Updating event. Only owner can update event.
     *
     * @param eventDtoRequest
     * @param principal
     * @param eventId
     * @return
     */
    @Override
    @Transactional
    public EventDtoFullResponse updateEventByUser(EventDtoRequest eventDtoRequest, Principal principal, Long eventId) {
        Event eventToUpdate = findEvent(eventId);
        if (eventToUpdate.getOwner().getUsername().equals(principal.getName())) {
            updateEventFields(eventDtoRequest, eventToUpdate);
        } else {
            log.error("User {} cannot update event {}.", principal.getName(), eventToUpdate);
            throw new AccessDeniedException("Only owner can update event");
        }

        Event updatedEvent = eventRepository.save(eventToUpdate);
        log.trace("Event updated: {}", updatedEvent);
        return mapToFullDto(updatedEvent);
    }

    /**
     * Updating event by admin
     *
     * @param eventDtoRequest
     * @param eventId
     * @return
     */
    @Override
    @Transactional
    public EventDtoFullResponse updateEventByAdmin(EventDtoRequest eventDtoRequest, Long eventId) {
        Event eventToUpdate = findEvent(eventId);
        updateEventFields(eventDtoRequest, eventToUpdate);
        Event updatedEvent = eventRepository.save(eventToUpdate);
        log.trace("Event updated: {}", updatedEvent);
        return mapToFullDto(updatedEvent);
    }

    /**
     * Find user events with pagination by user ID
     *
     * @param userId
     * @param from start position of pagination
     * @param size size of page
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<EventDtoShortResponse> findUserEvents(Long userId, int from, int size) {
        log.trace("Looking for events of user {}.", userId);
        User owner = userService.findUserById(userId);
        final PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Event> events = eventRepository.findByOwnerOrderByStartDate(owner, page);
        return events.stream().map(EventMapper::mapToShortDto).collect(Collectors.toList());
    }

    /**
     * Find event by id to see detailed information.
     *
     * @param eventId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public EventDtoFullResponse findEventById(Long eventId) {
        return mapToFullDto(findEvent(eventId));
    }

    /**
     * Provides working stats by month.
     * Statistics is calculated based on following: <p>
     *  - if full working day: total days +1, total working hours +8 <p>
     *  - if short working day: total days +1, total working hours +7 <p>
     *  - if day off - days off +1 <p>
     *
     * @param userId user whose stats is needed
     * @param year year of stats
     * @param month month of stats
     */
    @Override
    @Transactional(readOnly = true)
    public MonthsWorkingResult getMonthsWorkingResult(Long userId, Integer year, Integer month) {
        log.trace("getting working result for user {} for {} {}", userId, month, year);
        User employee = userService.findUserById(userId);
        LocalDate firstDayOfCheckMonth = LocalDate.of(year, month, 1);
        List<Event> eventsOff = eventRepository
                .findByOwnerAndEventTypeIn(employee, List.of(VACATION, SICK_LEAVE));
        Map<LocalDate, Integer> publicHolidaysAndExtraWorkingDays = daysOffService.getDaysOff(year);

        Map<LocalDate, String> calendar = getCalendar(firstDayOfCheckMonth, publicHolidaysAndExtraWorkingDays);
        Integer totalBusinessDays = countTotalBusinessDays(calendar);
        excludeDaysOff(eventsOff, calendar);

        int actualBusinessDays = 0;
        int actualBusinessHours = 0;
        int daysOff = 0;
        for (Map.Entry<LocalDate, String> day : calendar.entrySet()) {
            if (day.getValue().equals("business day")) {
                actualBusinessDays++;
                actualBusinessHours += 8;
                continue;
            }
            if (day.getValue().equals("short day")) {
                actualBusinessDays++;
                actualBusinessHours += 7;
                continue;
            }
            if (day.getValue().equals("day off")) {
                daysOff++;
            }
        }

        return MonthsWorkingResult.builder()
                .employee(UserMapper.mapToShortDto(employee))
                .year(Year.of(year))
                .month(Month.of(month))
                .totalBusinessDaysNumber(totalBusinessDays)
                .actualBusinessDaysNumber(actualBusinessDays)
                .workingTime(actualBusinessHours)
                .daysOff(daysOff)
                .build();
    }

    private Map<LocalDate, String> getCalendar(LocalDate startDate, Map<LocalDate, Integer> publicHolidaysAndExtraWorkingDays) {
        Map<LocalDate, String> calendar = new HashMap<>();
        Month checkMonth = startDate.getMonth();
        LocalDate checkDate = startDate;
        while (checkDate.getMonth().equals(checkMonth)) {
            Integer type = publicHolidaysAndExtraWorkingDays.get(checkDate);
            if (type != null) {
                if (type == 1) {
                    calendar.put(checkDate, "non business day");
                } else if (type == 2) {
                    calendar.put(checkDate, "short day");
                }
            } else if (isBusinessDay(checkDate)) {
                calendar.put(checkDate, "business day");
            } else {
                calendar.put(checkDate, "non business day");
            }
            checkDate = checkDate.plusDays(1);
        }
        return calendar;
    }

    private Integer countTotalBusinessDays(Map<LocalDate, String> calendar) {
        int businessDaysCount = 0;
        for (Map.Entry<LocalDate, String> entry : calendar.entrySet()) {
            if (entry.getValue().equals("business day") || entry.getValue().equals("short day")) {
                businessDaysCount++;
            }
        }
        return businessDaysCount;
    }

    private void excludeDaysOff(List<Event> eventsOff, Map<LocalDate, String> calendar) {
        Set<LocalDate> daysOff = new HashSet<>();

        for (Event event : eventsOff) {
            LocalDate checkDate = event.getStartDate().toLocalDate();

            while (isDayBelongsToEvent(event, checkDate)) {
                daysOff.add(checkDate);
                checkDate = checkDate.plusDays(1);
            }
        }

        for (Map.Entry<LocalDate, String> day : calendar.entrySet()) {
            if (daysOff.contains(day.getKey())) {
                calendar.put(day.getKey(), "day off");
            }
        }
    }

    private boolean isDayBelongsToEvent(Event event, LocalDate date) {
        return date.isAfter(event.getStartDate().toLocalDate().minusDays(1))
                && date.isBefore(event.getEndDate().toLocalDate().plusDays(1));
    }

    private boolean isBusinessDay(LocalDate date) {
        return date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY;
    }

    private void updateEventFields(EventDtoRequest eventDtoRequest, Event eventToUpdate) {
        if (eventDtoRequest.getStartDate() != null) {
            eventToUpdate.setStartDate(eventDtoRequest.getStartDate());
        }

        if (eventDtoRequest.getEndDate() != null) {
            eventToUpdate.setEndDate(eventDtoRequest.getEndDate());
        }

        if (eventDtoRequest.getEventType() != null) {
            eventToUpdate.setEventType(eventDtoRequest.getEventType());
        }

        if (eventDtoRequest.getName() != null) {
            eventToUpdate.setName(eventDtoRequest.getName());
        }

        if (eventDtoRequest.getDescription() != null) {
            eventToUpdate.setDescription(eventDtoRequest.getDescription());
        }
    }

    private Event findEvent(Long eventId) {
        log.trace("Searching event id {}.", eventId);
        return eventRepository.findById(eventId).orElseThrow(() -> {
            log.error("Event id {} not found.", eventId);
            return new NotFoundException(String.format("Task id %s not found", eventId));
        });
    }
}
