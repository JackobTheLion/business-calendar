package ru.yakovlev.businesscalendar.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Business calendar Api",
                description = "Simple backend for Business calendar APP.",
                contact = @Contact(
                        name = "Egor Yakovlev",
                        email = "riddik24@yandex.ru",
                        url = "https://github.com/JackobTheLion"
                )
        )
)
public class OpenApiConfig {
}