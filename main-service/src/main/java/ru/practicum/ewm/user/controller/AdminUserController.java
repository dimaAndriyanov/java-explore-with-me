package ru.practicum.ewm.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.UserRequestDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.ewm.user.util.UserMapper.*;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminUserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                  @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                  @Positive @RequestParam(defaultValue = "10") int size) {
        String stringIds;
        if (ids == null) {
            stringIds = "null";
        } else if (ids.isEmpty()) {
            stringIds = "[]";
        } else {
            StringBuilder sb = new StringBuilder("[");
            for (Long id : ids) {
                sb.append(id).append(",");
            }
            sb.replace(sb.length() - 1, sb.length(), "]");
            stringIds = sb.toString();
        }
        log.info("Request on getting users with parameters:\nids=" + stringIds + "\nfrom={}\nsize={}\nhas been received",
                from, size);
        return toUserDtos(userService.getUsers(ids, from, size));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        log.info("Request on creating user with\nname={}\nemail={}\nhas been received",
                userRequestDto.getName(), userRequestDto.getEmail());
        return toUserDto(userService.createUser(toUser(userRequestDto)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        log.info("Request on deleting user with id={} has been received", id);
        userService.deleteUserById(id);
    }
}