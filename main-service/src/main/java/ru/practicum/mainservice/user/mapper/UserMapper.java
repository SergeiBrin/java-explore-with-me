package ru.practicum.mainservice.user.mapper;

import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.user.model.dto.NewUserRequest;
import ru.practicum.mainservice.user.model.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static User buildUserForPost(NewUserRequest newUserRequest) {
        return User.builder()
                .name(newUserRequest.getName())
                .email(newUserRequest.getEmail())
                .build();
    }

    public static User buildUserForPatch(Long userId, UserDto userDto) {
        return User.builder()
                .id(userId)
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static UserDto buildUserDto(User user) {
        return UserDto.builder()
                .email(user.getEmail())
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public static List<UserDto> buildUserDtoList(List<User> users) {
        return users.stream()
                .map(UserMapper::buildUserDto)
                .collect(Collectors.toList());
    }
}
