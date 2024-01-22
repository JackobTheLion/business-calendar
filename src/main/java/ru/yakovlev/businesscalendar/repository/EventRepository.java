package ru.yakovlev.businesscalendar.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yakovlev.businesscalendar.model.event.Event;
import ru.yakovlev.businesscalendar.model.event.EventType;
import ru.yakovlev.businesscalendar.model.user.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByOwnerOrderByStartDate(User owner, Pageable page);

    List<Event> findByOwnerAndEventTypeIn(User owner, Collection<EventType> eventType);
}
