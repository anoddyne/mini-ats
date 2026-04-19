package ru.practice.mini_ats.dto.Interview;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.practice.mini_ats.models.enums.InterviewStatus;

public record InterviewFeedbackDTO(
        @NotBlank
        String feedback,
        @NotNull
        InterviewStatus status
) {
}
