package ru.practice.mini_ats.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practice.mini_ats.dto.User.UserRequestDTO;
import ru.practice.mini_ats.dto.User.UserResponseDTO;
import ru.practice.mini_ats.models.enums.UserRole;
import ru.practice.mini_ats.services.UserService;

import java.util.List;

@Tag(name = "User Controller",description = "Обрабатывает запросы, связанные с пользователями")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Регистрация нового пользователя")
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRequestDTO dto) {
        return new ResponseEntity<>(userService.createUser(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Получить список пользователей")
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getListUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @Operation(summary = "Получить информацию о пользователе по id")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Integer id) {
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    @Operation(summary = "Обновить информацию о пользователе по id")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Integer id, @Valid @RequestBody UserRequestDTO dto) {
        return new ResponseEntity<>(userService.updateUser(id, dto), HttpStatus.OK);
    }

    @Operation(summary = "Обновить роль пользователя по id")
    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponseDTO> updateUserRole(@PathVariable Integer id, @RequestParam UserRole role) {
        return new ResponseEntity<>(userService.updateRole(id, role), HttpStatus.OK);
    }

    @Operation(summary = "Удалить пользователя по id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
