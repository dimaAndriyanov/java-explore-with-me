package ru.practicum.ewm.user.util;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.user.dto.UserRequestDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class UserMapper {
    public User toUser(UserRequestDto userRequestDto) {
        return new User(null, userRequestDto.getName(), userRequestDto.getEmail());
    }

    public UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public List<UserDto> toUserDtos(List<User> users) {
        return users
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserShortDto toUserShortDto(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }
}