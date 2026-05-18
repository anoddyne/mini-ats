package ru.practice.mini_ats.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practice.mini_ats.dto.User.UserRequestDTO;
import ru.practice.mini_ats.dto.User.UserResponseDTO;
import ru.practice.mini_ats.dto.User.UserUpdateDTO;
import ru.practice.mini_ats.models.User;
import ru.practice.mini_ats.models.enums.UserRole;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperImplTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toResponseDto_ShouldMapAllFields() {
        User user = new User();
        user.setUserId(1);
        user.setName("John");
        user.setSurname("Doe");
        user.setPatronymic("Smith");
        user.setAge(30);
        user.setPhoneNumber("+123456789");
        user.setEmail("john@example.com");
        user.setLogin("johndoe");
        user.setRole(UserRole.CANDIDATE);
        user.setActive(true);

        UserResponseDTO dto = mapper.toResponseDto(user);

        assertThat(dto.userId()).isEqualTo(1);
        assertThat(dto.name()).isEqualTo("John");
        assertThat(dto.surname()).isEqualTo("Doe");
        assertThat(dto.patronymic()).isEqualTo("Smith");
        assertThat(dto.age()).isEqualTo(30);
        assertThat(dto.phoneNumber()).isEqualTo("+123456789");
        assertThat(dto.email()).isEqualTo("john@example.com");
        assertThat(dto.login()).isEqualTo("johndoe");
        assertThat(dto.role()).isEqualTo("CANDIDATE");
    }

    @Test
    void toEntity_ShouldMapRequestDtoToUser_IgnoringUserIdRoleActive() {
        UserRequestDTO dto = new UserRequestDTO(
                "Alice", "Johnson", "Marie", 25, "+987654321",
                "alice@example.com", "alice123", "securePass", UserRole.ADMIN
        );

        User user = mapper.toEntity(dto);

        assertThat(user.getName()).isEqualTo("Alice");
        assertThat(user.getSurname()).isEqualTo("Johnson");
        assertThat(user.getPatronymic()).isEqualTo("Marie");
        assertThat(user.getAge()).isEqualTo(25);
        assertThat(user.getPhoneNumber()).isEqualTo("+987654321");
        assertThat(user.getEmail()).isEqualTo("alice@example.com");
        assertThat(user.getLogin()).isEqualTo("alice123");
        assertThat(user.getPassword()).isEqualTo("securePass");

        assertThat(user.getUserId()).isNull();
        assertThat(user.getRole()).isNull();
    }

    @Test
    void updateEntityFromDto_ShouldUpdateExistingUser_IgnoringUserIdPasswordActiveRole() {
        User existing = new User();
        existing.setUserId(10);
        existing.setName("Old");
        existing.setSurname("OldSurname");
        existing.setPatronymic("OldPatr");
        existing.setAge(20);
        existing.setPhoneNumber("000");
        existing.setEmail("old@example.com");
        existing.setLogin("oldlogin");
        existing.setPassword("oldpass");
        existing.setRole(UserRole.CANDIDATE);
        existing.setActive(true);

        UserUpdateDTO dto = new UserUpdateDTO(
                "NewName", "NewSurname", "NewPatr", 30, "111",
                "new@example.com", "newpass"
        );

        mapper.updateEntityFromDto(dto, existing);

        // Обновляемые поля
        assertThat(existing.getName()).isEqualTo("NewName");
        assertThat(existing.getSurname()).isEqualTo("NewSurname");
        assertThat(existing.getPatronymic()).isEqualTo("NewPatr");
        assertThat(existing.getAge()).isEqualTo(30);
        assertThat(existing.getPhoneNumber()).isEqualTo("111");
        assertThat(existing.getEmail()).isEqualTo("new@example.com");
        // Игнорируемые поля
        assertThat(existing.getUserId()).isEqualTo(10);
        assertThat(existing.getPassword()).isEqualTo("oldpass");
        assertThat(existing.getRole()).isEqualTo(UserRole.CANDIDATE);
        assertThat(existing.getLogin()).isEqualTo("oldlogin");
    }

    @Test
    void updateEntityFromDto_WhenDtoHasNullFields_ShouldSetNullInEntity() {
        User existing = new User();
        existing.setName("OldName");
        existing.setSurname("OldSurname");
        existing.setPatronymic("OldPatr");
        existing.setAge(25);
        existing.setPhoneNumber("123");
        existing.setEmail("old@example.com");
        existing.setPassword("oldpass");

        UserUpdateDTO dto = new UserUpdateDTO(
                "NewName", null, null, null, null, "new@example.com", null
        );

        mapper.updateEntityFromDto(dto, existing);

        assertThat(existing.getName()).isEqualTo("NewName");
        assertThat(existing.getSurname()).isNull();
        assertThat(existing.getPatronymic()).isNull();
        assertThat(existing.getAge()).isNull();
        assertThat(existing.getPhoneNumber()).isNull();
        assertThat(existing.getEmail()).isEqualTo("new@example.com");
        assertThat(existing.getPassword()).isEqualTo("oldpass");
    }
}