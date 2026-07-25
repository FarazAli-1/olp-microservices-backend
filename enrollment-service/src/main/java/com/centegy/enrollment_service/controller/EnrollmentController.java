package com.centegy.enrollment_service.controller;

import com.centegy.common.dto.ApiResponse;
import com.centegy.common.dto.PageResponse;
import com.centegy.enrollment_service.dto.request.EnrollmentRequestDto;
import com.centegy.enrollment_service.dto.request.UpdateProgressRequestDto;
import com.centegy.enrollment_service.dto.response.EnrollmentResponseDto;
import com.centegy.enrollment_service.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@Slf4j
public class EnrollmentController {

    private final EnrollmentService  enrollmentService;

    @PostMapping("/enroll")
    public ResponseEntity<ApiResponse<EnrollmentResponseDto>> enrollInCourse(@Valid @RequestBody EnrollmentRequestDto enrollmentRequestDto) {
        log.info("Attempting to create enrollment");
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        EnrollmentResponseDto responseDto =  enrollmentService.enrollInCourse(enrollmentRequestDto,currentUsername);
        log.info("Successfully enrolled in course: {}", responseDto.getCourseId());
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Successfully enrolled in course",
                        responseDto
                )
        );
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelEnrollment(@PathVariable Long id){
        log.info("Attempting to cancel enrollment");
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        enrollmentService.cancelEnrollment(id,currentUsername);
        log.info("Successfully cancelled enrollment");
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Successfully cancelled enrollment",
                        null
                )
        );
    }

    @PutMapping("/{id}/progress")
    public ResponseEntity<ApiResponse<Void>> updateProgress(
            @PathVariable Long id, @Valid @RequestBody UpdateProgressRequestDto progressDto) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        enrollmentService.updateProgress(id, currentUsername, progressDto.getProgressPercentage());
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Successfully updated progress",
                        null
                )
        );
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<Void>> completeCourse(@PathVariable Long id) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        enrollmentService.completeCourseEnrollment(id, currentUsername);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Successfully completed course",
                        null
                )
        );
    }
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<EnrollmentResponseDto>>> getAllEnrollments(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable
    ) {
        log.info("Admin fetching all enrollments");
        PageResponse<EnrollmentResponseDto> response = enrollmentService.getAllEnrollments(pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "All enrollments retrieved", response));
    }

    @GetMapping("/my-enrollments")
    public ResponseEntity<ApiResponse<PageResponse<EnrollmentResponseDto>>> getMyEnrollments(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "enrollmentDate",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ){
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        PageResponse<EnrollmentResponseDto> response = enrollmentService.getStudentEnrollments(currentUsername, pageable);
        log.info("Successfully fetched enrollments");
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Successfully retrieved enrollment",
                        response
                )
        );
    }

    @GetMapping("/course/{id}")
    public ResponseEntity<ApiResponse<PageResponse<EnrollmentResponseDto>>>  getEnrollmentByCourse(
            @PathVariable Long id,
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "enrollmentDate",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ){
        log.info("Attempting to get enrollment by course: {}", id);
        PageResponse<EnrollmentResponseDto> response = enrollmentService.getCourseEnrollments(id,pageable);
        log.info("Successfully retrieved enrollment by course: {}", id);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Successfully retrieved enrollment by course",
                        response
                )
        );
    }

    @GetMapping("/status/{courseId}")
    public ResponseEntity<ApiResponse<Boolean>> checkStatus(@PathVariable Long courseId) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isEnrolled = enrollmentService.checkEnrollmentStatus(courseId, currentUsername);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Successfully check status of enrollment",
                        isEnrolled
                )
        );

    }


}
