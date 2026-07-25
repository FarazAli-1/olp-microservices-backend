package com.centegy.user_service.controller;

import com.centegy.common.dto.ApiResponse;
import com.centegy.user_service.dto.response.UserResponseDto;
import com.centegy.user_service.mapper.UserMapper;
import com.centegy.user_service.model.User;
import com.centegy.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users") // FIXED PATH
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUser(@PathVariable String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserResponseDto userResponseDto = userMapper.mapToUserResponseDto(user);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Successfully retrieved user", userResponseDto)
        );
    }
}
