package ru.practicum.ewm.event.moderation.issue.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.event.moderation.issue.dto.EventModerationIssueResolveDto;
import ru.practicum.ewm.event.moderation.issue.model.EventModerationIssue;
import ru.practicum.ewm.event.moderation.issue.model.EventModerationIssueStatus;
import ru.practicum.ewm.event.moderation.issue.repository.EventModerationIssueRepository;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.CanNotUpdateIssueException;
import ru.practicum.ewm.exception.ObjectNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventModerationIssueServiceImpl implements EventModerationIssueService {
    private final EventModerationIssueRepository issueRepository;
    private final EventRepository eventRepository;


    @Override
    @Transactional
    public EventModerationIssue createEventModerationIssue(EventModerationIssue issue) {
        issue.getEvent().setState(State.CHANGE_REQUIRED);
        issueRepository.save(issue);
        log.info("Issue\n{}\nhas been created", issue);
        return issue;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventModerationIssue> getUsersEventModerationIssues(Long userId, Boolean onlyOpened) {
        if (onlyOpened != null && onlyOpened) {
            return issueRepository.findAllByEventInitiatorIdAndIssueStatus(userId, EventModerationIssueStatus.OPENED);
        } else {
            return issueRepository.findAllByEventInitiatorId(userId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventModerationIssue> getUsersEventModerationIssuesByEventId(Long userId, Long eventId, Boolean onlyOpened) {
        if (onlyOpened != null && onlyOpened) {
            return issueRepository.findAllByEventIdAndIssueStatus(userId, EventModerationIssueStatus.OPENED);
        } else {
            return issueRepository.findAllByEventId(userId);
        }
    }

    @Override
    @Transactional
    public EventModerationIssue replyToEventModerationIssue(Long issueId, Long userId, String reply) {
        EventModerationIssue issue = issueRepository.findByIdAndEventInitiatorId(issueId, userId)
                .orElseThrow(() -> new ObjectNotFoundException(
                        "Issue with id =" + issueId + " was not found"
                ));
        if (!issue.getIssueStatus().equals(EventModerationIssueStatus.OPENED)) {
            throw new CanNotUpdateIssueException("Can reply only to OPENED event moderation issues");
        }
        issue.setUsersReply(reply);
        issue.setIssueStatus(EventModerationIssueStatus.REPLIED);
        if (issueRepository.findAllByEventIdAndIssueStatus(issue.getEvent().getId(), EventModerationIssueStatus.OPENED)
                .isEmpty()) {
            issue.getEvent().setState(State.PENDING);
            log.info("State of event with id ={} is now PENDING due to absence of OPENED issues", issue.getEvent().getId());
        }
        issueRepository.save(issue);
        log.info("Reply: {} has been added to issue with id={} and status has been set to REPLIED", reply, issueId);
        return issue;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventModerationIssue> getEventModerationIssues(Boolean onlyReplied, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        if (onlyReplied != null && onlyReplied) {
            return issueRepository.findAllByIssueStatus(EventModerationIssueStatus.REPLIED, pageRequest).getContent();
        } else {
            return issueRepository.findAll(pageRequest).getContent();
        }
    }

    @Override
    @Transactional
    public List<EventModerationIssue> resolveRepliedEventModerationIssues(Map<Long, EventModerationIssueResolveDto> resolves) {
        List<EventModerationIssue> issues = issueRepository.findAllById(resolves.keySet());
        if (issues.size() != resolves.size()) {
            throw new ObjectNotFoundException("Some of event moderation issues with id in " + resolves.keySet() + " was not found");
        }
        if (issues
                .stream()
                .anyMatch(issue -> !issue.getIssueStatus().equals(EventModerationIssueStatus.REPLIED))) {
            throw new CanNotUpdateIssueException("Can resolve only REPLIED event moderation issues");
        }
        issues.forEach(issue -> {
            String issueMessage = resolves.get(issue.getId()).getMessage();
            EventModerationIssueStatus issueStatus = resolves.get(issue.getId()).getIssueStatus();
            if (issueMessage != null && !issueMessage.isBlank()) {
                issue.setIssueMessage(issueMessage);
                issue.setUsersReply(null);
            }
            issue.setIssueStatus(issueStatus);
        });
        log.info("Issues {} has been updated", issues);
        List<Event> eventsToUpdateState = eventRepository.findAllById(issues
                .stream()
                .filter(issue -> issue.getIssueStatus().equals(EventModerationIssueStatus.OPENED))
                .map(issue -> issue.getEvent().getId())
                .collect(Collectors.toList()));
        eventsToUpdateState.forEach(event -> event.setState(State.CHANGE_REQUIRED));
        log.info("Statuses of events with id in {} has been updated to CHANGE_REQUIRED due to reopened issues",
                eventsToUpdateState
                        .stream()
                        .map(Event::getId)
                        .collect(Collectors.toList()));
        eventRepository.saveAll(eventsToUpdateState);
        issueRepository.saveAll(issues);
        return issues;
    }
}