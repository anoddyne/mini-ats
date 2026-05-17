package ru.practice.mini_ats.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.org.hamcrest.Matchers;
import ru.practice.mini_ats.controllers.UserController;
import ru.practice.mini_ats.dto.User.UserRequestDTO;
import ru.practice.mini_ats.dto.User.UserResponseDTO;
import ru.practice.mini_ats.dto.User.UserUpdateDTO;
import ru.practice.mini_ats.models.enums.UserRole;
import ru.practice.mini_ats.services.UserService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private static final List<UserResponseDTO> users = List.of(
            new UserResponseDTO(
                    1,
                    "name1",
                    "surname1",
                    "patronymic1",
                    25,
                    "+88005553535",
                    "name1@mail.ru",
                    "login1",
                    "CANDIDATE"
            ),
            new UserResponseDTO(
                    2,
                    "name2",
                    "surname2",
                    "patronymic2",
                    25,
                    "+88005553536",
                    "name2@mail.ru",
                    "login2",
                    "CANDIDATE"
            )
    );

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void registerUserTest() throws Exception {
        UserRequestDTO requestDTO = new UserRequestDTO(
                "name1",
                "surname1",
                "patronymic1",
                25,
                "+88005553535",
                "name1@mail.ru",
                "login1",
                "password1",
                UserRole.CANDIDATE
        );
        UserResponseDTO responseDTO = users.getFirst();

        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.name").value("name1"))
                .andExpect(jsonPath("$.surname").value("surname1"))
                .andExpect(jsonPath("$.age").value(25))
                .andExpect(jsonPath("$.phoneNumber").value("+88005553535"))
                .andExpect(jsonPath("$.email").value("name1@mail.ru"))
                .andExpect(jsonPath("$.login").value("login1"))
                .andExpect(jsonPath("$.role").value("CANDIDATE"));

        verify(userService, times(1)).createUser(any(UserRequestDTO.class));
    }

    @Test
    void getNotEmptyListUsersTest() throws Exception {
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].name").value("name1"))
                .andExpect(jsonPath("$[0].surname").value("surname1"))
                .andExpect(jsonPath("$[0].age").value(25))
                .andExpect(jsonPath("$[0].phoneNumber").value("+88005553535"))
                .andExpect(jsonPath("$[0].email").value("name1@mail.ru"))
                .andExpect(jsonPath("$[0].login").value("login1"))
                .andExpect(jsonPath("$[0].role").value("CANDIDATE"))
                .andExpect(jsonPath("$[1].userId").value(2))
                .andExpect(jsonPath("$[1].name").value("name2"))
                .andExpect(jsonPath("$[1].surname").value("surname2"))
                .andExpect(jsonPath("$[1].age").value(25))
                .andExpect(jsonPath("$[1].phoneNumber").value("+88005553536"))
                .andExpect(jsonPath("$[1].email").value("name2@mail.ru"))
                .andExpect(jsonPath("$[1].login").value("login2"))
                .andExpect(jsonPath("$[1].role").value("CANDIDATE"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getEmptyListUsersTest() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUserByIdTest() throws Exception {
        Integer id = 1;
        UserResponseDTO user = users.getFirst();

        when(userService.getUserById(id)).thenReturn(user);

        mockMvc.perform(get("/api/v1/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.name").value("name1"))
                .andExpect(jsonPath("$.surname").value("surname1"))
                .andExpect(jsonPath("$.age").value(25))
                .andExpect(jsonPath("$.phoneNumber").value("+88005553535"))
                .andExpect(jsonPath("$.email").value("name1@mail.ru"))
                .andExpect(jsonPath("$.login").value("login1"))
                .andExpect(jsonPath("$.role").value("CANDIDATE"));

        verify(userService, times(1)).getUserById(id);
    }

    @Test
    void updateUserTest() throws Exception {
        // Обновление текущего пользователя через PUT /me
        UserUpdateDTO updateDTO = new UserUpdateDTO(
                "updatedName",
                "updatedSurname",
                "updatedPatronymic",
                30,
                "+79998887766",
                "updated@mail.ru",
                "newPassword"
        );
        UserResponseDTO updatedResponse = new UserResponseDTO(
                1,
                "updatedName",
                "updatedSurname",
                "updatedPatronymic",
                30,
                "+79998887766",
                "updated@mail.ru",
                "login1",
                "CANDIDATE"
        );

        when(userService.updateUser(any(UserUpdateDTO.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.name").value("updatedName"))
                .andExpect(jsonPath("$.surname").value("updatedSurname"))
                .andExpect(jsonPath("$.age").value(30))
                .andExpect(jsonPath("$.phoneNumber").value("+79998887766"))
                .andExpect(jsonPath("$.email").value("updated@mail.ru"))
                .andExpect(jsonPath("$.login").value("login1"))
                .andExpect(jsonPath("$.role").value("CANDIDATE"));

        verify(userService, times(1)).updateUser(any(UserUpdateDTO.class));
    }

    @Test
    void updateUserRoleTest() throws Exception {
        Integer id = 1;
        UserRole newRole = UserRole.ADMIN;
        UserResponseDTO updatedResponse = new UserResponseDTO(
                1,
                "name1",
                "surname1",
                "patronymic1",
                25,
                "+88005553535",
                "name1@mail.ru",
                "login1",
                "ADMIN"
        );

        when(userService.updateRole(eq(id), eq(newRole))).thenReturn(updatedResponse);

        mockMvc.perform(patch("/api/v1/users/{id}/role", id)
                        .param("role", newRole.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        verify(userService, times(1)).updateRole(eq(id), eq(newRole));
    }

    @Test
    void deleteUserTest() throws Exception {
        Integer id = 1;
        doNothing().when(userService).deleteUser(id);

        mockMvc.perform(delete("/api/v1/users/{id}", id))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(id);
    }
}