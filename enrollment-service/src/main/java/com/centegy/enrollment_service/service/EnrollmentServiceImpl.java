package com.centegy.enrollment_service.service;

import com.centegy.common.dto.NotificationEventDto;
import com.centegy.common.dto.PageResponse;
import com.centegy.common.enums.NotificationType;
import com.centegy.enrollment_service.dto.request.EnrollmentRequestDto;
import com.centegy.enrollment_service.dto.response.EnrollmentResponseDto;
import com.centegy.enrollment_service.dto.response.UserResponseDto;
import com.centegy.enrollment_service.mapper.EnrollmentMapper;
import com.centegy.enrollment_service.model.Enrollment;
import com.centegy.enrollment_service.model.enums.EnrollmentStatus;
import com.centegy.enrollment_service.repository.EnrollmentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final WebClient.Builder webClientBuilder;
    private final EnrollmentMapper enrollmentMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    @CacheEvict(value = {"studentEnrollments", "courseEnrollments", "allEnrollments"}, allEntries = true)
    public EnrollmentResponseDto enrollInCourse(EnrollmentRequestDto enrollmentRequestDto, String studentUsername) {
        if (enrollmentRepository.existsByStudentUsernameAndCourseId(studentUsername, enrollmentRequestDto.getCourseId())) {
            throw new RuntimeException("Student is already enrolled in this course");
        }

        String actualEmail = fetchUserEmailFallback(studentUsername);
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

        sendNotification(studentUsername, actualEmail, NotificationType.ENROLLMENT_SUCCESS,
                "You have successfully enrolled in course ID: " + enrollmentRequestDto.getCourseId());

        return enrollmentMapper.maptoEnrollmentResponseDto(savedEnrollment);
    }

    @Override
    @Cacheable(value = "studentEnrollments", key = "#studentUsername + '-' + #pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort")
    public PageResponse<EnrollmentResponseDto> getStudentEnrollments(String studentUsername, Pageable pageable) {
        Page<EnrollmentResponseDto> pagedData = enrollmentRepository.findByStudentUsernameAsDTO(studentUsername, pageable);
        return mapToPageResponse(pagedData);
    }

    @Override
    @CacheEvict(value = {"studentEnrollments", "courseEnrollments", "allEnrollments"}, allEntries = true)
    public void cancelEnrollment(Long enrollmentId, String studentUsername) {
        Enrollment enrollment = enrollmentRepository.findByIdAndStudentUsername(enrollmentId, studentUsername)
                .orElseThrow(() -> new RuntimeException("Enrollment record not found or access denied"));

        enrollment.setStatus(EnrollmentStatus.CANCELLED);
        enrollmentRepository.save(enrollment);

        String actualEmail = fetchUserEmailFallback(studentUsername);
        sendNotification(studentUsername, actualEmail, NotificationType.ENROLLMENT_CANCELLED,
                "You have successfully cancelled enrollment ID: " + enrollmentId);
    }

    @Override
    @Cacheable(value = "courseEnrollments", key = "#courseId + '-' + #pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort")
    public PageResponse<EnrollmentResponseDto> getCourseEnrollments(Long courseId, Pageable pageable) {
        Page<EnrollmentResponseDto> pagedData = enrollmentRepository.findByCourseIdAsDTO(courseId, pageable);
        return mapToPageResponse(pagedData);
    }

    @Override
    @CacheEvict(value = {"studentEnrollments", "courseEnrollments", "allEnrollments"}, allEntries = true)
    public void completeCourseEnrollment(Long enrollmentId, String studentUsername) {
        Enrollment enrollment = enrollmentRepository.findByIdAndStudentUsername(enrollmentId, studentUsername)
                .orElseThrow(() -> new RuntimeException("Enrollment record not found or access denied"));

        enrollment.setStatus(EnrollmentStatus.COMPLETED);
        enrollmentRepository.save(enrollment);

        String actualEmail = fetchUserEmailFallback(studentUsername);
        sendNotification(studentUsername, actualEmail, NotificationType.COURSE_COMPLETED,
                "You have successfully completed the enrollment of course ID: " + enrollmentId);
    }

    @Override
    @CacheEvict(value = {"studentEnrollments", "courseEnrollments", "allEnrollments"}, allEntries = true)
    public void updateProgress(Long enrollmentId, String studentUsername, Double progressPercentage) {
        Enrollment enrollment = enrollmentRepository.findByIdAndStudentUsername(enrollmentId, studentUsername)
                .orElseThrow(() -> new RuntimeException("Enrollment record not found or access denied"));

        enrollment.setProgressPercentage(progressPercentage);
        if (progressPercentage >= 100.0) {
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
        }
        enrollmentRepository.save(enrollment);

        String actualEmail = fetchUserEmailFallback(studentUsername);
        sendNotification(studentUsername, actualEmail, NotificationType.PROGRESS_UPDATED,
                "You have successfully updated the enrollment of course ID: " + enrollmentId);
    }

    @Override
    public boolean checkEnrollmentStatus(Long courseId, String studentUsername) {
        return enrollmentRepository.existsByStudentUsernameAndCourseId(studentUsername, courseId);
    }

    @Override
    @Cacheable(value = "allEnrollments", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort")
    public PageResponse<EnrollmentResponseDto> getAllEnrollments(Pageable pageable) {
        Page<EnrollmentResponseDto> pagedData = enrollmentRepository.findAllAsDTOs(pageable);
        return mapToPageResponse(pagedData);
    }

    private void sendNotification(String username, String email, NotificationType type, String message) {
        try {
            NotificationEventDto event = NotificationEventDto.builder()
                    .recipientId(username)
                    .recipientEmail(email)
                    .notificationType(type)
                    .message(message)
                    .build();
            String jsonPayload = objectMapper.writeValueAsString(event);
            stringRedisTemplate.opsForList().leftPush("notificationQueue", jsonPayload);
        } catch (Exception e) {
            log.error("Could not send notification to Redis", e);
        }
    }

    private PageResponse<EnrollmentResponseDto> mapToPageResponse(Page<EnrollmentResponseDto> pagedData) {
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

    private String fetchUserEmailFallback(String username) {
        try {
            UserResponseDto userResponseDto = webClientBuilder.build()
                    .get()
                    .uri("http://user-service/api/users/{username}", username)
                    .header(HttpHeaders.AUTHORIZATION, getCurrentUserToken())
                    .retrieve()
                    .bodyToMono(UserResponseDto.class)
                    .block();
            if (userResponseDto != null && userResponseDto.getEmail() != null) {
                return userResponseDto.getEmail();
            }
        } catch (Exception e) {
            log.warn("Could not fetch email for {}. Falling back to username.", username);
        }
        return username;
    }

    private String getCurrentUserToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
        }
        return null;
    }
}