package ru.practice.mini_ats.mapper;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practice.mini_ats.dto.User.UserRequestDTO;
import ru.practice.mini_ats.dto.User.UserResponseDTO;
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
    void toEntity_ShouldMapRequestDtoToUser_IgnoringSpecificFields() {
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

        // Поля, которые должны игнорироваться
        assertThat(user.getUserId()).isNull();     // игнорируется
        assertThat(user.getRole()).isNull();       // игнорируется
    }

    @Test
    void updateEntityFromDto_ShouldUpdateExistingUser_IgnoringSpecificFields() {
        User existingUser = getUser();

        UserRequestDTO dto = new UserRequestDTO(
                "NewName", "NewSurname", "NewPatr", 30, "111",
                "new@example.com", "newlogin", "newpass", UserRole.ADMIN
        );

        mapper.updateEntityFromDto(dto, existingUser);

        // Обновляемые поля
        assertThat(existingUser.getName()).isEqualTo("NewName");
        assertThat(existingUser.getSurname()).isEqualTo("NewSurname");
        assertThat(existingUser.getPatronymic()).isEqualTo("NewPatr");
        assertThat(existingUser.getAge()).isEqualTo(30);
        assertThat(existingUser.getPhoneNumber()).isEqualTo("111");
        assertThat(existingUser.getEmail()).isEqualTo("new@example.com");
        assertThat(existingUser.getLogin()).isEqualTo("newlogin");
        
        assertThat(existingUser.getPassword()).isEqualTo("oldpass");
        assertThat(existingUser.getUserId()).isEqualTo(10);
        assertThat(existingUser.getRole()).isEqualTo(UserRole.CANDIDATE);
    }

    private static @NonNull User getUser() {
        User existingUser = new User();
        existingUser.setUserId(10);
        existingUser.setName("Old");
        existingUser.setSurname("OldSurname");
        existingUser.setPatronymic("OldPatr");
        existingUser.setAge(18);
        existingUser.setPhoneNumber("000");
        existingUser.setEmail("old@example.com");
        existingUser.setLogin("oldlogin");
        existingUser.setPassword("oldpass");
        existingUser.setRole(UserRole.CANDIDATE);
        existingUser.setActive(true);
        return existingUser;
    }

    @Test
    void toEntity_WhenDtoHasNullFields_ShouldSetNullInEntity() {
        UserRequestDTO dto = new UserRequestDTO(
                "Bob", "Builder", null, null, null,
                "bob@example.com", "bob", "pass", UserRole.CANDIDATE
        );

        User user = mapper.toEntity(dto);

        assertThat(user.getName()).isEqualTo("Bob");
        assertThat(user.getSurname()).isEqualTo("Builder");
        assertThat(user.getPatronymic()).isNull();
        assertThat(user.getAge()).isNull();
        assertThat(user.getPhoneNumber()).isNull();
        assertThat(user.getEmail()).isEqualTo("bob@example.com");
        assertThat(user.getLogin()).isEqualTo("bob");
        assertThat(user.getPassword()).isEqualTo("pass");
    }
}