package ru.practice.mini_ats.mapper;

import org.junit.jupiter.api.Test;
import ru.practice.mini_ats.dto.Resume.ResumeRequestDTO;
import ru.practice.mini_ats.dto.Resume.ResumeResponseDTO;
import ru.practice.mini_ats.models.Resume;
import ru.practice.mini_ats.models.User;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ResumeMapperImplTest {

    private final ResumeMapper mapper = new ResumeMapperImpl();

    @Test
    void toResponseDto_shouldMapAllFields() {
        // given
        User user = new User();
        user.setUserId(42);
        user.setName("John");
        user.setSurname("Doe");
        user.setPatronymic("James");

        Resume resume = new Resume();
        resume.setResumeId(101);
        resume.setSummary("Experienced Java developer");
        resume.setEducation("Master's in CS");
        resume.setDesiredSalary(250_000);
        resume.setResumeFileUrl("https://example.com/resume.pdf");
        resume.setSkills(Map.of("Java", 5, "Spring", 4));
        resume.setExperience(Map.of("years", 5, "lastPosition", "Tech Lead"));
        resume.setUser(user);

        // when
        ResumeResponseDTO dto = mapper.toResponseDto(resume);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.resumeId()).isEqualTo(101);
        assertThat(dto.summary()).isEqualTo("Experienced Java developer");
        assertThat(dto.education()).isEqualTo("Master's in CS");
        assertThat(dto.desiredSalary()).isEqualTo(250_000);
        assertThat(dto.resumeFileUrl()).isEqualTo("https://example.com/resume.pdf");
        assertThat(dto.skills()).containsExactlyInAnyOrderEntriesOf(Map.of("Java", 5, "Spring", 4));
        assertThat(dto.experience()).containsExactlyInAnyOrderEntriesOf(Map.of("years", 5, "lastPosition", "Tech Lead"));
        assertThat(dto.userId()).isEqualTo(42);
        assertThat(dto.userFullName()).isEqualTo("Doe John James");
    }

    @Test
    void toResponseDto_shouldHandleNullUser() {
        // given
        Resume resume = new Resume();
        resume.setResumeId(1);
        resume.setSummary("No user");
        resume.setUser(null);

        // when
        ResumeResponseDTO dto = mapper.toResponseDto(resume);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.userId()).isNull();
        assertThat(dto.userFullName()).isNull();
    }

    @Test
    void toResponseDto_shouldHandleNullSkillsAndExperience() {
        // given
        Resume resume = new Resume();
        resume.setSkills(null);
        resume.setExperience(null);

        // when
        ResumeResponseDTO dto = mapper.toResponseDto(resume);

        // then
        assertThat(dto.skills()).isNull();
        assertThat(dto.experience()).isNull();
    }

    @Test
    void toResponseDto_shouldCopySkillsAndExperienceAsNewMapInstance() {
        // given
        Map<String, Object> originalSkills = new LinkedHashMap<>();
        originalSkills.put("key", "value");
        Map<String, Object> originalExp = new LinkedHashMap<>();
        originalExp.put("exp", 3);

        Resume resume = new Resume();
        resume.setSkills(originalSkills);
        resume.setExperience(originalExp);

        // when
        ResumeResponseDTO dto = mapper.toResponseDto(resume);
        Map<String, Object> copiedSkills = dto.skills();
        Map<String, Object> copiedExp = dto.experience();

        // then
        assertThat(copiedSkills).isNotSameAs(originalSkills);
        assertThat(copiedSkills).isEqualTo(originalSkills);
        assertThat(copiedExp).isNotSameAs(originalExp);
        assertThat(copiedExp).isEqualTo(originalExp);
    }

    @Test
    void toResponseDto_shouldHandleNullInput() {
        ResumeResponseDTO dto = mapper.toResponseDto(null);
        assertThat(dto).isNull();
    }

    @Test
    void toEntity_shouldMapAllFields() {
        // given
        ResumeRequestDTO request = new ResumeRequestDTO(
                "Skilled Python developer",
                "Bachelor's in IT",
                180_000,
                "https://example.com/cv.pdf",
                Map.of("Python", 5, "Django", 4),
                Map.of("previousJob", "Senior Dev")
        );

        // when
        Resume entity = mapper.toEntity(request);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getResumeId()).isNull(); // ID не маппится
        assertThat(entity.getSummary()).isEqualTo("Skilled Python developer");
        assertThat(entity.getEducation()).isEqualTo("Bachelor's in IT");
        assertThat(entity.getDesiredSalary()).isEqualTo(180_000);
        assertThat(entity.getResumeFileUrl()).isEqualTo("https://example.com/cv.pdf");
        assertThat(entity.getSkills()).containsExactlyInAnyOrderEntriesOf(Map.of("Python", 5, "Django", 4));
        assertThat(entity.getExperience()).containsExactlyInAnyOrderEntriesOf(Map.of("previousJob", "Senior Dev"));
        assertThat(entity.getUser()).isNull(); // связь не устанавливается маппером
    }

    @Test
    void toEntity_shouldHandleNullInput() {
        Resume entity = mapper.toEntity(null);
        assertThat(entity).isNull();
    }

    @Test
    void toEntity_shouldHandleNullOptionalFields() {
        // given
        ResumeRequestDTO minimal = new ResumeRequestDTO(
                "Minimal summary", null, null, null, null, null
        );

        // when
        Resume entity = mapper.toEntity(minimal);

        // then
        assertThat(entity.getSummary()).isEqualTo("Minimal summary");
        assertThat(entity.getEducation()).isNull();
        assertThat(entity.getDesiredSalary()).isNull();
        assertThat(entity.getResumeFileUrl()).isNull();
        assertThat(entity.getSkills()).isNull();
        assertThat(entity.getExperience()).isNull();
    }

    @Test
    void toEntity_shouldCopySkillsAndExperienceAsNewMapInstance() {
        // given
        Map<String, Object> originalSkills = Map.of("skill", 1);
        Map<String, Object> originalExp = Map.of("exp", 2);
        ResumeRequestDTO request = new ResumeRequestDTO(
                "Title", null, null, null, originalSkills, originalExp
        );

        // when
        Resume entity = mapper.toEntity(request);
        Map<String, Object> copiedSkills = entity.getSkills();
        Map<String, Object> copiedExp = entity.getExperience();

        // then
        assertThat(copiedSkills).isNotSameAs(originalSkills);
        assertThat(copiedSkills).isEqualTo(originalSkills);
        assertThat(copiedExp).isNotSameAs(originalExp);
        assertThat(copiedExp).isEqualTo(originalExp);
    }
}