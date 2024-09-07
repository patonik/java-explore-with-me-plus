package ru.practicum.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.admin.repository.UserRepository;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.NewUserRequestMapper;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserDtoMapper;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final NewUserRequestMapper newUserRequestMapper;
    private final UserDtoMapper userDtoMapper;

    public List<UserDto> getUsers(Long[] ids, Integer page, Integer size) {
        Pageable pageRequest = PageRequest.of(page, size);
        return userRepository.findUserDtosByIds(ids, pageRequest);
    }

    @Transactional
    public UserDto addUser(NewUserRequest newUserRequest) {
        String email = newUserRequest.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Email already exists");
        }
        User saved = userRepository.save(newUserRequestMapper.toUser(newUserRequest));
        return userDtoMapper.toUserDto(saved);
    }

    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        userRepository.deleteById(userId);
    }
}
