package ru.practice.mini_ats.dto.Interview;

import ru.practice.mini_ats.models.ResumeReaction;
import ru.practice.mini_ats.models.enums.InterviewStatus;
import ru.practice.mini_ats.models.enums.InterviewType;

import java.time.LocalDate;

public record InterviewResponseDTO(
        Integer interviewId,
        LocalDate date,
        InterviewType type,
        InterviewStatus status,
        String feedback,

        Integer resumeReactionId,
        String vacancyTitle,
        String candidateFullName,
        String companyName
) {
}
