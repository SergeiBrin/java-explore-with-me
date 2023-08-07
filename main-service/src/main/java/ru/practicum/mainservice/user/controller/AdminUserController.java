package ru.practicum.mainservice.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.user.model.dto.NewUserRequest;
import ru.practicum.mainservice.user.model.dto.UserDto;
import ru.practicum.mainservice.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDto>> findUsersByIds(@RequestParam(required = false) List<Long> ids,
                                                        @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                        @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Поступил GET запрос в AdminUserController. Метод findUsersByIds(), ids={}", ids);
        List<UserDto> findUsers= userService.findUsers(ids, from, size);

        return new ResponseEntity<>(findUsers, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        log.info("Поступил POST запрос в AdminUserController. Метод createUser(), newUserRequest={}", newUserRequest);
        UserDto createUser = userService.createUser(newUserRequest);

        return new ResponseEntity<>(createUser, HttpStatus.CREATED);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long userId) {
        log.info("Поступил DELETE запрос в AdminUserController. Метод deleteUserById(), userId={}", userId);
        userService.deleteUserById(userId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

