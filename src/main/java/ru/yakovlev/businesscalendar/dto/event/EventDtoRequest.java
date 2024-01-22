package ru.yakovlev.businesscalendar.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yakovlev.businesscalendar.model.event.EventType;
import ru.yakovlev.businesscalendar.validation.ValidationGroups.Create;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDtoRequest {

    @NotNull(groups = Create.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    @NotNull(groups = Create.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    @NotNull(groups = Create.class)
    private EventType eventType;

    @NotBlank(groups = Create.class)
    @Size(max = 255)
    private String name;

    @NotBlank(groups = Create.class)
    @Size(max = 2000)
    private String description;
}
