package ru.practicum.repository.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.enums.State;
import ru.practicum.model.event.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findEventByIdAndInitiatorId(long eventId, long userId);

    List<Event> findEventsByInitiatorId(long userId, Pageable pageable);

    @Query("SELECT event FROM Event AS event " +
            "WHERE (:users is null OR event.initiator.id IN (:users)) " +
            "AND (:states is null OR event.state IN (:states)) " +
            "AND (:categories is null OR event.category.id IN (:categories)) " +
            "AND (cast(:start as timestamp) is null OR event.eventDate >= :start) " +
            "AND (cast(:end as timestamp) is null OR event.eventDate <= :end) ")
    List<Event> findEventsByFilters(List<Long> users, List<State> states, List<Long> categories,
                                    LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("SELECT event FROM Event AS event " +
            "WHERE (:text is null OR LOWER(event.annotation) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND (:categories is null OR event.category.id IN (:categories)) " +
            "AND (:paid is null OR event.paid = :paid) " +
            "AND (:state is null OR event.state = :state) " +
            "AND (cast(:start as timestamp) is null OR event.eventDate >= :start) " +
            "AND (cast(:end as timestamp) is null OR event.eventDate <= :end) ")
    List<Event> findPublicEvents(String text, List<Long> categories, Boolean paid, LocalDateTime start,
                                 LocalDateTime end, State state, Pageable pageable);

    List<Event> findEventsByCategoryId(long catId);

}
