package com.centegy.user_service.service;

import com.centegy.common.dto.PageResponse;
import com.centegy.user_service.dto.response.UserResponseDto;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponseDto getUser(String username);
    PageResponse<UserResponseDto> getAllUsers(Pageable pageable);
    UserResponseDto updateUser(String username, String newEmail);
    void deleteUser(String username);
}