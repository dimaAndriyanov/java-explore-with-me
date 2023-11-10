package ru.practicum.ewm.event.moderation.issue.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.ewm.event.model.Event;

import javax.persistence.*;

@Entity
@Table(name = "event_moderation_issues")
@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class EventModerationIssue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_moderation_issue_id")
    private final Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @ToString.Exclude
    private final Event event;

    @Column(name = "issue_message", nullable = false)
    private String issueMessage;

    @Enumerated(EnumType.STRING)
    @Column(name = "issue_status", nullable = false)
    private EventModerationIssueStatus issueStatus;

    @Column(name = "users_reply")
    private String usersReply;
}