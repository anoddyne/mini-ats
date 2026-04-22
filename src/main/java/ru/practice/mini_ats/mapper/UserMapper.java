package ru.practice.mini_ats.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practice.mini_ats.dto.User.UserRequestDTO;
import ru.practice.mini_ats.dto.User.UserResponseDTO;
import ru.practice.mini_ats.models.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDTO toResponseDto(User user);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toEntity(UserRequestDTO dto);
}
