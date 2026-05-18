package ru.practice.mini_ats.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.practice.mini_ats.dto.User.UserRequestDTO;
import ru.practice.mini_ats.dto.User.UserResponseDTO;
import ru.practice.mini_ats.dto.User.UserUpdateDTO;
import ru.practice.mini_ats.mapper.UserMapper;
import ru.practice.mini_ats.models.User;
import ru.practice.mini_ats.models.enums.UserRole;
import ru.practice.mini_ats.repositories.UserRepository;
import ru.practice.mini_ats.security.SecurityUtils;
import ru.practice.mini_ats.services.UserService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

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
        user.setPassword("encodedPassword");
        user.setRole(UserRole.CANDIDATE);
        user.setActive(true);
        return user;
    }

    private static UserRequestDTO getRequestDto() {
        return new UserRequestDTO(
                "Иван", "Петров", "Иванович", 30,
                "+7(999)123-45-67", "ivan@example.com", "ivanp", "rawPassword",
                UserRole.CANDIDATE
        );
    }

    private static UserResponseDTO getResponseDto() {
        return new UserResponseDTO(
                1, "Иван", "Петров", "Иванович", 30,
                "+7(999)123-45-67", "ivan@example.com", "ivanp", "CANDIDATE"
        );
    }

    // ==================== CREATE USER ====================

    @Test
    void createUser_WhenLoginAndEmailBothExist_ShouldThrowException() {
        UserRequestDTO requestDto = getRequestDto();

        when(userRepository.existsByLogin(requestDto.login())).thenReturn(true);
        when(userRepository.existsByEmail(requestDto.email())).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(requestDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Пользователь с таким логином или почтой уже существует");

        verify(userRepository).existsByLogin(requestDto.login());
        verify(userRepository).existsByEmail(requestDto.email());
        verify(userMapper, never()).toEntity(any());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_WhenLoginExistsButEmailFree_ShouldCreateSuccessfully() {
        UserRequestDTO requestDto = getRequestDto();
        User userToSave = new User();
        User savedUser = getUser();
        UserResponseDTO expectedResponse = getResponseDto();

        when(userRepository.existsByLogin(requestDto.login())).thenReturn(true);
        when(userRepository.existsByEmail(requestDto.email())).thenReturn(false);
        when(userMapper.toEntity(requestDto)).thenReturn(userToSave);
        when(passwordEncoder.encode(requestDto.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toResponseDto(savedUser)).thenReturn(expectedResponse);

        UserResponseDTO result = userService.createUser(requestDto);

        assertThat(result).isEqualTo(expectedResponse);
        assertThat(userToSave.getRole()).isEqualTo(requestDto.role());
        assertThat(userToSave.isActive()).isTrue();
        assertThat(userToSave.getPassword()).isEqualTo("encodedPassword");
        verify(userMapper).toEntity(requestDto);
        verify(passwordEncoder).encode(requestDto.password());
        verify(userRepository).save(userToSave);
    }

    // ==================== GET ALL USERS ====================

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

    // ==================== GET USER BY ID ====================

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

    // ==================== DELETE USER ====================

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

    // ==================== UPDATE ROLE ====================

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

    // ==================== UPDATE USER (CURRENT USER) ====================

    @Test
    void updateUser_WhenCurrentUserExistsAndPasswordProvided_ShouldUpdatePasswordAndFields() {
        String currentLogin = "ivanp";
        UserUpdateDTO updateDto = new UserUpdateDTO(
                "НовоеИмя", "НоваяФамилия", null, 35,
                null, "newemail@example.com", "newPassword"
        );
        User existingUser = getUser();
        User updatedUser = getUser();
        updatedUser.setName("НовоеИмя");
        updatedUser.setSurname("НоваяФамилия");
        updatedUser.setPatronymic(null);
        updatedUser.setAge(35);
        updatedUser.setPhoneNumber(null);
        updatedUser.setEmail("newemail@example.com");
        updatedUser.setPassword("encodedNewPassword");

        UserResponseDTO expectedResponse = new UserResponseDTO(
                1, "НовоеИмя", "НоваяФамилия", null, 35,
                null, "newemail@example.com", "ivanp", "CANDIDATE"
        );

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(currentLogin);
            when(userRepository.findByLogin(currentLogin)).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
            doNothing().when(userMapper).updateEntityFromDto(updateDto, existingUser);
            when(userRepository.save(existingUser)).thenReturn(updatedUser);
            when(userMapper.toResponseDto(updatedUser)).thenReturn(expectedResponse);

            UserResponseDTO result = userService.updateUser(updateDto);

            assertThat(result).isEqualTo(expectedResponse);
            assertThat(existingUser.getPassword()).isEqualTo("encodedNewPassword");
            verify(userRepository).findByLogin(currentLogin);
            verify(passwordEncoder).encode("newPassword");
            verify(userMapper).updateEntityFromDto(updateDto, existingUser);
            verify(userRepository).save(existingUser);
        }
    }

    @Test
    void updateUser_WhenCurrentUserExistsAndPasswordIsBlank_ShouldNotUpdatePassword() {
        String currentLogin = "ivanp";
        UserUpdateDTO updateDto = new UserUpdateDTO(
                "НовоеИмя", "НоваяФамилия", null, 35,
                null, "newemail@example.com", ""
        );
        User existingUser = getUser();
        User updatedUser = getUser();
        updatedUser.setName("НовоеИмя");
        updatedUser.setSurname("НоваяФамилия");
        updatedUser.setEmail("newemail@example.com");

        UserResponseDTO expectedResponse = new UserResponseDTO(
                1, "НовоеИмя", "НоваяФамилия", null, 35,
                null, "newemail@example.com", "ivanp", "CANDIDATE"
        );

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(currentLogin);
            when(userRepository.findByLogin(currentLogin)).thenReturn(Optional.of(existingUser));
            doNothing().when(userMapper).updateEntityFromDto(updateDto, existingUser);
            when(userRepository.save(existingUser)).thenReturn(updatedUser);
            when(userMapper.toResponseDto(updatedUser)).thenReturn(expectedResponse);

            UserResponseDTO result = userService.updateUser(updateDto);

            assertThat(result).isEqualTo(expectedResponse);
            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository).save(existingUser);
        }
    }

    @Test
    void updateUser_WhenCurrentUserNotFound_ShouldThrowEntityNotFoundException() {
        String currentLogin = "unknown";
        UserUpdateDTO updateDto = new UserUpdateDTO("a", "b", null, 20, null, "e@x.com", null);

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserLogin).thenReturn(currentLogin);
            when(userRepository.findByLogin(currentLogin)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateUser(updateDto))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Пользователь не найден");

            verify(userRepository).findByLogin(currentLogin);
            verify(userMapper, never()).updateEntityFromDto(any(), any());
            verify(userRepository, never()).save(any());
        }
    }

    // ==================== GET USER BY LOGIN ====================

    @Test
    void getUserByLogin_WhenExists_ShouldReturnResponseDto() {
        String login = "ivanp";
        User user = getUser();
        UserResponseDTO expectedDto = getResponseDto();

        when(userRepository.findUserByLogin(login)).thenReturn(user);
        when(userMapper.toResponseDto(user)).thenReturn(expectedDto);

        UserResponseDTO result = userService.getUserByLogin(login);

        assertThat(result).isEqualTo(expectedDto);
        verify(userRepository).findUserByLogin(login);
        verify(userMapper).toResponseDto(user);
    }
}