package com.centegy.course_service.service;

import com.centegy.common.dto.PageResponse;
import com.centegy.course_service.dto.request.CourseRequestDto;
import com.centegy.course_service.dto.response.CourseResponseDto;
import com.centegy.course_service.mapper.CourseMapper;
import com.centegy.course_service.model.Course;
import com.centegy.course_service.repository.CourseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService{

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    @Override
    @CacheEvict(value = "coursesList", allEntries = true)
    public CourseResponseDto createCourse(CourseRequestDto courseRequestDto, String instructorUsername) {

        Course course = courseMapper.mapToCourse(courseRequestDto);
        course.setInstructorUsername(instructorUsername);

        Course savedCourse = courseRepository.save(course);
        return courseMapper.mapToCourseResponseDto(savedCourse);

    }

    @Override
    @CachePut(value = "course", key = "#id")
    @CacheEvict(value = "coursesList", allEntries = true)
    public CourseResponseDto updateCourse(Long id, CourseRequestDto courseRequestDto, String instructorUsername) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Course not Found"));
        if (!course.getInstructorUsername().equals(instructorUsername)) {
            throw new RuntimeException("Unauthorized: You do not own this course");
        }
        course.setDescription(courseRequestDto.getDescription());
        course.setTitle(courseRequestDto.getTitle());
        course.setPrice(courseRequestDto.getPrice());
        Course savedCourse = courseRepository.save(course);

        return  courseMapper.mapToCourseResponseDto(savedCourse);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "course", key = "#id"),
            @CacheEvict(value = "coursesList", allEntries = true)
    })
    public void deleteCourse(Long id, String instructorUsername) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Course not Found"));
        if (!course.getInstructorUsername().equals(instructorUsername)) {
            throw new RuntimeException("Unauthorized: You do not own this course");
        }

        courseRepository.delete(course);
    }

    @Override
    @Cacheable(value = "course", key = "#id")
    public CourseResponseDto getCourseById(Long id) {
        Course course =  courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not Found"));

        return courseMapper.mapToCourseResponseDto(course);
    }

    @Override
    @Cacheable(
            value = "coursesList",
            key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort"
    )
    public PageResponse<CourseResponseDto> getAllCourses(Pageable  pageable) {

        Page<Course> pagedData = courseRepository.findAll(pageable);
        List<CourseResponseDto> content = pagedData.getContent().stream()
                .map(courseMapper::mapToCourseResponseDto)
                .toList();

        return new PageResponse<>(
                content,
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
