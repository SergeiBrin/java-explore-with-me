package ru.practicum.mainservice.user.service;

import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.user.model.dto.NewUserRequest;
import ru.practicum.mainservice.user.model.dto.UserDto;

import java.util.List;

public interface UserService {
    User findUserById(Long userId);

    List<UserDto> findUsers(List<Long> ids, int from, int size);

    UserDto createUser(NewUserRequest newUserRequest);

    void deleteUserById(Long userId);
}
