package ru.practice.mini_ats.dto.User;


import jakarta.validation.constraints.*;

public record UserRequestDTO(
        @NotBlank(message = "Имя пользователя не может быть пустым")
        String name,

        @NotBlank(message = "Фамилия пользователя не может быть пустой")
        String surname,

        String patronymic,

        @Min(18)
        Integer age,

        String phoneNumber,

        @Email(message = "Неверный формат почты")
        @NotBlank(message = "Необходимо указать электронную почту")
        String email,

        @NotBlank(message = "Логин не может быть пустым")
        @Size(min = 3, max = 50, message = "Длина логина должна быть от 3 до 30 символов")
        String login,

        @NotBlank
        @Size(min = 8, max = 100, message = "Длина пароля должна быть от 8 до 100 символов")
        String password
) {
}
