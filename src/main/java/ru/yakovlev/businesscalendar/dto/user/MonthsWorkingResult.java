package ru.yakovlev.businesscalendar.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Month;
import java.time.Year;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonthsWorkingResult {
    private UserDtoShortResponse employee;
    private Year year;
    private Month month;
    private Integer totalBusinessDaysNumber;
    private Integer actualBusinessDaysNumber;
    private Integer workingTime;
    private Integer daysOff;
}
