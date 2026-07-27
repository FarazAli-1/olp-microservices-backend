package com.centegy.course_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequestDto {

    @NotBlank(message = "Course title cannot be blank")
    private String title;

    @NotBlank(message = "Course description cannot be blank")
    private String description;

    @Min(value = 0, message = "Price cannot be a negative value")
    private BigDecimal price;
}
