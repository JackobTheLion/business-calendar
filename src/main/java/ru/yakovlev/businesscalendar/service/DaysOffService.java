package ru.yakovlev.businesscalendar.service;

import ru.yakovlev.businesscalendar.dto.event.DayOff;

import java.time.LocalDate;
import java.util.Map;

public interface DaysOffService {
    Map<LocalDate, Integer> getDaysOff(Integer year);
}
