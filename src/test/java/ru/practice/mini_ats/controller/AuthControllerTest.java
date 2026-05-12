//package ru.practice.mini_ats.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
//import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//import ru.practice.mini_ats.controllers.AuthController;
//import ru.practice.mini_ats.dto.LoginRequest;
//import ru.practice.mini_ats.security.jwt.JwtUtils;
//
//import java.util.Collections;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(AuthController.class)
//@AutoConfigureMockMvc(addFilters = false)
//public class AuthControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private AuthenticationManager authenticationManager;
//
//    @MockitoBean
//    private JwtUtils jwtUtils;
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Test
//    void loginSuccessTest() throws Exception {
//        LoginRequest loginRequest = new LoginRequest("user@example.com", "password123");
//        UserDetails userDetails = new User("user@example.com", "password123", Collections.emptyList());
//
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                .thenReturn(new UsernamePasswordAuthenticationToken(userDetails, null));
//        when(jwtUtils.generateToken(userDetails)).thenReturn("mocked.jwt.token");
//
//        mockMvc.perform(post("/api/v1/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(loginRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.token").value("mocked.jwt.token"));
//    }
//
//    @Test
//    void loginWithInvalidCredentialsTest() throws Exception {
//        LoginRequest loginRequest = new LoginRequest("wrong@example.com", "wrongPass");
//
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                .thenThrow(new BadCredentialsException("Invalid credentials"));
//
//        mockMvc.perform(post("/api/v1/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(loginRequest)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void loginWithEmptyLoginTest() throws Exception {
//        LoginRequest emptyLoginRequest = new LoginRequest("", "password");
//
//        mockMvc.perform(post("/api/v1/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(emptyLoginRequest)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void loginWithMissingRequestBodyTest() throws Exception {
//        mockMvc.perform(post("/api/v1/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void loginWhenJwtGenerationFailsTest() throws Exception {
//        LoginRequest loginRequest = new LoginRequest("user@example.com", "password123");
//        UserDetails userDetails = new User("user@example.com", "password123", Collections.emptyList());
//
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                .thenReturn(new UsernamePasswordAuthenticationToken(userDetails, null));
//        when(jwtUtils.generateToken(userDetails)).thenThrow(new RuntimeException("JWT generation error"));
//
//        mockMvc.perform(post("/api/v1/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(loginRequest)))
//                .andExpect(status().isBadRequest());
//    }
//}