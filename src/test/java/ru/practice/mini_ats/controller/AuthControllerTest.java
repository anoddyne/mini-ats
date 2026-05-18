package ru.practice.mini_ats.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practice.mini_ats.controllers.AuthController;
import ru.practice.mini_ats.dto.LoginRequest;
import ru.practice.mini_ats.dto.User.UserResponseDTO;
import ru.practice.mini_ats.security.jwt.JwtUtils;
import ru.practice.mini_ats.services.UserService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UserResponseDTO createUserResponse() {
        return new UserResponseDTO(
                1,
                "John",
                "Doe",
                null,
                30,
                "+71234567890",
                "john@example.com",
                "john_doe",
                "CANDIDATE"
        );
    }

    @Test
    void loginSuccessTest() throws Exception {
        LoginRequest loginRequest = new LoginRequest("john@example.com", "password123");
        UserDetails userDetails = new User("john@example.com", "password123", Collections.emptyList());
        UserResponseDTO userResponse = createUserResponse();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(userDetails, null));
        when(jwtUtils.generateToken(userDetails)).thenReturn("mocked.jwt.token");
        when(userService.getUserByLogin(loginRequest.login())).thenReturn(userResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked.jwt.token"))
                .andExpect(jsonPath("$.user.userId").value(1))
                .andExpect(jsonPath("$.user.login").value("john_doe"));
    }

    @Test
    void loginWithInvalidCredentialsTest() throws Exception {
        LoginRequest loginRequest = new LoginRequest("wrong@example.com", "wrongPass");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    void loginWithEmptyLoginTest() throws Exception {
        LoginRequest emptyLoginRequest = new LoginRequest("", "password");

        // При отсутствии валидации на DTO запрос дойдёт до authenticationManager,
        // который может выбросить исключение. Тест просто проверяет, что не 200.
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyLoginRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void loginWithMissingRequestBodyTest() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginWhenJwtGenerationFailsTest() throws Exception {
        LoginRequest loginRequest = new LoginRequest("user@example.com", "password123");
        UserDetails userDetails = new User("user@example.com", "password123", Collections.emptyList());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(userDetails, null));
        when(jwtUtils.generateToken(userDetails)).thenThrow(new RuntimeException("JWT generation error"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest()) // 400
                .andExpect(jsonPath("$.message").value("JWT generation error"));
    }
}