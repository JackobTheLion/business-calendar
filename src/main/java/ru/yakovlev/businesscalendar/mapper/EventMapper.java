package ru.yakovlev.businesscalendar.mapper;

import ru.yakovlev.businesscalendar.dto.event.EventDtoFullResponse;
import ru.yakovlev.businesscalendar.dto.event.EventDtoRequest;
import ru.yakovlev.businesscalendar.dto.event.EventDtoShortResponse;
import ru.yakovlev.businesscalendar.dto.user.UserDtoShortResponse;
import ru.yakovlev.businesscalendar.model.event.Event;

public class EventMapper {
    public static Event mapFromDto(EventDtoRequest eventDtoRequest) {
        return Event.builder()
                .startDate(eventDtoRequest.getStartDate())
                .endDate(eventDtoRequest.getEndDate())
                .eventType(eventDtoRequest.getEventType())
                .name(eventDtoRequest.getName())
                .description(eventDtoRequest.getDescription())
                .build();
    }

    public static EventDtoShortResponse mapToShortDto(Event event) {
        return EventDtoShortResponse.builder()
                .id(event.getId())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .eventType(event.getEventType())
                .name(event.getName())
                .build();
    }

    public static EventDtoFullResponse mapToFullDto(Event event) {
        UserDtoShortResponse owner = null;
        if (event.getOwner() != null) {
            owner = UserMapper.mapToShortDto(event.getOwner());
        }

        return EventDtoFullResponse.builder()
                .id(event.getId())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .eventType(event.getEventType())
                .name(event.getName())
                .description(event.getDescription())
                .owner(owner)
                .build();
    }
}
