package ru.practice.mini_ats.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practice.mini_ats.dto.Resume.ResumeResponseDTO;
import ru.practice.mini_ats.models.Resume;
import ru.practice.mini_ats.models.User;

import static org.assertj.core.api.Assertions.assertThat;

class ResumeMapperImplTest {

    private final ResumeMapper mapper = Mappers.getMapper(ResumeMapper.class);

    @Test
    void toResponseDto_ShouldMapAllFields() {
        User user = new User();
        user.setUserId(42);
        user.setName("John"); // не важно для маппера

        Resume resume = new Resume();
        resume.setResumeId(1);
        resume.setResumeFileUrl("https://minio.local/resumes/file.pdf");
        resume.setFileName("file.pdf");
        resume.setUser(user);

        ResumeResponseDTO dto = mapper.toResponseDto(resume);

        assertThat(dto.resumeId()).isEqualTo(1);
        assertThat(dto.resumeFileUrl()).isEqualTo("https://minio.local/resumes/file.pdf");
        assertThat(dto.fileName()).isEqualTo("file.pdf");
        assertThat(dto.userId()).isEqualTo(42);
    }

    @Test
    void toResponseDto_ShouldHandleNullOptionalFields() {
        User user = new User();
        user.setUserId(10);

        Resume resume = new Resume();
        resume.setResumeId(2);
        resume.setUser(user);
        // resumeFileUrl и fileName могут быть null

        ResumeResponseDTO dto = mapper.toResponseDto(resume);

        assertThat(dto.resumeId()).isEqualTo(2);
        assertThat(dto.resumeFileUrl()).isNull();
        assertThat(dto.fileName()).isNull();
        assertThat(dto.userId()).isEqualTo(10);
    }

    @Test
    void toResponseDto_ShouldMapUserIdEvenIfOtherUserFieldsAreNull() {
        User user = new User();
        user.setUserId(99);
        // User может иметь null в других полях, это не влияет

        Resume resume = new Resume();
        resume.setResumeId(3);
        resume.setResumeFileUrl("url");
        resume.setFileName("name.pdf");
        resume.setUser(user);

        ResumeResponseDTO dto = mapper.toResponseDto(resume);

        assertThat(dto.userId()).isEqualTo(99);
        assertThat(dto.resumeId()).isEqualTo(3);
        assertThat(dto.resumeFileUrl()).isEqualTo("url");
        assertThat(dto.fileName()).isEqualTo("name.pdf");
    }
}