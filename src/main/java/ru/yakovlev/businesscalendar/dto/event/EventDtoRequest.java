package ru.yakovlev.businesscalendar.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Create event request")
public class EventDtoRequest {

    @NotNull(groups = Create.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime startDate;

    @NotNull(groups = Create.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime endDate;

    @NotNull(groups = Create.class)
    protected EventType eventType;

    @NotBlank(groups = Create.class)
    @Size(max = 255)
    protected String name;

    @NotBlank(groups = Create.class)
    @Size(max = 2000)
    protected String description;
}
