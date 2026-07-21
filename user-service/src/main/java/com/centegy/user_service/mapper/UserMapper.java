package com.centegy.user_service.mapper;

import com.centegy.user_service.dto.response.UserResponseDto;
import com.centegy.user_service.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "role.level", target = "role")
    UserResponseDto mapToUserResponseDto(User user);

}
