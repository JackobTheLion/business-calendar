package ru.yakovlev.businesscalendar.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yakovlev.businesscalendar.model.event.Event;
import ru.yakovlev.businesscalendar.model.event.EventType;
import ru.yakovlev.businesscalendar.model.user.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByOwnerAndDeletedOrderByStartDate(User owner, Pageable page, Boolean deleted);

    List<Event> findByOwnerAndEventTypeInAndDeleted(User owner, Collection<EventType> eventType, Boolean deleted);

    Optional<Event> findByIdAndDeleted(Long eventId, Boolean deleted);
}
