package ru.practice.mini_ats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practice.mini_ats.dto.Resume.ResumeResponseDTO;
import ru.practice.mini_ats.dto.ResumeReaction.ResumeReactionRequestDTO;
import ru.practice.mini_ats.dto.ResumeReaction.ResumeReactionResponseDTO;
import ru.practice.mini_ats.models.ResumeReaction;

@Mapper(componentModel = "spring")
public interface ResumeReactionMapper {

    @Mapping(source = "vacancy.vacancyId", target = "vacancyId")
    @Mapping(source = "vacancy.title", target = "vacancyTitle")
    @Mapping(source = "resume.resumeId", target = "resumeId")
    @Mapping(target = "companyName", source = "vacancy.company.name")
    @Mapping(source = "resume.user.fullName", target = "candidateFullName")
    ResumeReactionResponseDTO toResponseDto(ResumeReaction reaction);

    @Mapping(target = "resumeReactionId", ignore = true)
    @Mapping(target = "appliedAt", ignore = true)
    @Mapping(target = "vacancy", ignore = true)
    @Mapping(target = "resume", ignore = true)
    ResumeReaction toEntity(ResumeReactionRequestDTO dto);
}
