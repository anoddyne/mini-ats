package ru.practice.mini_ats.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practice.mini_ats.dto.User.UserRequestDTO;
import ru.practice.mini_ats.dto.User.UserResponseDTO;
import ru.practice.mini_ats.dto.User.UserUpdateDTO;
import ru.practice.mini_ats.mapper.UserMapper;
import ru.practice.mini_ats.models.User;
import ru.practice.mini_ats.models.enums.UserRole;
import ru.practice.mini_ats.repositories.UserRepository;
import ru.practice.mini_ats.security.SecurityUtils;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO dto) {
        if (userRepository.existsByLogin(dto.login()) && userRepository.existsByEmail(dto.email())) {
            throw new RuntimeException("Пользователь с таким логином или почтой уже существует");
        }
        User user = userMapper.toEntity(dto);
        user.setRole(dto.role());
        user.setActive(true);
        user.setPassword(passwordEncoder.encode(dto.password()));

        return userMapper.toResponseDto(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Integer id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Пользователь с id " + id + " не найден")
        );
        return userMapper.toResponseDto(user);
    }

    @Transactional
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("Не удалось удалить: пользователь с id" + id + " не существует");
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public UserResponseDTO updateRole(Integer userId, UserRole newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id " + userId + " не найден"));
        user.setRole(newRole);
        return userMapper.toResponseDto(userRepository.save(user));
    }

    @Transactional
    public UserResponseDTO updateUser(UserUpdateDTO dto) {
        String login = SecurityUtils.getCurrentUserLogin();
        User user = userRepository.findByLogin(login).orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        if (dto.password() != null && !dto.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }
        userMapper.updateEntityFromDto(dto, user);
        return userMapper.toResponseDto(userRepository.save(user));
    }

    @Transactional
    public UserResponseDTO getUserByLogin(String login) {
        User user = userRepository.findUserByLogin(login);
        return userMapper.toResponseDto(user);
    }
}
