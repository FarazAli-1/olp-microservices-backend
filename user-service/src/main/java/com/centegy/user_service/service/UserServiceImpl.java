package com.centegy.user_service.service;

import com.centegy.common.dto.PageResponse;
import com.centegy.user_service.dto.response.UserResponseDto;
import com.centegy.user_service.mapper.UserMapper;
import com.centegy.user_service.model.User;
import com.centegy.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Cacheable(value = "users", key = "#username")
    public UserResponseDto getUser(String username) {
        log.info("Fetching user from database: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.mapToUserResponseDto(user);
    }

    @Override
    @Cacheable(value = "usersList", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort")
    public PageResponse<UserResponseDto> getAllUsers(Pageable pageable) {
        log.info("Fetching paginated users from database");
        Page<User> pagedData = userRepository.findAll(pageable);

        List<UserResponseDto> content = pagedData.getContent().stream()
                .map(userMapper::mapToUserResponseDto)
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                pagedData.getNumber(),
                pagedData.getSize(),
                pagedData.getTotalPages(),
                pagedData.getTotalElements(),
                pagedData.getNumberOfElements(),
                pagedData.isFirst(),
                pagedData.isLast(),
                pagedData.hasNext(),
                pagedData.hasPrevious()
        );
    }

    @Override
    @Caching(
            put = { @CachePut(value = "users", key = "#username") },
            evict = { @CacheEvict(value = "usersList", allEntries = true) }
    )
    public UserResponseDto updateUser(String username, String newEmail) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEmail(newEmail);
        User savedUser = userRepository.save(user);
        return userMapper.mapToUserResponseDto(savedUser);
    }


    @Override
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#username"),
            @CacheEvict(value = "usersList", allEntries = true)
    })
    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }
}