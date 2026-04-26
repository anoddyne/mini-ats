package ru.practice.mini_ats.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.practice.mini_ats.models.User;
import ru.practice.mini_ats.models.enums.UserRole;
import ru.practice.mini_ats.repositories.UserRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgresSQLContainer =
            new PostgreSQLContainer<>("postgres:17.0");

    @Autowired
    private UserRepository userRepository;

    @Test
    void testThatConnectionEstablished() {
        assertThat(postgresSQLContainer.isCreated()).isTrue();
        assertThat(postgresSQLContainer.isRunning()).isTrue();
    }

    @BeforeEach
    void beforeEach() {
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void saveUserTest() {
        User user = new User();
        user.setName("John");
        user.setSurname("Doe");
        user.setPatronymic("Smith");
        user.setAge(30);
        user.setPhoneNumber("+123456789");
        user.setEmail("john.doe@example.com");
        user.setLogin("johndoe");
        user.setPassword("password123");
        user.setRole(UserRole.CANDIDATE);

        User savedUser = userRepository.save(user);
        User foundUser = userRepository.findById(savedUser.getUserId()).orElse(null);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser).isEqualTo(savedUser);
    }

    @Test
    void findAllUsersTest() {
        User user1 = new User();
        user1.setName("Alice");
        user1.setSurname("Smith");
        user1.setEmail("alice@example.com");
        user1.setLogin("alice");
        user1.setPassword("pass1");
        user1.setRole(UserRole.CANDIDATE);

        User user2 = new User();
        user2.setName("Bob");
        user2.setSurname("Johnson");
        user2.setEmail("bob@example.com");
        user2.setLogin("bob");
        user2.setPassword("pass2");
        user2.setRole(UserRole.ADMIN);

        userRepository.saveAll(List.of(user1, user2));
        List<User> usersFromRepo = userRepository.findAll();

        assertTrue(usersFromRepo.size() == 2 &&
                usersFromRepo.contains(user1) &&
                usersFromRepo.contains(user2));
    }

    @Test
    void deleteUserByIdTest() {
        User user = new User();
        user.setName("Test");
        user.setSurname("User");
        user.setEmail("delete@example.com");
        user.setLogin("deleteuser");
        user.setPassword("pass");
        user.setRole(UserRole.CANDIDATE);

        User saved = userRepository.save(user);
        userRepository.deleteById(saved.getUserId());

        assertThat(userRepository.findById(saved.getUserId()).isEmpty()).isTrue();
    }

    @Test
    void deleteAllUsersTest() {
        User user1 = new User();
        user1.setName("Temp1");
        user1.setSurname("Temp1");
        user1.setEmail("temp1@example.com");
        user1.setLogin("temp1");
        user1.setPassword("pass");
        user1.setRole(UserRole.CANDIDATE);

        User user2 = new User();
        user2.setName("Temp2");
        user2.setSurname("Temp2");
        user2.setEmail("temp2@example.com");
        user2.setLogin("temp2");
        user2.setPassword("pass");
        user2.setRole(UserRole.CANDIDATE);

        userRepository.saveAll(List.of(user1, user2));
        userRepository.deleteAll();

        List<User> users = userRepository.findAll();
        assertThat(users.isEmpty()).isTrue();
    }

    @Test
    void findUserByLogin() {
        User user = new User();
        user.setName("LoginTest");
        user.setSurname("Test");
        user.setEmail("login@example.com");
        user.setLogin("uniquelogin");
        user.setPassword("pass");
        user.setRole(UserRole.CANDIDATE);

        userRepository.save(user);
        User found = userRepository.findUserByLogin(user.getLogin());

        assertThat(found).isEqualTo(user);
    }

    @Test
    void findUserByEmail() {
        User user = new User();
        user.setName("EmailTest");
        user.setSurname("Test");
        user.setEmail("unique@example.com");
        user.setLogin("emailtest");
        user.setPassword("pass");
        user.setRole(UserRole.CANDIDATE);

        userRepository.save(user);
        User found = userRepository.findUserByEmail(user.getEmail());

        assertThat(found).isEqualTo(user);
    }

    @Test
    void findUserByLoginAndPassword() {
        User user = new User();
        user.setName("AuthTest");
        user.setSurname("Test");
        user.setEmail("auth@example.com");
        user.setLogin("authuser");
        user.setPassword("secret");
        user.setRole(UserRole.CANDIDATE);

        userRepository.save(user);
        User found = userRepository.findUserByLoginAndPassword(user.getLogin(), user.getPassword());

        assertThat(found).isEqualTo(user);
    }

    @Test
    void deleteUserByLogin() {
        User user = new User();
        user.setName("DeleteLogin");
        user.setSurname("Test");
        user.setEmail("deletelogin@example.com");
        user.setLogin("todelete");
        user.setPassword("pass");
        user.setRole(UserRole.CANDIDATE);

        userRepository.save(user);
        userRepository.deleteUserByLogin(user.getLogin());

        assertThat(userRepository.findUserByLogin(user.getLogin())).isNull();
    }

    @Test
    void deleteUserByEmail() {
        User user = new User();
        user.setName("DeleteEmail");
        user.setSurname("Test");
        user.setEmail("deleteemail@example.com");
        user.setLogin("deleteemailuser");
        user.setPassword("pass");
        user.setRole(UserRole.CANDIDATE);

        userRepository.save(user);
        userRepository.deleteUserByEmail(user.getEmail());

        assertThat(userRepository.findUserByEmail(user.getEmail())).isNull();
    }

    @Test
    void updateUserTest() {
        User user = new User();
        user.setName("OldName");
        user.setSurname("OldSurname");
        user.setEmail("update@example.com");
        user.setLogin("updatelogin");
        user.setPassword("pass");
        user.setRole(UserRole.CANDIDATE);

        User saved = userRepository.save(user);
        saved.setName("NewName");
        saved.setSurname("NewSurname");
        userRepository.save(saved);

        User updated = userRepository.findById(saved.getUserId()).orElse(null);
        assertThat(updated).isNotNull();
        assert updated != null;
        assertThat(updated.getName()).isEqualTo("NewName");
        assertThat(updated.getSurname()).isEqualTo("NewSurname");
    }

    // Дополнительный тест: поиск по имени и фамилии (если метод существует в репозитории)
    @Test
    void findUserByNameAndSurname() {
        User user = new User();
        user.setName("Jane");
        user.setSurname("Doe");
        user.setEmail("jane@example.com");
        user.setLogin("janedoe");
        user.setPassword("pass");
        user.setRole(UserRole.CANDIDATE);

        userRepository.save(user);
        User found = userRepository.findUserByNameAndSurname(user.getName(), user.getSurname());

        assertThat(found).isEqualTo(user);
    }
}
