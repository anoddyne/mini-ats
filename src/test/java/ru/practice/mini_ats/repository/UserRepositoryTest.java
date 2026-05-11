package ru.practice.mini_ats.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.practice.mini_ats.models.User;
import ru.practice.mini_ats.models.enums.UserRole;
import ru.practice.mini_ats.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
public class UserRepositoryTest {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17.0");

    @Autowired
    private UserRepository userRepository;

    @Test
    void testContainerIsRunning() {
        assertThat(POSTGRES.isCreated()).isTrue();
        assertThat(POSTGRES.isRunning()).isTrue();
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    // ========== Базовые CRUD методы (унаследованы от JpaRepository) ==========

    @Test
    void saveUserTest() {
        User user = new User();
        user.setName("John");
        user.setSurname("Doe");
        user.setEmail("john@example.com");
        user.setLogin("johndoe");
        user.setPassword("secret");
        user.setRole(UserRole.CANDIDATE);

        User saved = userRepository.save(user);
        assertThat(saved.getUserId()).isNotNull();

        Optional<User> found = userRepository.findById(saved.getUserId());
        assertThat(found).isPresent();
        assertThat(found.get().getLogin()).isEqualTo("johndoe");
    }

    @Test
    void findByIdTest() {
        User user = new User();
        user.setName("Find");
        user.setSurname("ById");
        user.setEmail("find@example.com");
        user.setLogin("findbyid");
        user.setPassword("pass");
        user.setRole(UserRole.CANDIDATE);
        User saved = userRepository.save(user);

        Optional<User> found = userRepository.findById(saved.getUserId());
        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo(saved.getUserId());
    }

    @Test
    void findAllTest() {
        User u1 = new User();
        u1.setName("A");
        u1.setSurname("A");
        u1.setEmail("a@example.com");
        u1.setLogin("a1");
        u1.setPassword("p");
        u1.setRole(UserRole.CANDIDATE);

        User u2 = new User();
        u2.setName("B");
        u2.setSurname("B");
        u2.setEmail("b@example.com");
        u2.setLogin("b1");
        u2.setPassword("p");
        u2.setRole(UserRole.ADMIN);

        userRepository.saveAll(List.of(u1, u2));
        List<User> users = userRepository.findAll();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getLogin).containsExactlyInAnyOrder("a1", "b1");
    }

    @Test
    void deleteByIdTest() {
        User user = new User();
        user.setName("Delete");
        user.setSurname("Me");
        user.setEmail("delete@example.com");
        user.setLogin("deleteid");
        user.setPassword("p");
        user.setRole(UserRole.CANDIDATE);
        User saved = userRepository.save(user);

        userRepository.deleteById(saved.getUserId());
        Optional<User> found = userRepository.findById(saved.getUserId());
        assertThat(found).isEmpty();
    }

    @Test
    void deleteAllTest() {
        User u1 = new User();
        u1.setName("A");
        u1.setSurname("A");
        u1.setEmail("delall1@example.com");
        u1.setLogin("del1");
        u1.setPassword("p");
        u1.setRole(UserRole.CANDIDATE);

        User u2 = new User();
        u2.setName("B");
        u2.setSurname("B");
        u2.setEmail("delall2@example.com");
        u2.setLogin("del2");
        u2.setPassword("p");
        u2.setRole(UserRole.CANDIDATE);

        userRepository.saveAll(List.of(u1, u2));
        userRepository.deleteAll();

        assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    void updateUserTest() {
        User user = new User();
        user.setName("Old");
        user.setSurname("Old");
        user.setEmail("update@example.com");
        user.setLogin("updateme");
        user.setPassword("pass");
        user.setRole(UserRole.CANDIDATE);
        User saved = userRepository.save(user);

        saved.setName("New");
        saved.setSurname("New");
        userRepository.save(saved);

        Optional<User> updated = userRepository.findById(saved.getUserId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("New");
        assertThat(updated.get().getSurname()).isEqualTo("New");
    }

    // ========== Кастомные методы репозитория ==========

    @Test
    void existsByLoginTest() {
        User user = new User();
        user.setName("LoginExists");
        user.setSurname("Test");
        user.setEmail("loginExist@example.com");
        user.setLogin("existentlogin");
        user.setPassword("pass");
        user.setRole(UserRole.CANDIDATE);
        userRepository.save(user);

        assertThat(userRepository.existsByLogin("existentlogin")).isTrue();
        assertThat(userRepository.existsByLogin("nonexistent")).isFalse();
    }

    @Test
    void existsByEmailTest() {
        User user = new User();
        user.setName("EmailExists");
        user.setSurname("Test");
        user.setEmail("existemail@example.com");
        user.setLogin("emailexist");
        user.setPassword("pass");
        user.setRole(UserRole.CANDIDATE);
        userRepository.save(user);

        assertThat(userRepository.existsByEmail("existemail@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("unknown@example.com")).isFalse();
    }

    @Test
    void findByLoginTest() {
        User user = new User();
        user.setName("FindByLogin");
        user.setSurname("Test");
        user.setEmail("findlogin@example.com");
        user.setLogin("findmelogin");
        user.setPassword("pass");
        user.setRole(UserRole.CANDIDATE);
        userRepository.save(user);

        Optional<User> found = userRepository.findByLogin("findmelogin");
        assertThat(found).isPresent();
        assertThat(found.get().getLogin()).isEqualTo("findmelogin");

        Optional<User> notFound = userRepository.findByLogin("wrong");
        assertThat(notFound).isEmpty();
    }

    @Test
    void findUserByLoginTest() {
        User user = new User();
        user.setName("FindUserByLogin");
        user.setSurname("Test");
        user.setEmail("finduserlogin@example.com");
        user.setLogin("finduser");
        user.setPassword("pass");
        user.setRole(UserRole.CANDIDATE);
        userRepository.save(user);

        User found = userRepository.findUserByLogin("finduser");
        assertThat(found).isNotNull();
        assertThat(found.getLogin()).isEqualTo("finduser");

        User notFound = userRepository.findUserByLogin("wrong");
        assertThat(notFound).isNull();
    }
}