package ru.yakovlev.businesscalendar.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yakovlev.businesscalendar.dto.event.EventDtoFullResponse;
import ru.yakovlev.businesscalendar.dto.event.EventDtoRequest;
import ru.yakovlev.businesscalendar.dto.event.EventDtoUpdateRequest;
import ru.yakovlev.businesscalendar.dto.user.MonthsWorkingResult;
import ru.yakovlev.businesscalendar.exception.exceptions.AccessDeniedException;
import ru.yakovlev.businesscalendar.model.event.EventType;
import ru.yakovlev.businesscalendar.model.user.User;
import ru.yakovlev.businesscalendar.repository.EventRepository;
import ru.yakovlev.businesscalendar.repository.UserRepository;
import ru.yakovlev.businesscalendar.service.EventService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@Testcontainers
class EventServiceTest {
    private final EventService eventService;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Container
    private static final PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres:14.3-alpine")
            .withDatabaseName("dbname")
            .withUsername("sa")
            .withPassword("sa");

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", database::getJdbcUrl);
        registry.add("spring.datasource.username", database::getUsername);
        registry.add("spring.datasource.password", database::getPassword);
    }

    User owner;
    EventDtoRequest eventDtoRequest;
    Principal principal;

    @BeforeEach
    public void beforeEach() {
        owner = User.builder()
                .email("email@ya.ru")
                .userName("userName")
                .password("123")
                .build();

        eventDtoRequest = EventDtoRequest.builder()
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .eventType(EventType.MEETING)
                .name("name")
                .description("description")
                .build();

        principal = () -> owner.getUsername();
    }

    @AfterEach
    public void afterEach() {
        eventRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    public void addEvent_Normal() {
        userRepository.save(owner);

        EventDtoFullResponse savedEvent = eventService.addEvent(eventDtoRequest, principal);

        assertEquals(eventDtoRequest.getName(), savedEvent.getName());
    }

    @Test
    public void updateEvent_Normal() {
        userRepository.save(owner);
        Long id = eventService.addEvent(eventDtoRequest, principal).getId();
        EventDtoUpdateRequest eventDtoUpdateRequest = getEventUpdateRequest();
        eventDtoUpdateRequest.setName("New name");

        EventDtoFullResponse updatedEvent = eventService.updateEventByUser(eventDtoUpdateRequest, principal, id);

        assertEquals("New name", updatedEvent.getName());
    }

    @Test
    public void updateEvent_NoRights() {
        userRepository.save(owner);
        Long id = eventService.addEvent(eventDtoRequest, principal).getId();
        EventDtoUpdateRequest eventDtoUpdateRequest = getEventUpdateRequest();
        principal = () -> "RANDOM NAME";

        assertThrows(AccessDeniedException.class, () ->
                eventService.updateEventByUser(eventDtoUpdateRequest, principal, id));
    }

    @Test
    public void findEvent_Normal() {
        userRepository.save(owner);
        Long id = eventService.addEvent(eventDtoRequest, principal).getId();

        EventDtoFullResponse eventById = eventService.findEventById(id);
        assertEquals(eventDtoRequest.getName(), eventById.getName());
        assertEquals(id, eventById.getId());
    }

    @Test
    public void getMonthWorkingResult_Normal() {
        userRepository.save(owner);
        eventDtoRequest.setEventType(EventType.VACATION);
        eventDtoRequest.setStartDate(LocalDateTime.of(2024, 1, 23, 0, 0));
        eventDtoRequest.setStartDate(LocalDateTime.of(2024, 1, 24, 0, 0));
        eventService.addEvent(eventDtoRequest, principal);

        MonthsWorkingResult monthsWorkingResult = eventService.getMonthsWorkingResult(owner.getId(), 2024, 1);
        assertEquals(Month.JANUARY, monthsWorkingResult.getMonth());
        assertEquals(17, monthsWorkingResult.getTotalBusinessDaysNumber());
        assertEquals(16, monthsWorkingResult.getActualBusinessDaysNumber());
        assertEquals(128, monthsWorkingResult.getWorkingTime());
    }

    @Test
    public void getMonthWorkingResult_Normal2() {
        userRepository.save(owner);
        eventDtoRequest.setEventType(EventType.MEETING);
        eventDtoRequest.setStartDate(LocalDateTime.of(2024, 1, 23, 0, 0));
        eventDtoRequest.setStartDate(LocalDateTime.of(2024, 1, 24, 0, 0));
        eventService.addEvent(eventDtoRequest, principal);

        MonthsWorkingResult monthsWorkingResult = eventService.getMonthsWorkingResult(owner.getId(), 2024, 1);
        assertEquals(Month.JANUARY, monthsWorkingResult.getMonth());
        assertEquals(17, monthsWorkingResult.getTotalBusinessDaysNumber());
        assertEquals(17, monthsWorkingResult.getActualBusinessDaysNumber());
        assertEquals(136, monthsWorkingResult.getWorkingTime());
    }

    private EventDtoUpdateRequest getEventUpdateRequest() {
        EventDtoUpdateRequest eventDtoUpdateRequest = new EventDtoUpdateRequest();
        eventDtoUpdateRequest.setStartDate(eventDtoRequest.getStartDate());
        eventDtoUpdateRequest.setEndDate(eventDtoRequest.getStartDate());
        eventDtoUpdateRequest.setName(eventDtoRequest.getName());
        eventDtoUpdateRequest.setDescription(eventDtoRequest.getDescription());
        eventDtoUpdateRequest.setEventType(eventDtoRequest.getEventType());

        return eventDtoUpdateRequest;
    }

}