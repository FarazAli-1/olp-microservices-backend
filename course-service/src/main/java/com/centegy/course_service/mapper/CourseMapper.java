package com.centegy.course_service.mapper;


import com.centegy.course_service.dto.request.CourseRequestDto;
import com.centegy.course_service.dto.response.CourseResponseDto;
import com.centegy.course_service.model.Course;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CourseMapper {

    Course mapToCourse(CourseRequestDto courseRequestDto);

    CourseResponseDto mapToCourseResponseDto(Course course);

}