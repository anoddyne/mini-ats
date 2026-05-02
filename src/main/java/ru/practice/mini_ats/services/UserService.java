package ru.practice.mini_ats.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practice.mini_ats.dto.User.UserRequestDTO;
import ru.practice.mini_ats.dto.User.UserResponseDTO;
import ru.practice.mini_ats.mapper.UserMapper;
import ru.practice.mini_ats.models.User;
import ru.practice.mini_ats.models.enums.UserRole;
import ru.practice.mini_ats.repositories.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO dto) {
        if (userRepository.existsByLogin(dto.login()) || userRepository.existsByEmail(dto.email())) {
            throw new RuntimeException("Пользователь с таким логином или почтой уже существует");
        }
        User user = userMapper.toEntity(dto);
        user.setRole(UserRole.CANDIDATE);

        // в будущем шифрование пароля
        // user.setPassword();

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
    public UserResponseDTO updateUser(Integer userId, UserRequestDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id " + userId + " не найден"));
        userMapper.updateEntityFromDto(dto, user);
        User updatedUser = userRepository.save(user);
        return userMapper.toResponseDto(updatedUser);
    }
}
