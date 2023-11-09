package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.event.participation.request.model.EventParticipationRequest;
import ru.practicum.ewm.event.participation.request.model.Status;
import ru.practicum.ewm.event.participation.request.repository.EventParticipationRequestRepository;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.CanNotDeleteObjectException;
import ru.practicum.ewm.exception.EmailIsAlreadyInUseException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventParticipationRequestRepository requestRepository;

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsers(List<Long> ids, int from, int size) {
        if (ids == null || ids.isEmpty()) {
            PageRequest pageRequest = PageRequest.of(from / size, size);
            return userRepository.findAll(pageRequest).getContent();
        } else {
            return userRepository.findAllById(ids);
        }
    }

    @Override
    @Transactional
    public User createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new EmailIsAlreadyInUseException("Email " + user.getEmail() + " is already in use");
        }
        userRepository.save(user);
        log.info("User with\nid={}\nname={}\nemail={}\nhas been created",
                user.getId(), user.getName(), user.getEmail());
        return user;
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new ObjectNotFoundException("User with id=" + id + " was not found");
        }
        if (!eventRepository.findAllByInitiatorIdAndState(id, State.PUBLISHED).isEmpty()) {
            throw new CanNotDeleteObjectException("Can not delete user with id=" + id +
                    " due to user is initiator in published even");
        }
        List<EventParticipationRequest> confirmedUsersRequests =
                requestRepository.findAllByRequesterIdAndStatus(id, Status.CONFIRMED);
        if (!confirmedUsersRequests.isEmpty()) {
            List<Long> eventIds = confirmedUsersRequests
                    .stream()
                    .map(request -> request.getEvent().getId())
                    .collect(Collectors.toList());
            List<Event> events = eventRepository.findAllById(eventIds);
            events.forEach(event -> event.decreaseConfirmedRequestsBy(1));
            requestRepository.deleteAll(confirmedUsersRequests);
        }
        userRepository.deleteById(id);
        log.info("User with id={} has been deleted", id);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        try {
            return userRepository.findById(id).get();
        } catch (NoSuchElementException exception) {
            throw new ObjectNotFoundException("User with id=" + id + " was not found");
        }
    }
}