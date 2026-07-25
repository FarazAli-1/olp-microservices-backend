package com.centegy.enrollment_service.mapper;

import com.centegy.enrollment_service.dto.request.EnrollmentRequestDto;
import com.centegy.enrollment_service.dto.response.EnrollmentResponseDto;
import com.centegy.enrollment_service.model.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EnrollmentMapper {

    Enrollment maptoEnrollment(EnrollmentRequestDto  enrollmentRequestDto);

    EnrollmentResponseDto  maptoEnrollmentResponseDto(Enrollment enrollment);



}
