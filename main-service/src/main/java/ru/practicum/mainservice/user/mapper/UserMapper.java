package ru.practicum.mainservice.user.mapper;

import ru.practicum.mainservice.user.model.dto.user.NewUserRequest;
import ru.practicum.mainservice.user.model.dto.user.UserDto;
import ru.practicum.mainservice.user.model.user.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static User buildUserForPost(NewUserRequest newUserRequest) {
        return User.builder()
                .name(newUserRequest.getName())
                .email(newUserRequest.getEmail())
                .privateAccount(newUserRequest.isPrivateAccount())
                .build();
    }

    public static User buildUserForPatch(Long userId, UserDto userDto) {
        return User.builder()
                .id(userId)
                .name(userDto.getName())
                .email(userDto.getEmail())
                .privateAccount(userDto.isPrivateAccount())
                .build();
    }

    public static UserDto buildUserDto(User user) {
        return UserDto.builder()
                .email(user.getEmail())
                .id(user.getId())
                .name(user.getName())
                .privateAccount(user.getPrivateAccount())
                .build();
    }

    public static List<UserDto> buildUserDtoList(List<User> users) {
        return users.stream()
                .map(UserMapper::buildUserDto)
                .collect(Collectors.toList());
    }
}
