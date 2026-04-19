package ru.practice.mini_ats.dto.Resume;


import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.util.Map;

public record ResumeRequestDTO(
        @Size(max = 2000, message = "Текст не может быть больше 2000 символов")
        String summary,
        String education,

        @PositiveOrZero(message = "Желаемая зарплата не может быть отрицательной")
        Integer desiredSalary,

        @URL(message = "Неверный формат ссылки")
        String resumeFileUrl,
        Map<String, Object> skills,
        Map<String, Object> experience
) {
}
