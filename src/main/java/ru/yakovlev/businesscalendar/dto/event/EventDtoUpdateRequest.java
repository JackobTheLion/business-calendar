package ru.yakovlev.businesscalendar.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.yakovlev.businesscalendar.model.event.EventType;
import ru.yakovlev.businesscalendar.validation.ValidationGroups.Create;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Update event request by admin")
public class EventDtoUpdateRequest extends EventDtoRequest {
    private Boolean deleted;

    @Builder(builderMethodName = "updateBuilder")
    public EventDtoUpdateRequest(@NotNull(groups = Create.class) LocalDateTime startDate,
                                 @NotNull(groups = Create.class) LocalDateTime endDate,
                                 @NotNull(groups = Create.class) EventType eventType,
                                 @NotBlank(groups = Create.class) @Size(max = 255) String name,
                                 @NotBlank(groups = Create.class) @Size(max = 2000) String description,
                                 Boolean deleted) {
        super(startDate, endDate, eventType, name, description);
        this.deleted = deleted;
    }
}
