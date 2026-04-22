package ru.practice.mini_ats.dto.ResumeReaction;

import java.time.LocalDate;

public record ResumeReactionResponseDTO(
        Integer resumeReactionId,
        String coverLetter,
        LocalDate appliedAt,
        Integer vacancyId,
        String vacancyTitle,
        Integer resumeId,
        String candidateFullName
) {
}
