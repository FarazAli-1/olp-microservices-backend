package com.centegy.course_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponseDto implements Serializable {

    private Long id;
    private String title;
    private String description;
    private String instructorUsername;
    private BigDecimal price;

}
