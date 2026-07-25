package com.centegy.enrollment_service.repository;

import com.centegy.enrollment_service.dto.response.EnrollmentResponseDto;
import com.centegy.enrollment_service.model.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudentUsername(String studentUsername);

    boolean existsByStudentUsernameAndCourseId(String studentUsername, Long courseId);

    Optional<Enrollment> findByIdAndStudentUsername(Long id, String studentUsername);

    List<Enrollment> findByCourseId(Long courseId);

    Optional<Enrollment> findByStudentUsernameAndCourseId(String studentUsername, Long courseId);

    @Query("SELECT new com.centegy.enrollment_service.dto.response.EnrollmentResponseDto(" +
            "e.id, e.courseId, e.studentUsername, e.enrollmentDate, e.status, e.progressPercentage) " +
            "FROM Enrollment e")
    Page<EnrollmentResponseDto> findAllAsDTOs(Pageable pageable);

    @Query("SELECT new com.centegy.enrollment_service.dto.response.EnrollmentResponseDto(" +
            "e.id, e.courseId, e.studentUsername, e.enrollmentDate, e.status, e.progressPercentage) " +
            "FROM Enrollment e WHERE e.courseId = :courseId")
    Page<EnrollmentResponseDto> findByCourseIdAsDTO(Long courseId, Pageable pageable);

    @Query("SELECT new com.centegy.enrollment_service.dto.response.EnrollmentResponseDto(" +
            "e.id, e.courseId, e.studentUsername, e.enrollmentDate, e.status, e.progressPercentage) " +
            "FROM Enrollment e WHERE e.studentUsername = :studentUsername")
    Page<EnrollmentResponseDto> findByStudentUsernameAsDTO(String studentUsername, Pageable pageable);

}
