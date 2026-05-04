package ru.practice.mini_ats.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practice.mini_ats.dto.User.UserRequestDTO;
import ru.practice.mini_ats.dto.User.UserResponseDTO;
import ru.practice.mini_ats.mapper.UserMapper;
import ru.practice.mini_ats.models.User;
import ru.practice.mini_ats.models.enums.UserRole;
import ru.practice.mini_ats.repositories.UserRepository;
import ru.practice.mini_ats.services.UserService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private static User getUser() {
        User user = new User();
        user.setUserId(1);
        user.setName("Иван");
        user.setSurname("Петров");
        user.setPatronymic("Иванович");
        user.setAge(30);
        user.setPhoneNumber("+7(999)123-45-67");
        user.setEmail("ivan@example.com");
        user.setLogin("ivanp");
        user.setPassword("password");
        user.setRole(UserRole.CANDIDATE);
        return user;
    }

    private static UserRequestDTO getRequestDto() {
        return new UserRequestDTO(
                "Иван", "Петров", "Иванович", 30,
                "+7(999)123-45-67", "ivan@example.com", "ivanp", "password"
        );
    }

    private static UserResponseDTO getResponseDto() {
        return new UserResponseDTO(
                1, "Иван", "Петров", "Иванович", 30,
                "+7(999)123-45-67", "ivan@example.com", "ivanp", "CANDIDATE"
        );
    }

    @Test
    void createUser_WhenLoginAndEmailUnique_ShouldReturnResponseDto() {
        UserRequestDTO requestDto = getRequestDto();
        User userToSave = new User();
        User savedUser = getUser();
        UserResponseDTO expectedResponse = getResponseDto();

        when(userRepository.existsByLogin(requestDto.login())).thenReturn(false);
        when(userRepository.existsByEmail(requestDto.email())).thenReturn(false);
        when(userMapper.toEntity(requestDto)).thenReturn(userToSave);
        when(userRepository.save(userToSave)).thenReturn(savedUser);
        when(userMapper.toResponseDto(savedUser)).thenReturn(expectedResponse);

        UserResponseDTO result = userService.createUser(requestDto);

        assertThat(result).isEqualTo(expectedResponse);
        assertThat(userToSave.getRole()).isEqualTo(UserRole.CANDIDATE);
        verify(userRepository).existsByLogin(requestDto.login());
        verify(userRepository).existsByEmail(requestDto.email());
        verify(userMapper).toEntity(requestDto);
        verify(userRepository).save(userToSave);
        verify(userMapper).toResponseDto(savedUser);
    }

    @Test
    void createUser_WhenLoginAlreadyExists_ShouldThrowException() {
        UserRequestDTO requestDto = getRequestDto();

        when(userRepository.existsByLogin(requestDto.login())).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(requestDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Пользователь с таким логином или почтой уже существует");

        verify(userRepository).existsByLogin(requestDto.login());
        verify(userRepository, never()).existsByEmail(any());
        verify(userMapper, never()).toEntity(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_WhenEmailAlreadyExists_ShouldThrowException() {
        UserRequestDTO requestDto = getRequestDto();

        when(userRepository.existsByLogin(requestDto.login())).thenReturn(false);
        when(userRepository.existsByEmail(requestDto.email())).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(requestDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Пользователь с таким логином или почтой уже существует");

        verify(userRepository).existsByLogin(requestDto.login());
        verify(userRepository).existsByEmail(requestDto.email());
        verify(userMapper, never()).toEntity(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void getAllUsers_ShouldReturnListOfResponseDto() {
        List<User> users = List.of(getUser());
        UserResponseDTO expectedDto = getResponseDto();

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toResponseDto(users.getFirst())).thenReturn(expectedDto);

        List<UserResponseDTO> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(expectedDto);
        verify(userRepository).findAll();
        verify(userMapper).toResponseDto(users.getFirst());
    }

    @Test
    void getUserById_WhenExists_ShouldReturnResponseDto() {
        User user = getUser();
        UserResponseDTO expectedDto = getResponseDto();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(expectedDto);

        UserResponseDTO result = userService.getUserById(1);

        assertThat(result).isEqualTo(expectedDto);
        verify(userRepository).findById(1);
    }

    @Test
    void getUserById_WhenNotExists_ShouldThrowEntityNotFoundException() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(999))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Пользователь с id 999 не найден");

        verify(userRepository).findById(999);
    }

    @Test
    void deleteUser_WhenExists_ShouldDelete() {
        when(userRepository.existsById(1)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1);

        userService.deleteUser(1);

        verify(userRepository).existsById(1);
        verify(userRepository).deleteById(1);
    }

    @Test
    void deleteUser_WhenNotExists_ShouldThrowEntityNotFoundException() {
        when(userRepository.existsById(999)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(999))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Не удалось удалить: пользователь с id999 не существует");

        verify(userRepository).existsById(999);
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void updateRole_WhenUserExists_ShouldUpdateAndReturnResponseDto() {
        User user = getUser();
        UserRole newRole = UserRole.ADMIN;
        User updatedUser = getUser();
        updatedUser.setRole(newRole);
        UserResponseDTO expectedDto = new UserResponseDTO(1, "Иван", "Петров", "Иванович", 30,
                "+7(999)123-45-67", "ivan@example.com", "ivanp", "ADMIN");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(updatedUser);
        when(userMapper.toResponseDto(updatedUser)).thenReturn(expectedDto);

        UserResponseDTO result = userService.updateRole(1, newRole);

        assertThat(result).isEqualTo(expectedDto);
        assertThat(user.getRole()).isEqualTo(newRole);
        verify(userRepository).findById(1);
        verify(userRepository).save(user);
        verify(userMapper).toResponseDto(updatedUser);
    }

    @Test
    void updateRole_WhenUserNotExists_ShouldThrowEntityNotFoundException() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateRole(999, UserRole.ADMIN))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Пользователь с id 999 не найден");

        verify(userRepository).findById(999);
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_WhenExists_ShouldUpdateAndReturnResponseDto() {
        Integer userId = 1;
        UserRequestDTO updateDto = new UserRequestDTO("НовоеИмя", "НоваяФамилия", null, 35,
                null, "newemail@example.com", "newlogin", "newpass");
        User existingUser = getUser();
        User updatedUser = getUser();
        updatedUser.setName("НовоеИмя");
        updatedUser.setSurname("НоваяФамилия");
        updatedUser.setPatronymic(null);
        updatedUser.setAge(35);
        updatedUser.setPhoneNumber(null);
        updatedUser.setEmail("newemail@example.com");
        updatedUser.setLogin("newlogin");
        updatedUser.setPassword("newpass");
        UserResponseDTO expectedResponse = new UserResponseDTO(1, "НовоеИмя", "НоваяФамилия", null, 35,
                null, "newemail@example.com", "newlogin", "CANDIDATE");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        doNothing().when(userMapper).updateEntityFromDto(updateDto, existingUser);
        when(userRepository.save(existingUser)).thenReturn(updatedUser);
        when(userMapper.toResponseDto(updatedUser)).thenReturn(expectedResponse);

        UserResponseDTO result = userService.updateUser(userId, updateDto);

        assertThat(result).isEqualTo(expectedResponse);
        verify(userRepository).findById(userId);
        verify(userMapper).updateEntityFromDto(updateDto, existingUser);
        verify(userRepository).save(existingUser);
        verify(userMapper).toResponseDto(updatedUser);
    }

    @Test
    void updateUser_WhenNotExists_ShouldThrowEntityNotFoundException() {
        Integer userId = 999;
        UserRequestDTO updateDto = getRequestDto();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(userId, updateDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Пользователь с id 999 не найден");

        verify(userRepository).findById(userId);
        verify(userMapper, never()).updateEntityFromDto(any(), any());
        verify(userRepository, never()).save(any());
    }
}