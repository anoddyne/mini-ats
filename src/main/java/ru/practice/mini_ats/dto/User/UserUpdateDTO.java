package ru.practice.mini_ats.dto.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateDTO(
        @NotBlank(message = "Имя пользователя не может быть пустым")
        String name,

        @NotBlank(message = "Фамилия пользователя не может быть пустой")
        String surname,

        String patronymic,

        @Min(value = 18, message = "Возраст должен быть не менее 18 лет")
        Integer age,

        String phoneNumber,

        @Email(message = "Неверный формат почты")
        @NotBlank(message = "Необходимо указать электронную почту")
        String email,

        @Size(min = 8, max = 100, message = "Пароль должен быть от 8 символов")
        String password
){
}