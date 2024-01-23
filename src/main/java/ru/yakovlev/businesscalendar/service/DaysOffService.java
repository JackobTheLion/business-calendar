package ru.yakovlev.businesscalendar.service;

import ru.yakovlev.businesscalendar.dto.event.DayOff;

import java.time.LocalDate;
import java.util.Map;

/**
 * Service, providing list of days of and short days in year.
 */
public interface DaysOffService {

    /**
     * Method providing list of days of and short days in year.
     * Day type 1 - holiday, 2 - short day, 3 - working day.
     *
     * @param year for which data should be provided
     * @return Map of date -> day type.
     */
    Map<LocalDate, Integer> getDaysOff(Integer year);
}
