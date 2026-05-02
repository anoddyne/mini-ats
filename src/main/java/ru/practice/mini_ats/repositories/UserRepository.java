package ru.practice.mini_ats.repositories;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practice.mini_ats.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByLogin(@NotBlank(message = "Логин не может быть пустым") @Size(min = 3, max = 50, message = "Длина логина должна быть от 3 до 30 символов") String login);

    boolean existsByEmail(@Email(message = "Неверный формат почты") @NotBlank(message = "Необходимо указать электронную почту") String email);
}
