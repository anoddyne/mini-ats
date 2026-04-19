package ru.practice.mini_ats.dto.Interview;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import ru.practice.mini_ats.models.enums.InterviewType;

import java.time.LocalDate;

public record InterviewRequestDTO(
        @NotNull(message = "Дата обязательна")
        @Future(message = "Интервью нельзя назначить в прошлом")
        LocalDate date,

        @NotNull(message = "Укажите тип интервью")
        InterviewType type,

        @NotNull
        Integer resumeReactionId
) {
}
