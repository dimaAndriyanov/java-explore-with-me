package ru.practicum.ewm.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.State;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    @Query("select e.id " +
            "from Event e " +
            "where e.initiator.id = ?1")
    Page<Long> findAllEventsIdsByInitiatorId(Long userId, Pageable pageable);

    @Query("select e " +
            "from Event e " +
            "join fetch e.initiator " +
            "join fetch e.category " +
            "where e.id in (?1)")
    List<Event> findAllByIdEager(List<Long> ids);

    @Query("select e " +
            "from Event e " +
            "join fetch e.initiator " +
            "join fetch e.category " +
            "where e.id = ?1 " +
            "and e.initiator.id = ?2")
    Optional<Event> findByIdAndInitiatorId(Long eventId, Long initiatorId);

    List<Event> findAllByCategoryId(Long categoryId);

    @Query("select e.id from Event e")
    Page<Long> findAllEventsIds(Pageable pageable);

    @Query("select e " +
            "from Event e " +
            "join fetch e.initiator " +
            "join fetch e.category " +
            "where e.id = ?1")
    Optional<Event> findByIdEager(Long id);

    @Query("select e " +
            "from Event e " +
            "join fetch e.initiator " +
            "join fetch e.category " +
            "where e.id = ?1 and e.state = ?2")
    Optional<Event> findByIdAndState(Long id, State state);

    @Query("select e " +
            "from Event e " +
            "join fetch e.initiator " +
            "join fetch e.category " +
            "where e.id in (?1)")
    List<Event> findAllByIdEager(Iterable<Long> ids);

    List<Event> findAllByInitiatorIdAndState(Long id, State state);
}