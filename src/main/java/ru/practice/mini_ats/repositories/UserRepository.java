package ru.practice.mini_ats.repositories;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practice.mini_ats.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserByLogin(@NotBlank String login);

    User findUserByEmail(@Email @NotBlank String email);

    User findUserByLoginAndPassword(@NotBlank String login, @NotBlank String password);

    void deleteUserByLogin(@NotBlank String login);

    void deleteUserByEmail(@Email @NotBlank String email);

    User findUserByNameAndSurname(@NotBlank String name, @NotBlank String surname);
}
