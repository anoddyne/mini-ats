package ru.practice.mini_ats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practice.mini_ats.dto.Interview.InterviewFeedbackDTO;
import ru.practice.mini_ats.dto.Interview.InterviewRequestDTO;
import ru.practice.mini_ats.dto.Interview.InterviewResponseDTO;
import ru.practice.mini_ats.models.Interview;

@Mapper(componentModel = "spring")
public interface InterviewMapper {
    @Mapping(source = "resumeReaction.resumeReactionId", target = "resumeReactionId")
    @Mapping(source = "resumeReaction.vacancy.title", target = "vacancyTitle")
    @Mapping(source = "resumeReaction.resume.user.fullName", target = "candidateFullName")
    @Mapping(source = "resumeReaction.vacancy.company.name", target = "companyName")
    InterviewResponseDTO toResponseDto(Interview interview);

    @Mapping(target = "interviewId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "feedback", ignore = true)
    @Mapping(target = "resumeReaction", ignore = true)
    Interview toEntity(InterviewRequestDTO dto);

    void updateEntityFromFeedback(InterviewFeedbackDTO dto, @MappingTarget Interview interview);
}
