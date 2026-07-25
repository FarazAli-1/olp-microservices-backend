package com.centegy.enrollment_service.service;

import com.centegy.common.dto.PageResponse;
import com.centegy.enrollment_service.dto.request.EnrollmentRequestDto;
import com.centegy.enrollment_service.dto.response.EnrollmentResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EnrollmentService {

    EnrollmentResponseDto enrollInCourse(EnrollmentRequestDto requestDto, String studentUsername);

    PageResponse<EnrollmentResponseDto> getStudentEnrollments(String studentUsername, Pageable pageable);

    void cancelEnrollment(Long enrollmentId, String studentUsername);

    PageResponse<EnrollmentResponseDto> getCourseEnrollments(Long courseId,Pageable pageable);

    void completeCourseEnrollment(Long enrollmentId, String studentUsername);

    void updateProgress(Long enrollmentId, String studentUsername, Double progressPercentage);

   boolean checkEnrollmentStatus(Long courseId, String studentUsername);

   PageResponse<EnrollmentResponseDto> getAllEnrollments(Pageable pageable);
}
