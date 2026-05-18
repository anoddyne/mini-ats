package ru.practice.mini_ats.dto.Resume;


public record ResumeResponseDTO(
        Integer resumeId,
        String resumeFileUrl,
        String fileName,
        Integer userId
) {
}
