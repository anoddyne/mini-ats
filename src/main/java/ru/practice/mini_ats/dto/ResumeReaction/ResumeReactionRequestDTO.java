package ru.practice.mini_ats.dto.ResumeReaction;

import jakarta.validation.constraints.NotNull;

public record ResumeReactionRequestDTO(
        String coverLetter,

        @NotNull(message = "Выберите вакансию")
        Integer vacancyId

//        @NotNull(message = "Выберите резюме")
//        Integer resumeId
) {
}
