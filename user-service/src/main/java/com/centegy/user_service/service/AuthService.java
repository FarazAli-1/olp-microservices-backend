package com.centegy.user_service.service;


import com.centegy.security.JwtService;
import com.centegy.user_service.dto.request.LoginRequestDto;
import com.centegy.user_service.dto.request.SignUpRequestDto;
import com.centegy.user_service.dto.response.AuthResponse;
import com.centegy.user_service.model.RefreshToken;
import com.centegy.user_service.model.Role;
import com.centegy.user_service.model.User;
import com.centegy.user_service.repository.RefreshTokenRepository;
import com.centegy.user_service.repository.RoleRepository;
import com.centegy.user_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@AllArgsConstructor
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService  jwtService;
    private final RoleRepository roleRepository;


    @Transactional
    public AuthResponse signUp(SignUpRequestDto  signUpRequestDto) {

        User user = new User();
        Role role = roleRepository.findById(signUpRequestDto.getRoleId())
                        .orElseThrow(() -> new RuntimeException("User not Found"));

        user.setUsername(signUpRequestDto.getUsername());
        user.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));
        user.setEmail(signUpRequestDto.getEmail());

        user.setRole(role);

        List<String> roles = List.of(role.getLevel().name());

        User savedUser = userRepository.save(user);
        String refreshToken = jwtService.generateRefreshToken(savedUser.getUsername());
        String accessToken = jwtService.generateAccessToken(savedUser.getUsername(), savedUser.getId(), roles);

        RefreshToken refreshTokenObject = new RefreshToken();
        refreshTokenObject.setToken(refreshToken);
        refreshTokenObject.setUser(savedUser);
        refreshTokenObject.setExpiryDate(Instant.now().plus(1, ChronoUnit.HOURS));
        refreshTokenObject.setRevoked(false);

        return new AuthResponse(accessToken,refreshToken,savedUser.getUsername());
    }

    @Transactional
    public AuthResponse login(LoginRequestDto loginRequestDto) {


        User user = userRepository.findByUsername(loginRequestDto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not Found"));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Wrong password");
        }

        refreshTokenRepository.deleteByUser(user);

        List<String> roles = List.of(user.getRole().getLevel().name());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());
        String accessToken = jwtService.generateAccessToken(user.getUsername(), user.getId(), roles);

        RefreshToken refreshTokenObject = new RefreshToken();
        refreshTokenObject.setToken(refreshToken);
        refreshTokenObject.setUser(user);
        refreshTokenObject.setRevoked(false);
        refreshTokenObject.setExpiryDate(Instant.now().plus(1, ChronoUnit.HOURS));

        refreshTokenRepository.save(refreshTokenObject);

        return new AuthResponse(accessToken, refreshToken, user.getUsername());
    }


    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        RefreshToken refreshToken1 = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Not Found"));

        if (refreshToken1.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Token is expired");
        }

        refreshToken1.setExpiryDate(Instant.now().plus(1, ChronoUnit.HOURS));
        refreshTokenRepository.save(refreshToken1);

        User user = refreshToken1.getUser();
        List<String> roles = List.of(user.getRole().getLevel().name());
        String accessToken = jwtService.generateAccessToken(user.getUsername(), user.getId(), roles );

        return new AuthResponse(accessToken,refreshToken,user.getUsername());
    }
}
