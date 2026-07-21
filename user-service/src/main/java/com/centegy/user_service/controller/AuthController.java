package com.centegy.user_service.controller;

import com.centegy.common.dto.ApiResponse;
import com.centegy.user_service.dto.request.LoginRequestDto;
import com.centegy.user_service.dto.request.SignUpRequestDto;
import com.centegy.user_service.dto.response.AuthResponse;
import com.centegy.user_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signUp")
    public ResponseEntity<ApiResponse<AuthResponse>> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        log.info("Attempting to register new user with email: {}", signUpRequestDto.getEmail());
        AuthResponse authResponse = authService.signUp(signUpRequestDto);
        log.info("User registered successfully: {}", authResponse.getUsername());
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "User registered Successfully",
                        authResponse
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequestDto loginRequestDto) {
        log.info("Attempting to login user");
        AuthResponse authResponse = authService.login(loginRequestDto);
        log.info("User logged successfully: {}", authResponse.getUsername());
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "User logged Successfully",
                        authResponse
                )
        );

    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestParam String refreshToken) {
        log.info("Attempting to refresh user");
        AuthResponse authResponse = authService.refreshToken(refreshToken);
        log.info("User refreshed successfully: {}", authResponse.getUsername());
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "User refreshed successfully",
                        authResponse
                )
        );
    }



}
