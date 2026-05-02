package ru.practice.mini_ats.mapper;

import org.junit.jupiter.api.Test;
import ru.practice.mini_ats.dto.User.UserRequestDTO;
import ru.practice.mini_ats.dto.User.UserResponseDTO;
import ru.practice.mini_ats.models.User;
import ru.practice.mini_ats.models.enums.UserRole;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperImplTest {

    private final UserMapper mapper = new UserMapperImpl();

    @Test
    void toResponseDto_shouldMapAllFields() {
        User user = new User();
        user.setUserId(100);
        user.setName("Иван");
        user.setSurname("Петров");
        user.setPatronymic("Иванович");
        user.setAge(30);
        user.setPhoneNumber("+7(999)123-45-67");
        user.setEmail("ivan@example.com");
        user.setLogin("ivanp");
        user.setRole(UserRole.ADMIN);

        UserResponseDTO dto = mapper.toResponseDto(user);

        assertThat(dto.userId()).isEqualTo(100);
        assertThat(dto.name()).isEqualTo("Иван");
        assertThat(dto.surname()).isEqualTo("Петров");
        assertThat(dto.patronymic()).isEqualTo("Иванович");
        assertThat(dto.age()).isEqualTo(30);
        assertThat(dto.phoneNumber()).isEqualTo("+7(999)123-45-67");
        assertThat(dto.email()).isEqualTo("ivan@example.com");
        assertThat(dto.login()).isEqualTo("ivanp");
        assertThat(dto.role()).isEqualTo("ADMIN");
    }

    @Test
    void toResponseDto_shouldHandleNullRole() {
        User user = new User();
        user.setUserId(1);
        user.setName("NoRole");
        user.setRole(null);

        UserResponseDTO dto = mapper.toResponseDto(user);

        assertThat(dto.role()).isNull();
    }

    @Test
    void toResponseDto_shouldHandleNullInput() {
        UserResponseDTO dto = mapper.toResponseDto(null);
        assertThat(dto).isNull();
    }

    @Test
    void toEntity_shouldMapAllFields() {
        UserRequestDTO request = new UserRequestDTO(
                "Анна",
                "Сидорова",
                "Алексеевна",
                25,
                "8-800-555-35-35",
                "anna@mail.ru",
                "annas",
                "qwerty123"
        );

        User entity = mapper.toEntity(request);

        assertThat(entity.getUserId()).isNull(); // ID генерируется БД
        assertThat(entity.getName()).isEqualTo("Анна");
        assertThat(entity.getSurname()).isEqualTo("Сидорова");
        assertThat(entity.getPatronymic()).isEqualTo("Алексеевна");
        assertThat(entity.getAge()).isEqualTo(25);
        assertThat(entity.getPhoneNumber()).isEqualTo("8-800-555-35-35");
        assertThat(entity.getEmail()).isEqualTo("anna@mail.ru");
        assertThat(entity.getLogin()).isEqualTo("annas");
        assertThat(entity.getPassword()).isEqualTo("qwerty123");
        assertThat(entity.getRole()).isNull(); // роль не устанавливается из запроса
    }

    @Test
    void toEntity_shouldHandleNullInput() {
        User entity = mapper.toEntity(null);
        assertThat(entity).isNull();
    }

    @Test
    void toEntity_shouldHandleNullOptionalFields() {
        UserRequestDTO request = new UserRequestDTO(
                "Минимальный",
                "Пользователь",
                null,
                null,
                null,
                "min@example.com",
                "minlogin",
                "pass"
        );

        User entity = mapper.toEntity(request);

        assertThat(entity.getName()).isEqualTo("Минимальный");
        assertThat(entity.getSurname()).isEqualTo("Пользователь");
        assertThat(entity.getPatronymic()).isNull();
        assertThat(entity.getAge()).isNull();
        assertThat(entity.getPhoneNumber()).isNull();
        assertThat(entity.getEmail()).isEqualTo("min@example.com");
        assertThat(entity.getLogin()).isEqualTo("minlogin");
        assertThat(entity.getPassword()).isEqualTo("pass");
        assertThat(entity.getRole()).isNull();
    }
}