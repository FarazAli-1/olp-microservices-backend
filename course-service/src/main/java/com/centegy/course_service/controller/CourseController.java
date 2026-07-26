package com.centegy.course_service.controller;

import com.centegy.common.dto.ApiResponse;
import com.centegy.common.dto.PageResponse;
import com.centegy.course_service.dto.request.CourseRequestDto;
import com.centegy.course_service.dto.response.CourseResponseDto;
import com.centegy.course_service.repository.CourseRepository;
import com.centegy.course_service.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courses")
@Slf4j
public class CourseController {

    private final CourseService courseService;

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CourseResponseDto>> createCourse(@Valid @RequestBody CourseRequestDto courseRequestDto) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Attempting to create course by instructor: {}", currentUsername);
        CourseResponseDto courseResponseDto = courseService.createCourse(courseRequestDto, currentUsername);
        log.info("Course created successfully: {}", courseResponseDto.getTitle());
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Course created successfully",
                        courseResponseDto
                )
        );
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponseDto>> updateCourse(@PathVariable Long id, @Valid @RequestBody CourseRequestDto courseRequestDto) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Attempting to update course by instructor: {}", currentUsername);
        CourseResponseDto courseResponseDto = courseService.updateCourse(id, courseRequestDto, currentUsername);
        log.info("Course updated successfully: {}", courseResponseDto.getTitle());
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Course updated successfully",
                        courseResponseDto
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CourseResponseDto>>> getAllCourses(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable
    )  {
        log.info("Attempting to get all courses");
        PageResponse<CourseResponseDto> pageResponse = courseService.getAllCourses(pageable);
        log.info("Courses retrieved successfully");
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Course retrieved successfully",
                        pageResponse
                )
        );

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponseDto>> getCourseById(@PathVariable Long id) {
        log.info("Attempting to get course by id: {}", id);
        CourseResponseDto courseResponseDto = courseService.getCourseById(id);
        log.info("Course retrieved successfully: {}", courseResponseDto.getTitle());
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Course retrieved successfully",
                        courseResponseDto
                )
        );
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCourseById(@PathVariable Long id) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Attempting to delete course by instructor: {}", currentUsername);
        courseService.deleteCourse(id,  currentUsername);
        log.info("Course deleted successfully: {}", id);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Course deleted successfully",
                        null
                )
        );
    }



}
