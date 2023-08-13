package ru.practicum.mainservice.user.service.user;

import ru.practicum.mainservice.user.model.dto.user.NewUserRequest;
import ru.practicum.mainservice.user.model.dto.user.UserDto;
import ru.practicum.mainservice.user.model.user.User;

import java.util.List;

public interface UserService {
    User findUserById(Long userId);

    List<UserDto> findUsers(List<Long> ids, int from, int size);

    UserDto createUser(NewUserRequest newUserRequest);

    void deleteUserById(Long userId);
}
