package ru.practicum.ewm.event.participation.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.event.participation.request.model.EventParticipationRequest;
import ru.practicum.ewm.event.participation.request.model.Status;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventParticipationRequestRepository extends JpaRepository<EventParticipationRequest, Long> {
    List<EventParticipationRequest> findAllByRequesterId(Long requesterId);

    Optional<EventParticipationRequest> findByRequesterIdAndEventId(Long requesterId, Long eventId);

    @Query("select r " +
            "from EventParticipationRequest r " +
            "join fetch r.event " +
            "where r.id = ?1 and r.requester.id = ?2")
    Optional<EventParticipationRequest> findByIAndRequesterIddWithEvent(Long id, Long requesterId);

    List<EventParticipationRequest> findAllByEventId(Long eventId);

    List<EventParticipationRequest> findAllByEventIdAndStatus(Long eventId, Status status);

    long countByEventIdAndStatus(Long eventId, Status status);

    List<EventParticipationRequest> findAllByRequesterIdAndStatus(Long requesterId, Status status);
}