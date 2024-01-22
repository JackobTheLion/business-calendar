package ru.yakovlev.businesscalendar.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yakovlev.businesscalendar.dto.user.UserDtoShortResponse;
import ru.yakovlev.businesscalendar.model.event.EventType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDtoFullResponse {
    private Long id;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private EventType eventType;

    private String name;

    private String description;

    private UserDtoShortResponse owner;
}
