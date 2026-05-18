package ru.practice.mini_ats.controllers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practice.mini_ats.dto.LoginRequest;
import ru.practice.mini_ats.security.jwt.JwtUtils;
import ru.practice.mini_ats.services.UserService;

import java.util.Map;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;

    @Autowired
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody @NonNull LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.login(), loginRequest.password()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        assert userDetails != null;
        var user = userService.getUserByLogin(loginRequest.login());
        String jwt = jwtUtils.generateToken(userDetails);
        return ResponseEntity.ok(Map.of("token", jwt, "user", user));
    }
}

