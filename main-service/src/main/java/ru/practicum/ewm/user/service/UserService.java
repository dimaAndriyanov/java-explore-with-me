package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.model.User;

import java.util.List;

public interface UserService {
    List<User> getUsers(List<Long> ids, int from, int size);

    User createUser(User user);

    void deleteUserById(Long id);

    User getUserById(Long id);
}