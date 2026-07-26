package com.centegy.course_service.service;

import com.centegy.common.dto.PageResponse;
import com.centegy.course_service.dto.request.CourseRequestDto;
import com.centegy.course_service.dto.response.CourseResponseDto;
import org.springframework.data.domain.Pageable;

public interface CourseService {

    CourseResponseDto createCourse(CourseRequestDto courseRequestDto, String instructorUsername);

    CourseResponseDto updateCourse(Long id, CourseRequestDto courseRequestDto,  String instructorUsername);

    void deleteCourse(Long id,String instructorUsername);

    CourseResponseDto getCourseById(Long id);
    PageResponse<CourseResponseDto> getAllCourses(Pageable pageable);

}
