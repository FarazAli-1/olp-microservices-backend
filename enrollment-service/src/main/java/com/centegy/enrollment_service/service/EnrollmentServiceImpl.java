package com.centegy.enrollment_service.service;

import com.centegy.common.dto.PageResponse;
import com.centegy.enrollment_service.dto.request.EnrollmentRequestDto;
import com.centegy.enrollment_service.dto.response.EnrollmentResponseDto;
import com.centegy.enrollment_service.mapper.EnrollmentMapper;
import com.centegy.enrollment_service.model.Enrollment;
import com.centegy.enrollment_service.model.enums.EnrollmentStatus;
import com.centegy.enrollment_service.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final WebClient.Builder webClientBuilder;
    private final EnrollmentMapper enrollmentMapper;

    @Override
    public EnrollmentResponseDto enrollInCourse(EnrollmentRequestDto enrollmentRequestDto, String studentUsername) {

        if (enrollmentRepository.existsByStudentUsernameAndCourseId(studentUsername, enrollmentRequestDto.getCourseId())) {
            throw new RuntimeException("Student is already enrolled in this course");
        }

        Boolean courseExists = webClientBuilder.build()
                .get()
                .uri("http://course-service/api/courses/{id}", enrollmentRequestDto.getCourseId())
                .retrieve()
                .bodyToMono(Object.class)
                .map(response -> true)
                .onErrorReturn(false)
                .block();

        if (Boolean.FALSE.equals(courseExists)) {
            throw new RuntimeException("Course not found with ID: " + enrollmentRequestDto.getCourseId());
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setCourseId(enrollmentRequestDto.getCourseId());
        enrollment.setStudentUsername(studentUsername);
        enrollment.setEnrollmentDate(LocalDateTime.now());
        enrollment.setStatus(EnrollmentStatus.ACTIVE);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        log.info("Student {} enrolled in course {}", studentUsername, enrollmentRequestDto.getCourseId());

        return enrollmentMapper.maptoEnrollmentResponseDto(savedEnrollment);
    }

    @Override
    public PageResponse<EnrollmentResponseDto> getStudentEnrollments(String studentUsername, Pageable pageable) {
        Page<EnrollmentResponseDto> pagedData = enrollmentRepository.findByStudentUsernameAsDTO(studentUsername, pageable);

        return new PageResponse<>(
                pagedData.getContent(),
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
    public void cancelEnrollment(Long enrollmentId, String studentUsername) {
        Enrollment enrollment = enrollmentRepository.findByIdAndStudentUsername(enrollmentId, studentUsername)
                .orElseThrow(() -> new RuntimeException("Enrollment record not found or access denied"));

        enrollment.setStatus(EnrollmentStatus.CANCELLED);
        enrollmentRepository.save(enrollment);
        log.info("Enrollment {} cancelled by {}", enrollmentId, studentUsername);
    }

    @Override
    public PageResponse<EnrollmentResponseDto> getCourseEnrollments(Long courseId, Pageable pageable) {

       Page<EnrollmentResponseDto> pagedData = enrollmentRepository.findByCourseIdAsDTO(courseId, pageable);

        return new PageResponse<>(
                pagedData.getContent(),
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
    public void completeCourseEnrollment(Long enrollmentId, String studentUsername) {
        Enrollment enrollment = enrollmentRepository.findByIdAndStudentUsername(enrollmentId, studentUsername)
                .orElseThrow(() -> new RuntimeException("Enrollment record not found or access denied"));
        enrollment.setStatus(EnrollmentStatus.COMPLETED);
        enrollmentRepository.save(enrollment);
        log.info("Enrollment {} completed by {}", enrollmentId, studentUsername);

    }

    @Override
    public void updateProgress(Long enrollmentId, String studentUsername, Double progressPercentage) {
        Enrollment enrollment = enrollmentRepository.findByIdAndStudentUsername(enrollmentId, studentUsername)
                .orElseThrow(() -> new RuntimeException("Enrollment record not found or access denied"));
        enrollment.setProgressPercentage(progressPercentage);
        if (progressPercentage >= 100.0) {
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
        }

        enrollmentRepository.save(enrollment);
        log.info("Enrollment {} progress updated to {}% by {}", enrollmentId, progressPercentage, studentUsername);

    }

    @Override
    public boolean checkEnrollmentStatus(Long courseId, String studentUsername) {
        return enrollmentRepository.existsByStudentUsernameAndCourseId(studentUsername, courseId);
    }

    @Override
    public PageResponse<EnrollmentResponseDto> getAllEnrollments(Pageable pageable) {

        Page<EnrollmentResponseDto> pagedData = enrollmentRepository.findAllAsDTOs(pageable);

        return new PageResponse<>(
                pagedData.getContent(),
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
}