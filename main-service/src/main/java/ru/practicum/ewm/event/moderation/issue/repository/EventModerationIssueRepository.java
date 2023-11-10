package ru.practicum.ewm.event.moderation.issue.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.event.moderation.issue.model.EventModerationIssue;
import ru.practicum.ewm.event.moderation.issue.model.EventModerationIssueStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventModerationIssueRepository extends JpaRepository<EventModerationIssue, Long> {
    List<EventModerationIssue> findAllByEventInitiatorIdAndIssueStatus(Long initiatorId, EventModerationIssueStatus issueStatus);

    List<EventModerationIssue> findAllByEventInitiatorId(Long initiatorId);

    List<EventModerationIssue> findAllByEventIdAndIssueStatus(Long eventId, EventModerationIssueStatus issueStatus);

    List<EventModerationIssue> findAllByEventId(Long eventId);

    @Query("select i " +
            "from EventModerationIssue i " +
            "join fetch i.event e " +
            "where i.id = ?1 and i.event.initiator.id = ?2")
    Optional<EventModerationIssue> findByIdAndEventInitiatorId(Long issueId, Long initiatorId);

    Page<EventModerationIssue> findAllByIssueStatus(EventModerationIssueStatus issueStatus, Pageable pageable);
}