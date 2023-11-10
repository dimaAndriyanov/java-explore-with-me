package ru.practicum.ewm.event.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.exception.DataIntegrityViolationException;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private final Long id;

    @Column(name = "event_name", nullable = false)
    private final String title;

    @Column(nullable = false)
    private final String annotation;

    @Column(nullable = false)
    private final String description;

    @Column(name = "event_date", nullable = false)
    private final LocalDateTime eventDate;

    @Column(name = "location_lat", nullable = false)
    private final Double locationLat;

    @Column(name = "location_lon", nullable = false)
    private final Double locationLon;

    @Column(nullable = false)
    private final Boolean paid;

    @Column(name = "participant_limit", nullable = false)
    private final Integer participantLimit;

    @Column(name = "request_moderation", nullable = false)
    private final Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private State state;

    @Column(name = "created_on", nullable = false)
    private final LocalDateTime createdOn;

    @Column(name = "published_on")
    private final LocalDateTime publishedOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    @ToString.Exclude
    private final User initiator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @ToString.Exclude
    private final Category category;

    @Column(name = "confirmed_requests")
    private Integer confirmedRequests;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Transient
    private Long views;

    public void increaseConfirmedRequestsBy(int n) {
        if (participantLimit != 0 && participantLimit < confirmedRequests + n) {
            throw new DataIntegrityViolationException("confirmedRequests can not be greater than participantLimit");
        }
        confirmedRequests += n;
    }

    public void decreaseConfirmedRequestsBy(int n) {
        if (confirmedRequests - n < 0) {
            throw new DataIntegrityViolationException("confirmedRequests can not be negative");
        }
        confirmedRequests -= n;
    }
}