package ru.practicum.mainservice.user.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.exception.model.NotFoundException;
import ru.practicum.mainservice.user.mapper.UserMapper;
import ru.practicum.mainservice.user.model.dto.user.NewUserRequest;
import ru.practicum.mainservice.user.model.dto.user.UserDto;
import ru.practicum.mainservice.user.model.user.User;
import ru.practicum.mainservice.user.repository.user.UserRepository;
import ru.practicum.mainservice.utils.PageRequestFactory;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public User findUserById(Long userId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));
        log.info("Запрос в UserServiceImpl обработан успешно. " +
                "Метод findUserById(), findUser={}", findUser);

        return findUser;

    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> findUsers(List<Long> ids, int from, int size) {
        List<User> findUsers = new ArrayList<>();
        Pageable page = PageRequestFactory.buildPageRequestWithoutSort(from, size);

        if (ids != null) {
            findUsers = userRepository.findByIdIn(ids, page);
        } else {
            findUsers = userRepository.findAll(page).getContent();
        }

        log.info("GET запрос в AdminUserController обработан успешно. " +
                "Метод findUsers(), findUsers={}", findUsers);

        return UserMapper.buildUserDtoList(findUsers);
    }

    @Transactional
    @Override
    public UserDto createUser(NewUserRequest newUserRequest) {
        User buildUser = UserMapper.buildUserForPost(newUserRequest);
        User createUser = userRepository.save(buildUser);
        log.info("POST запрос в AdminUserController обработан успешно. " +
                "Метод createUser(), createUser={}", createUser);

        return UserMapper.buildUserDto(createUser);
    }

    @Transactional
    @Override
    public void deleteUserById(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));

        userRepository.deleteById(userId);
        log.info("DELETE запрос в AdminUserController обработан успешно. " +
                "Метод deleteUserById()");
    }
}
