package ru.practice.mini_ats.dto.Resume;

import java.util.Map;

public record ResumeResponseDTO(
        Integer resumeId,
        String summary,
        String education,
        Integer desiredSalary,
        String resumeFileUrl,
        String skills,
        String experience,
        Integer userId,
        String userFullName
) {
}
