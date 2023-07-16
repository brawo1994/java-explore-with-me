package ru.practicum.repository.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.enums.State;
import ru.practicum.model.event.Event;
import ru.practicum.model.request.Request;
import ru.practicum.model.user.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByRequester (User requester);

    List<Request> findByEventAndStatus(Event event, State status);

    List<Request> findByEventId(Long eventId);

    Optional<Request> findByEventAndRequester(Event event, User requester);
}
