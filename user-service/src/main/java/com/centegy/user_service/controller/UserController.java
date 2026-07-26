package com.centegy.user_service.controller;

import com.centegy.common.dto.ApiResponse;
import com.centegy.common.dto.PageResponse;
import com.centegy.user_service.dto.response.UserResponseDto;
import com.centegy.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUser(@PathVariable String username) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Retrieved user", userService.getUser(username)));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserResponseDto>>> getAllUsers(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Retrieved all users", userService.getAllUsers(pageable)));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{username}")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(
            @PathVariable String username, @RequestParam String newEmail
    ) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Updated user", userService.updateUser(username, newEmail)));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{username}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok(new ApiResponse<>(true, "Deleted user", null));
    }
}