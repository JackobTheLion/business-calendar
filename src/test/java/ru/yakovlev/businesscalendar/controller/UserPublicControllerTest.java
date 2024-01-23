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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yakovlev.businesscalendar.dto.jwt.JwtRequest;
import ru.yakovlev.businesscalendar.dto.user.UserDtoRequestUser;
import ru.yakovlev.businesscalendar.model.user.User;
import ru.yakovlev.businesscalendar.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
@WebAppConfiguration
@ActiveProfiles("test")
@Testcontainers
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserPublicControllerTest {
    private MockMvc mockMvc;
    private final WebApplicationContext webApplicationContext;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
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
        mockMvc = webAppContextSetup(webApplicationContext)
                .build();
    }

    @SneakyThrows
    @Test
    public void register_Normal() {
        UserDtoRequestUser requestUser = prepareUserDtoRequest();

        mockMvc.perform(post("/userPub/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUser)))
                .andExpect(status().isCreated());
    }

    @SneakyThrows
    @Test
    public void register_AlreadyRegistered() {
        UserDtoRequestUser requestUser = prepareUserDtoRequest();

        mockMvc.perform(post("/userPub/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestUser)));

        mockMvc.perform(post("/userPub/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUser)))
                .andExpect(status().isConflict());
    }

    @SneakyThrows
    @Test
    public void register_PasswordTooShort() {
        UserDtoRequestUser requestUser = prepareUserDtoRequest();
        requestUser.setPassword("123");

        mockMvc.perform(post("/userPub/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUser)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    public void getToken_Normal() {
        User user = prepareUser();
        String password = "1234Aaa!";
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        JwtRequest jwtRequest = new JwtRequest(user.getUsername(), password);

        mockMvc.perform(post("/userPub/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jwtRequest)))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    public void getToken_AccountNotActivated() {
        UserDtoRequestUser requestUser = prepareUserDtoRequest();

        mockMvc.perform(post("/userPub/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestUser)));

        JwtRequest jwtRequest = new JwtRequest(requestUser.getUserName(), requestUser.getPassword());
        String string = objectMapper.writeValueAsString(jwtRequest);

        mockMvc.perform(post("/userPub/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(string))
                .andExpect(status().isUnauthorized());
    }

    @SneakyThrows
    @Test
    public void getToken_WrongPassword() {
        UserDtoRequestUser requestUser = prepareUserDtoRequest();

        mockMvc.perform(post("/userPub/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestUser)));

        JwtRequest jwtRequest = new JwtRequest(requestUser.getUserName(), "WRONG PASSWORD");


        mockMvc.perform(post("/userPub/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jwtRequest)))
                .andExpect(status().isUnauthorized());
    }

    private UserDtoRequestUser prepareUserDtoRequest() {
        return UserDtoRequestUser.builder()
                .userName("username")
                .email("email@email.com")
                .password("1234Aaa!")
                .build();
    }

    private User prepareUser() {
        return User.builder()
                .userName("username")
                .email("email@email.com")
                .enabled(true)
                .build();
    }
}