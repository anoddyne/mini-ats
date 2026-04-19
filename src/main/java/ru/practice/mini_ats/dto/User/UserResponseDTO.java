package ru.practice.mini_ats.dto.User;


public record UserResponseDTO (
    Integer userId,
    String name,
    String surname,
    String patronymic,
    Integer age,
    String phoneNumber,
    String email,
    String login,
    String role
) {}
