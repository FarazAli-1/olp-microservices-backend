package com.centegy.enrollment_service.dto.response;

import com.centegy.enrollment_service.model.enums.EnrollmentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponseDto {

    private Long id;
    private Long courseId;
    private String studentUsername;
    private LocalDateTime enrollmentDate;
    private EnrollmentStatus status;
    private Double progressPercentage;

}