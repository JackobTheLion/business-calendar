package ru.yakovlev.businesscalendar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yakovlev.businesscalendar.dto.event.EventDtoFullResponse;
import ru.yakovlev.businesscalendar.dto.event.EventDtoRequest;
import ru.yakovlev.businesscalendar.model.event.EventType;
import ru.yakovlev.businesscalendar.model.user.User;
import ru.yakovlev.businesscalendar.repository.EventRepository;
import ru.yakovlev.businesscalendar.repository.UserRepository;
import ru.yakovlev.businesscalendar.repository.UserRoleRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@SpringBootTest
@WebAppConfiguration
@ActiveProfiles("test")
@Testcontainers
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class EventControllerTest {
    private MockMvc mockMvc;
    private final WebApplicationContext webApplicationContext;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final EventRepository eventRepository;
    private final PasswordEncoder passwordEncoder;

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

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        eventRepository.deleteAll();
        mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        User userToSave = User.builder()
                .userName("username")
                .email("email@email.com")
                .userRole(List.of(userRoleRepository.findUserRoleByName("ROLE_USER")))
                .enabled(true)
                .password(passwordEncoder.encode("123Aaa!"))
                .build();
        userRepository.save(userToSave);
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "username")
    public void addEvent_Normal() {
        EventDtoRequest eventDtoRequest = prepareEvent();

        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDtoRequest)))
                .andExpect(status().isCreated());
    }

    @SneakyThrows
    @Test
    public void addEvent_NotAuthorized() {
        EventDtoRequest eventDtoRequest = prepareEvent();

        mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDtoRequest)))
                .andExpect(status().isForbidden());
    }

    @SneakyThrows
    @Test
    @WithMockUser(username = "username")
    public void updateEvent_Normal() {
        EventDtoRequest eventDtoRequest = prepareEvent();
        String contentAsString = mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDtoRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        EventDtoFullResponse eventDtoFullResponse = objectMapper.readValue(contentAsString, EventDtoFullResponse.class);


        EventDtoRequest updatedEventDtoRequest = prepareEvent();
        updatedEventDtoRequest.setName("updated name");

        String response = mockMvc.perform(post("/event/" + eventDtoFullResponse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedEventDtoRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        EventDtoFullResponse updated = objectMapper.readValue(response, EventDtoFullResponse.class);
        assertEquals(updatedEventDtoRequest.getName(), updated.getName());
    }

    @SneakyThrows
    @Test
    public void updateEvent_NoRights() {
        EventDtoRequest eventDtoRequest = prepareEvent();
        mockMvc.perform(post("/event/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDtoRequest)))
                .andExpect(status().isForbidden());
    }

    private EventDtoRequest prepareEvent() {
        LocalDateTime now = LocalDateTime.now();
        return EventDtoRequest.builder()
                .startDate(now)
                .endDate(now.minusHours(1))
                .eventType(EventType.MEETING)
                .name("event name")
                .description("event description")
                .build();
    }
}